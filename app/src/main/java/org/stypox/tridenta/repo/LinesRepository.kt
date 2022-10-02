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
    fun getDbLine(lineId: Int, lineType: StopLineType): DbLine {
        return stopLineReloadHandler.reloadIfNeededAndRun {
            lineDao.getLine(lineId, lineType)
        } ?: DbLine(
            lineId = lineId,
            type = lineType,
            area = null,
            color = null,
            longName = "Error",
            shortName = "???"
        )
    }

    fun getDbLinesByArea(area: Area, forceReload: Boolean): List<DbLine> {
        return stopLineReloadHandler.reloadIfNeededAndRun(forceReload = forceReload) {
            lineDao.getLinesByArea(area).sortedWith(::lineShortNameComparator)
        }
    }

    fun getUiLine(lineId: Int, lineType: StopLineType): UiLine? {
        return stopLineReloadHandler.reloadIfNeededAndRun {
            val dbLine = lineDao.getLine(lineId, lineType) ?: return@reloadIfNeededAndRun null
            val newsItems = lineDao.getNewsForLine(lineId, lineType)
            UiLine(
                lineId = dbLine.lineId,
                type = dbLine.type,
                area = dbLine.area,
                color = dbLine.color,
                longName = dbLine.longName,
                shortName = dbLine.shortName,
                newsItems = newsItems
            )
        }
    }
}