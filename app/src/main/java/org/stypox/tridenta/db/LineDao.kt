package org.stypox.tridenta.db

import androidx.room.*
import org.stypox.tridenta.db.data.DbLine
import org.stypox.tridenta.db.data.DbNewsItem
import org.stypox.tridenta.enums.Area
import org.stypox.tridenta.enums.StopLineType

@Dao
interface LineDao {
    @Query("SELECT * FROM DbLine WHERE lineId = :lineId AND type = :lineType")
    fun getLine(lineId: Int, lineType: StopLineType): DbLine?

    @Query("SELECT * FROM DbLine WHERE area = :area")
    fun getLinesByArea(area: Area): List<DbLine>

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