package org.stypox.tridenta.repo

import org.stypox.tridenta.db.LineDao
import org.stypox.tridenta.db.data.DbLine
import org.stypox.tridenta.enums.Area
import org.stypox.tridenta.enums.StopLineType
import org.stypox.tridenta.repo.data.UiLine
import org.stypox.tridenta.repo.data.lineShortNameComparator
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LinesRepository @Inject constructor(
    private val stopLineReloadHandler: StopLineReloadHandler,
    private val lineDao: LineDao
) {
    fun getDbLine(lineId: Int, lineType: StopLineType): DbLine? {
        return stopLineReloadHandler.reloadIfNeededAndRun {
            lineDao.getLine(lineId, lineType)
        }
    }

    fun getDbLinesByArea(area: Area, forceReload: Boolean): List<DbLine> {
        return stopLineReloadHandler.reloadIfNeededAndRun(forceReload = forceReload) {
            if (area == Area.All) {
                lineDao.getAllLines()
            } else {
                lineDao.getLinesByArea(area)
            }
                .sortedWith(::lineShortNameComparator)
        }
    }

    fun getUiLine(lineId: Int, lineType: StopLineType): UiLine? {
        return stopLineReloadHandler.reloadIfNeededAndRun {
            UiLine(
                dbLine = lineDao.getLine(lineId, lineType) ?: return@reloadIfNeededAndRun null,
                newsItems = lineDao.getNewsForLine(lineId, lineType),
            )
        }
    }
}