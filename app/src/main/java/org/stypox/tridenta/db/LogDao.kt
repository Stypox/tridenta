package org.stypox.tridenta.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import org.stypox.tridenta.db.data.LogEntry
import java.time.OffsetDateTime

@Dao
interface LogDao {

    @Query("SELECT * FROM LogEntry ORDER BY dateTime DESC")
    fun getLogs(): LiveData<List<LogEntry>>

    @Insert
    fun insertLog(logEntry: LogEntry)

    @Query("DELETE FROM LogEntry WHERE dateTime < :before")
    fun clearOldLogs(before: OffsetDateTime)
}