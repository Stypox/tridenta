package org.stypox.tridenta.repo

import android.content.SharedPreferences
import kotlinx.coroutines.*
import org.stypox.tridenta.db.AppDatabase
import org.stypox.tridenta.db.LineDao
import org.stypox.tridenta.db.StopDao
import org.stypox.tridenta.db.data.DbLine
import org.stypox.tridenta.db.data.DbNewsItem
import org.stypox.tridenta.db.data.DbStop
import org.stypox.tridenta.db.data.DbStopLineJoin
import org.stypox.tridenta.enums.CardinalPoint
import org.stypox.tridenta.extractor.Extractor
import org.stypox.tridenta.extractor.data.ExLine
import org.stypox.tridenta.extractor.data.ExStop
import org.stypox.tridenta.log.logError
import org.stypox.tridenta.log.logInfo
import org.stypox.tridenta.log.logWarning
import org.stypox.tridenta.util.PreferenceKeys
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.PI
import kotlin.math.atan2
import androidx.core.content.edit

@Singleton
class StopLineReloadHandler @Inject constructor(
    private val prefs: SharedPreferences,
    private val extractor: Extractor,
    private val appDatabase: AppDatabase,
    private val stopDao: StopDao,
    private val lineDao: LineDao,
) {
    private var job: Job? = null

    fun <R> reloadIfNeededAndRun(forceReload: Boolean = false, function: () -> R): R {
        if (forceReload) {
            logInfo("Reloading lines and stops as requested by the user")
            return reloadAndRun(function)
        }

        val secondsSinceLastReload = getSecondsSinceLastReload()

        // also check `secondsSinceLastReload < 0` just to be sure
        if (secondsSinceLastReload < 0 ||
            secondsSinceLastReload >= LONG_TIME_RELOAD_INTERVAL_SECONDS) {
            // a long time has passed, data is probably outdated, so reload it before even trying to
            // run the function (it might be the same time the user opens the app)
            logInfo(
                "Reloading lines and stops without first checking for errors "
                        + "because a long time has passed since the last reload"
            )
            return reloadAndRun(function)
        }

        try {
            // data is (probably) up-to-date, so run the function directly
            return function().run {
                if (this == null && secondsSinceLastReload >= ERROR_RELOAD_INTERVAL_SECONDS) {
                    // if the function returned null, try to reload (without throwing errors)
                    logWarning("Reloading lines and stops because null was returned")
                    reloadAndRun(function)
                } else {
                    this
                }
            }
        } catch (e: Throwable) {
            if (secondsSinceLastReload >= ERROR_RELOAD_INTERVAL_SECONDS) {
                // if there was an error while running the function, try reloading only if enough
                // time has passed since the last reload
                logWarning("Reloading lines and stops because of error while operating on them", e)
                return reloadAndRun(function)
            }

            logError("Crash while operating on lines or stops even if data is up-to-date", e)
            throw e
        }
    }

    fun reloadIfOutdatedData() {
        if (getSecondsSinceLastReload() >= NORMAL_RELOAD_INTERVAL_SECONDS) {
            // some time has passed, so reload data to make sure it is up to date
            logInfo("Reloading lines and stops on app start "
                + "because some time has passed since the last reload")
            launchReloadFromNetworkJob()
        }
    }

    private fun getSecondsSinceLastReload(): Long {
        val lastReloadSeconds = prefs.getLong(PreferenceKeys.LAST_STOP_LINE_RELOAD_SECONDS, 0)
        val nowSeconds = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        return nowSeconds - lastReloadSeconds
    }

    private fun <R> reloadAndRun(function: () -> R): R {
        launchReloadFromNetworkJob()
        return function()
    }

    private fun launchReloadFromNetworkJob() {
        if (job?.isActive != true) {
            logInfo("Running lines and stops reloading job")
            runBlocking {
                job = launch {
                    try {
                        reloadFromNetwork()
                    } catch (e: Throwable) {
                        logError("Could not reload stops and lines", e)
                    }
                }
                job?.join()
            }
        } else {
            logInfo("Lines and stops reloading job is already running")
            runBlocking {
                job?.join()
            }
        }
    }

    private fun reloadFromNetwork() {
        lateinit var exLines: List<ExLine>
        lateinit var exStops: List<ExStop>
        runBlocking {
            launch {
                awaitAll(async {
                    exLines = extractor.getLines()
                }, async {
                    exStops = extractor.getStops()
                })
            }
        }

        val nameToCoordinates = mutableMapOf<String, MutableList<Pair<Double, Double>>>()
        exStops.forEach {
            val normalizedName = normalizeStopName(it)
            val list = nameToCoordinates[normalizedName]
            if (list == null) {
                nameToCoordinates[normalizedName] = mutableListOf(Pair(it.latitude, it.longitude))
            } else {
                list.add(Pair(it.latitude, it.longitude))
            }
        }

        val nameToAverageCoordinates = mutableMapOf<String, Pair<Double, Double>>()
        nameToCoordinates.forEach { (name, list) ->
            if (list.isNotEmpty()) {
                nameToAverageCoordinates[name] = list
                    .reduce { acc, pair -> Pair(acc.first + pair.first, acc.second + pair.second) }
                    .let { Pair(it.first / list.size, it.second / list.size) }
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
                    var cardinalPoint: CardinalPoint? = null
                    val averageCoordinates = nameToAverageCoordinates[normalizeStopName(exStop)]
                    if (averageCoordinates != null &&
                        (averageCoordinates.first != exStop.latitude ||
                                averageCoordinates.second != exStop.longitude)
                    ) {
                        val angle = atan2(
                            exStop.latitude - averageCoordinates.first,
                            exStop.longitude - averageCoordinates.second,
                        )
                        cardinalPoint = when (angle){
                            in (-PI*1/8..<+PI*1/8) -> CardinalPoint.East
                            in (+PI*1/8..<+PI*3/8) -> CardinalPoint.NorthEast
                            in (+PI*3/8..<+PI*5/8) -> CardinalPoint.North
                            in (+PI*5/8..<+PI*7/8) -> CardinalPoint.NorthWest
                            // west has an angle >= +PI*7/8 or < -PI*7/8
                            in (-PI*7/8..<-PI*5/8) -> CardinalPoint.SouthWest
                            in (-PI*5/8..<-PI*3/8) -> CardinalPoint.South
                            in (-PI*3/8..<-PI*1/8) -> CardinalPoint.SouthEast
                            else -> CardinalPoint.West
                        }
                    }

                    DbStop(
                        stopId = exStop.stopId,
                        type = exStop.type,
                        latitude = exStop.latitude,
                        longitude = exStop.longitude,
                        name = exStop.name,
                        street = exStop.street,
                        town = exStop.town,
                        wheelchairAccessible = exStop.wheelchairAccessible,
                        cardinalPoint = cardinalPoint,
                    )
                }
            )

            // and finally insert stop line joins, which depend on stops and lines
            stopDao.insertDbStopLineJoins(
                exStops.flatMap { exStop ->
                    exStop.lines.map { (lineId, lineType) ->
                        DbStopLineJoin(
                            stopId = exStop.stopId,
                            stopType = exStop.type,
                            lineId = lineId,
                            lineType = lineType
                        )
                    }
                }
            )
        }

        // once reloading succeeds, store the last reload time
        prefs.edit {
            putLong(
                PreferenceKeys.LAST_STOP_LINE_RELOAD_SECONDS,
                LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
            )
        }
    }

    private fun normalizeStopName(stop: ExStop): String {
        return stop.type.value + stop.name.lowercase().replace(NORMALIZE_STOP_NAME_REGEX, "")
    }

    companion object {
        private const val LONG_TIME_RELOAD_INTERVAL_SECONDS = 15811200L // six months
        private const val NORMAL_RELOAD_INTERVAL_SECONDS = 604800L // one week
        private const val ERROR_RELOAD_INTERVAL_SECONDS = 86400L // one day
        private val NORMALIZE_STOP_NAME_REGEX = Regex("[^a-z0-9]")
    }
}