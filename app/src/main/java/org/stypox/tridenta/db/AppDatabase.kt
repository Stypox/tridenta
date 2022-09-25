package org.stypox.tridenta.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.stypox.tridenta.db.data.DbLine
import org.stypox.tridenta.db.data.DbNewsItem
import org.stypox.tridenta.db.data.DbStop
import org.stypox.tridenta.db.data.DbStopLineJoin

@Database(
    entities = [
        DbLine::class,
        DbNewsItem::class,
        DbStop::class,
        DbStopLineJoin::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DbTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun lineDao(): LineDao
    abstract fun stopDao(): StopDao
}

