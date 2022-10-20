package org.stypox.tridenta.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.stypox.tridenta.db.data.*
import org.stypox.tridenta.db.views.DbLineAndFavorite
import org.stypox.tridenta.db.views.DbStopAndFavorite

@Database(
    entities = [
        DbLine::class,
        DbNewsItem::class,
        DbStop::class,
        DbStopLineJoin::class,
        HistoryEntry::class,
        LogEntry::class,
    ],
    views = [
        DbStopAndFavorite::class,
        DbLineAndFavorite::class,
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DbTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun lineDao(): LineDao
    abstract fun stopDao(): StopDao
    abstract fun historyDao(): HistoryDao
    abstract fun logDao(): LogDao
}

