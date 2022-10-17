package org.stypox.tridenta

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.MainScope
import org.stypox.tridenta.db.LogDao
import org.stypox.tridenta.log.setupLogger
import javax.inject.Inject

@HiltAndroidApp
class TridentaApplication : Application() {

    @Inject
    lateinit var logDao: LogDao

    private val scope = MainScope()

    override fun onCreate() {
        super.onCreate()
        setupLogger(logDao, scope)
    }
}