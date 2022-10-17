package org.stypox.tridenta.db.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.stypox.tridenta.log.LogLevel
import java.time.OffsetDateTime

@Entity
data class LogEntry(
    val logLevel: LogLevel,
    val text: String,
    val stackTrace: String?,
    val dateTime: OffsetDateTime,
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
)