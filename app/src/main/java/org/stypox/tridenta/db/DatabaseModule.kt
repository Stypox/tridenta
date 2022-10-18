package org.stypox.tridenta.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun providesAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return appDatabaseBuilder(appContext).build()
    }

    @Provides
    fun providesStopDao(appDatabase: AppDatabase): StopDao {
        return appDatabase.stopDao()
    }

    @Provides
    fun providesLineDao(appDatabase: AppDatabase): LineDao {
        return appDatabase.lineDao()
    }

    @Provides
    fun providesHistoryDao(appDatabase: AppDatabase): HistoryDao {
        return appDatabase.historyDao()
    }

    @Provides
    fun providesLogDao(appDatabase: AppDatabase): LogDao {
        return appDatabase.logDao()
    }

    companion object {
        fun appDatabaseBuilder(context: Context): RoomDatabase.Builder<AppDatabase> {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "db"
            )
        }
    }
}