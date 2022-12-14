package org.stypox.tridenta.repo

import org.stypox.tridenta.db.StopDao
import org.stypox.tridenta.db.data.DbStop
import org.stypox.tridenta.enums.StopLineType
import org.stypox.tridenta.repo.data.UiStop
import org.stypox.tridenta.repo.data.lineShortNameComparator
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StopsRepository @Inject constructor(
    private val stopLineReloadHandler: StopLineReloadHandler,
    private val stopDao: StopDao
) {
    fun getDbStop(stopId: Int, stopType: StopLineType): DbStop? {
        return stopLineReloadHandler.reloadIfNeededAndRun {
            stopDao.getStop(stopId, stopType)
        }
    }

    fun getUiStopsFiltered(
        searchString: String,
        limit: Int,
        offset: Int,
        forceReload: Boolean
    ): List<UiStop> {
        return stopLineReloadHandler.reloadIfNeededAndRun(forceReload = forceReload) {
            if (searchString.isEmpty()) {
                stopDao.getStops(limit, offset)
            } else {
                stopDao.getFilteredStops(searchString, limit, offset)
            }
                .map { dbStop ->
                    UiStop(
                        dbStop = dbStop,
                        lines = stopDao
                            .getLinesForStop(dbStop.stopId, dbStop.type)
                            .sortedWith(::lineShortNameComparator),
                    )
                }
        }
    }
}