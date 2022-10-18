package org.stypox.tridenta

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.MainScope
import org.stypox.tridenta.db.LogDao
import org.stypox.tridenta.log.AppUncaughtExceptionHandler
import org.stypox.tridenta.log.clearOldLogs
import org.stypox.tridenta.log.setupLogger
import javax.inject.Inject

@HiltAndroidApp
class TridentaApplication : Application() {

    // store logger's DAO and scope here, so that they are correctly garbage collected when
    // Application is destroyed (probably this is not needed, but let's be sure)
    @Inject lateinit var logDao: LogDao
    private val loggerScope = MainScope()

    override fun onCreate() {
        super.onCreate()
        setupLogger(logDao, loggerScope)
        AppUncaughtExceptionHandler.init(applicationContext)

        // cleanup old logs (we don't want the database to be cluttered with those)
        clearOldLogs()
    }
}