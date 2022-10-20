package org.stypox.tridenta.db

import androidx.room.*
import org.stypox.tridenta.db.data.DbLine
import org.stypox.tridenta.db.data.DbNewsItem
import org.stypox.tridenta.db.views.DbLineAndFavorite
import org.stypox.tridenta.enums.Area
import org.stypox.tridenta.enums.StopLineType

@Dao
interface LineDao {

    @Query("SELECT * FROM DbLineAndFavorite WHERE lineId = :lineId AND type = :lineType")
    fun getLineImpl(lineId: Int, lineType: StopLineType): DbLineAndFavorite?

    fun getLine(lineId: Int, lineType: StopLineType): DbLine? {
        return getLineImpl(lineId, lineType)?.dbLine
    }


    @Query("SELECT * FROM DbLineAndFavorite WHERE area = :area")
    fun getLinesByAreaImpl(area: Area): List<DbLineAndFavorite>

    /**
     * If [Area.All] is passed, this will not work as expected. Use [getAllLines] instead.
     */
    fun getLinesByArea(area: Area): List<DbLine> {
        return getLinesByAreaImpl(area).map(DbLineAndFavorite::dbLine)
    }


    @Query("SELECT * FROM DbLineAndFavorite")
    fun getAllLinesImpl(): List<DbLineAndFavorite>

    fun getAllLines(): List<DbLine> {
        return getAllLinesImpl().map(DbLineAndFavorite::dbLine)
    }


    @Query("SELECT * FROM DbNewsItem WHERE lineId = :lineId AND lineType = :lineType")
    fun getNewsForLine(lineId: Int, lineType: StopLineType): List<DbNewsItem>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertDbLines(lines: Collection<DbLine>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertDbNewsItems(lines: Collection<DbNewsItem>)

    @Query("DELETE FROM DbLine")
    fun deleteAllDbLines()

    @Query("DELETE FROM DbNewsItem")
    fun deleteAllDbNewsItems()
}