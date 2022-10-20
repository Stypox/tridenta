package org.stypox.tridenta.db

import androidx.room.*
import org.stypox.tridenta.db.data.DbLine
import org.stypox.tridenta.db.data.DbStop
import org.stypox.tridenta.db.data.DbStopLineJoin
import org.stypox.tridenta.db.views.DbLineAndFavorite
import org.stypox.tridenta.db.views.DbStopAndFavorite
import org.stypox.tridenta.enums.StopLineType

@Dao
interface StopDao {
    @Query("SELECT * FROM DbStopAndFavorite WHERE stopId = :stopId AND type = :stopType")
    fun getStopImpl(stopId: Int, stopType: StopLineType): DbStopAndFavorite?

    fun getStop(stopId: Int, stopType: StopLineType): DbStop? {
        return getStopImpl(stopId, stopType)?.dbStop
    }


    // TODO use a better filtering and sorting method, that also caches nfkd-normalized strings
    @RewriteQueriesToDropUnusedColumns // only DbStop.* columns are needed
    @Query(
        """
            SELECT DbStopAndFavorite.*,
                    name LIKE '%' || :searchString || '%' AS stringInName,
                    street LIKE '%' || :searchString || '%' AS stringInStreet,
                    town LIKE '%' || :searchString || '%' AS stringInTown
            FROM DbStopAndFavorite
            WHERE stringInName OR stringInStreet OR stringInTown
            ORDER BY stringInName * -2 + stringInStreet * -1 + stringInTown * -1
            LIMIT :limit OFFSET :offset
        """
    )
    fun getFilteredStopsImpl(searchString: String, limit: Int, offset: Int): List<DbStopAndFavorite>

    fun getFilteredStops(searchString: String, limit: Int, offset: Int): List<DbStop> {
        return getFilteredStopsImpl(searchString, limit, offset).map(DbStopAndFavorite::dbStop)
    }


    @Query(
        """
            SELECT DbStopAndFavorite.*
            FROM DbStopAndFavorite INNER JOIN DbStopLineJoin
            WHERE DbStopLineJoin.stopId = DbStopAndFavorite.stopId
                AND DbStopLineJoin.stopType = DbStopAndFavorite.type
            GROUP BY DbStopAndFavorite.stopId, DbStopAndFavorite.type
            ORDER BY COUNT(*) DESC
            LIMIT :limit OFFSET :offset
        """
    )
    fun getStopsImpl(limit: Int, offset: Int): List<DbStopAndFavorite>

    fun getStops(limit: Int, offset: Int): List<DbStop> {
        return getStopsImpl(limit, offset).map(DbStopAndFavorite::dbStop)
    }


    @Query(
        """
            SELECT DbLineAndFavorite.*
            FROM DbStopLineJoin INNER JOIN DbLineAndFavorite
            WHERE DbStopLineJoin.stopId = :stopId
                AND DbStopLineJoin.stopType = :stopType
                AND DbStopLineJoin.lineId = DbLineAndFavorite.lineId
                AND DbStopLineJoin.lineType = DbLineAndFavorite.type
        """
    )
    fun getLinesForStopImpl(stopId: Int, stopType: StopLineType): List<DbLineAndFavorite>

    fun getLinesForStop(stopId: Int, stopType: StopLineType): List<DbLine> {
        return getLinesForStopImpl(stopId, stopType).map(DbLineAndFavorite::dbLine)
    }


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertDbStops(stops: Collection<DbStop>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertDbStopLineJoins(stopLineJoins: Collection<DbStopLineJoin>)

    @Query("DELETE FROM DbStop")
    fun deleteAllDbStops()

    @Query("DELETE FROM DbStopLineJoin")
    fun deleteAllDbStopLineJoins()
}