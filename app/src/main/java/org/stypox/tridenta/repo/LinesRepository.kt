package org.stypox.tridenta.repo

import org.stypox.tridenta.db.LineDao
import org.stypox.tridenta.db.data.DbLine
import org.stypox.tridenta.enums.Area
import org.stypox.tridenta.enums.StopLineType
import org.stypox.tridenta.repo.data.UiLine
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
        }
    }

    fun getDbLinesByArea(area: Area, forceReload: Boolean): List<DbLine> {
        return stopLineReloadHandler.reloadIfNeededAndRun(forceReload = forceReload) {
            lineDao.getLinesByArea(area)
        }
    }

    fun getUiLine(lineId: Int, lineType: StopLineType): UiLine {
        return stopLineReloadHandler.reloadIfNeededAndRun {
            val dbLine = lineDao.getLine(lineId, lineType)
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