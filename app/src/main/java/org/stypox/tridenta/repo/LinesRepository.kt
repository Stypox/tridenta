package org.stypox.tridenta.repo

import org.stypox.tridenta.db.LineDao
import org.stypox.tridenta.db.data.DbLine
import org.stypox.tridenta.enums.Area
import org.stypox.tridenta.enums.StopLineType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LinesRepository @Inject constructor(
    private val stopLineReloadHandler: StopLineReloadHandler,
    private val lineDao: LineDao
) {
    fun getDbLine(lineId: Int, lineType: StopLineType): DbLine {
        return stopLineReloadHandler.runAndReloadIfNeeded {
            lineDao.getLine(lineId, lineType)
        }
    }

    fun getDbLinesByArea(area: Area): List<DbLine> {
        return stopLineReloadHandler.runAndReloadIfNeeded {
            lineDao.getLinesByArea(area)
        }
    }
}