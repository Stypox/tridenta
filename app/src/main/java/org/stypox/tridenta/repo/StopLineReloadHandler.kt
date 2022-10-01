package org.stypox.tridenta.repo

import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.stypox.tridenta.db.*
import org.stypox.tridenta.db.data.DbLine
import org.stypox.tridenta.db.data.DbNewsItem
import org.stypox.tridenta.db.data.DbStop
import org.stypox.tridenta.db.data.DbStopLineJoin
import org.stypox.tridenta.extractor.Extractor
import org.stypox.tridenta.extractor.data.ExLine
import org.stypox.tridenta.extractor.data.ExStop
import org.stypox.tridenta.util.PreferenceKeys
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StopLineReloadHandler @Inject constructor(
    private val prefs: SharedPreferences,
    private val extractor: Extractor,
    private val appDatabase: AppDatabase,
    private val stopDao: StopDao,
    private val lineDao: LineDao,
) {

    fun <R> runAndReloadIfNeeded(function: () -> R?): R {
        val lastReloadSeconds = prefs.getLong(PreferenceKeys.LAST_STOP_LINE_RELOAD_SECONDS, 0)
        val nowSeconds = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        val secondsSinceLastReload = nowSeconds - lastReloadSeconds

        fun reloadStoreRun(): R {
            Log.d(TAG, "Reloading lines and stops from network")
            reloadFromNetwork()
            prefs.edit().putLong(PreferenceKeys.LAST_STOP_LINE_RELOAD_SECONDS, nowSeconds).commit()
            return function()!!
        }

        // also check `secondsSinceLastReload < 0` just to be sure
        if (secondsSinceLastReload < 0 ||
            secondsSinceLastReload >= NORMAL_RELOAD_INTERVAL_SECONDS) {
            // some time has passed, so reload data to make sure it is up to date
            reloadStoreRun()
        }

        try {
            // data is (probably) up-to-date, so run the function directly
            return function()!!
        } catch (e: Exception) {
            if (secondsSinceLastReload >= ERROR_RELOAD_INTERVAL_SECONDS) {
                // if there was an error while running the function, try reloading only if enough
                // time has passed since the last reload
                return reloadStoreRun()
            }
            throw e
        }
    }

    private fun reloadFromNetwork() {
        lateinit var exLines: List<ExLine>
        lateinit var exStops: List<ExStop>
        runBlocking {
            coroutineScope {
                awaitAll(async {
                    exLines = extractor.getLines()
                }, async {
                    exStops = extractor.getStops()
                })
            }
        }

        appDatabase.runInTransaction {
            // first delete all data from the previous network fetch
            stopDao.deleteAllDbStopLineJoins()
            lineDao.deleteAllDbNewsItems()
            stopDao.deleteAllDbStops()
            lineDao.deleteAllDbLines()

            // then insert the new data
            // start from lines, since they are a dependency of news items and stop line joins
            lineDao.insertDbLines(
                exLines.map { exLine ->
                    DbLine(
                        lineId = exLine.lineId,
                        type = exLine.type,
                        area = exLine.area,
                        color = exLine.color,
                        longName = exLine.longName,
                        shortName = exLine.shortName
                    )
                }
            )

            // then insert news items, which only depend on lines
            lineDao.insertDbNewsItems(
                exLines.flatMap { exLine ->
                    exLine.newsItems.map { exNewsItem ->
                        DbNewsItem(
                            serviceType = exNewsItem.serviceType,
                            startDate = exNewsItem.startDate,
                            endDate = exNewsItem.endDate,
                            header = exNewsItem.header,
                            details = exNewsItem.details,
                            url = exNewsItem.url,
                            lineId = exLine.lineId,
                            lineType = exLine.type
                        )
                    }
                }
            )

            // then insert stops, which are a dependency of stop line joins
            stopDao.insertDbStops(
                exStops.map { exStop ->
                    DbStop(
                        stopId = exStop.stopId,
                        type = exStop.type,
                        latitude = exStop.latitude,
                        longitude = exStop.longitude,
                        name = exStop.name,
                        street = exStop.street,
                        town = exStop.town,
                        wheelchairAccessible = exStop.wheelchairAccessible
                    )
                }
            )

            // and finally insert stop line joins, which depend on stops and lines
            stopDao.insertDbStopLineJoins(
                exStops.flatMap { exStop ->
                    exStop.lines.map { exLine ->
                        DbStopLineJoin(
                            stopId = exStop.stopId,
                            stopType = exStop.type,
                            lineId = exLine.lineId,
                            lineType = exLine.type
                        )
                    }
                }
            )
        }
    }

    companion object {
        private const val NORMAL_RELOAD_INTERVAL_SECONDS = 604800L // one week
        private const val ERROR_RELOAD_INTERVAL_SECONDS = 86400L // one day
        private val TAG = StopLineReloadHandler::class.simpleName!!
    }
}