package org.stypox.tridenta.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.stypox.tridenta.db.data.*
import org.stypox.tridenta.db.views.DbLineAndFavorite
import org.stypox.tridenta.db.views.DbStopAndFavorite
import org.stypox.tridenta.db.views.HistoryLineOrStop

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
        HistoryLineOrStop::class,
    ],
    version = 3,
    autoMigrations = [
        AutoMigration(
            from = 1,
            to = 2,
        ),
        AutoMigration(
            from = 2,
            to = 3,
        ),
    ],
)
@TypeConverters(DbTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun lineDao(): LineDao
    abstract fun stopDao(): StopDao
    abstract fun historyDao(): HistoryDao
    abstract fun logDao(): LogDao
}

