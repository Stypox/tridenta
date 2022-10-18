package org.stypox.tridenta.log

import android.content.Context
import android.util.Log
import org.stypox.tridenta.db.DatabaseModule
import java.lang.Thread.UncaughtExceptionHandler

class AppUncaughtExceptionHandler private constructor(
    private val context: Context,
    private val previousHandler: UncaughtExceptionHandler?
) : UncaughtExceptionHandler {

    override fun uncaughtException(t: Thread, e: Throwable) {
        try {
            // use a database that allows main thread queries, since the app would hang indefinitely
            // if trying to launch a separate thread
            val logDao = DatabaseModule.appDatabaseBuilder(context)
                .allowMainThreadQueries()
                .build()
                .logDao()

            logToDatabaseBlocking(logDao, LogLevel.Error, "App crash on $t", e)

        } catch (e: Throwable) {
            // if exceptions are not caught, the app would hang indefinitely
            Log.e("ExceptionHandler", "Could not save error to database", e)
        }

        previousHandler?.uncaughtException(t, e)
    }

    companion object {
        fun init(context: Context) {
            val previousHandler = Thread.getDefaultUncaughtExceptionHandler()
            if (previousHandler !is AppUncaughtExceptionHandler) {
                Thread.setDefaultUncaughtExceptionHandler(
                    AppUncaughtExceptionHandler(context, previousHandler)
                )
            }
        }
    }
}