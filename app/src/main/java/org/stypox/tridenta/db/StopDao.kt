package org.stypox.tridenta.db

import androidx.room.*
import org.stypox.tridenta.db.data.DbLine
import org.stypox.tridenta.db.data.DbStop
import org.stypox.tridenta.db.data.DbStopLineJoin
import org.stypox.tridenta.enums.StopLineType

@Dao
interface StopDao {
    @Query("SELECT * FROM DbStop WHERE stopId = :stopId AND type = :stopType")
    fun getStop(stopId: Int, stopType: StopLineType): DbStop

    // TODO use a better filtering and sorting method, that also caches nfkd-normalized strings
    @RewriteQueriesToDropUnusedColumns // only DbStop.* columns are needed
    @Query(
        """
            SELECT DbStop.*,
                    name LIKE '%' || :searchString || '%' AS stringInName,
                    street LIKE '%' || :searchString || '%' AS stringInStreet,
                    town LIKE '%' || :searchString || '%' AS stringInTown
            FROM DbStop
            WHERE stringInName OR stringInStreet OR stringInTown
            ORDER BY stringInName * -2 + stringInStreet * -1 + stringInTown * -1
            LIMIT :limit OFFSET :offset
        """
    )
    fun getFilteredStops(searchString: String, limit: Int, offset: Int): List<DbStop>

    @Query(
        """
            SELECT DbStop.*
            FROM DbStop INNER JOIN DbStopLineJoin
            WHERE DbStopLineJoin.stopId = DbStop.stopId
                AND DbStopLineJoin.stopType = DbStop.type
            GROUP BY DbStop.stopId, DbStop.type
            ORDER BY COUNT(*) DESC
            LIMIT :limit OFFSET :offset
        """
    )
    fun getStops(limit: Int, offset: Int): List<DbStop>

    @Query(
        """
            SELECT DbLine.*
            FROM DbStopLineJoin INNER JOIN DbLine
            WHERE DbStopLineJoin.stopId = :stopId
                AND DbStopLineJoin.stopType = :stopType
                AND DbStopLineJoin.lineId = DbLine.lineId
                AND DbStopLineJoin.lineType = DbLine.type
        """
    )
    fun getLinesForStop(stopId: Int, stopType: StopLineType): List<DbLine>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertDbStops(stops: Collection<DbStop>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertDbStopLineJoins(stopLineJoins: Collection<DbStopLineJoin>)

    @Query("DELETE FROM DbStop")
    fun deleteAllDbStops()

    @Query("DELETE FROM DbStopLineJoin")
    fun deleteAllDbStopLineJoins()
}