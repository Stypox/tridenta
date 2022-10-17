package org.stypox.tridenta.log

import android.util.Log
import kotlinx.coroutines.*
import org.stypox.tridenta.db.LogDao
import org.stypox.tridenta.db.data.LogEntry
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.ref.WeakReference
import java.time.OffsetDateTime

private var logDao: WeakReference<LogDao> = WeakReference(null)
private var scope: WeakReference<CoroutineScope> = WeakReference(null)

private const val RETAIN_LOGS_DAYS = 14L

fun setupLogger(newLogDao: LogDao?, newScope: CoroutineScope?) {
    logDao = WeakReference(newLogDao)
    scope = WeakReference(newScope)
    newScope?.launch {
        withContext(Dispatchers.IO) {
            newLogDao?.clearOldLogs(OffsetDateTime.now().minusDays(RETAIN_LOGS_DAYS))
        }
    }
}

private fun log(logLevel: LogLevel, text: String, throwable: Throwable? = null) {
    // log to the database, if the scope and the DAO are in place
    scope.get()?.launch {
        withContext(Dispatchers.IO) {
            logDao.get()?.insertLog(
                LogEntry(
                    logLevel = logLevel,
                    text = text,
                    stackTrace = throwable?.let {
                        val stringWriter = StringWriter()
                        val printWriter = PrintWriter(stringWriter)
                        it.printStackTrace(printWriter)
                        stringWriter.toString()
                    },
                    dateTime = OffsetDateTime.now(),
                )
            )
        }
    }

    // also log with Android logger normally
    if (throwable == null) {
        val androidLogFunction: (String, String) -> Unit = when (logLevel) {
            LogLevel.Info -> Log::i
            LogLevel.Warning -> Log::w
            LogLevel.Error -> Log::e
        }
        androidLogFunction("Tridenta", text)
    } else {
        val androidLogFunction: (String, String, Throwable?) -> Unit = when (logLevel) {
            LogLevel.Info -> Log::i
            LogLevel.Warning -> Log::w
            LogLevel.Error -> Log::e
        }
        androidLogFunction("Tridenta", text, throwable)
    }
}

fun logInfo(text: String, throwable: Throwable? = null) {
    log(LogLevel.Info, text, throwable)
}

fun logWarning(text: String, throwable: Throwable? = null) {
    log(LogLevel.Warning, text, throwable)
}

fun logError(text: String, throwable: Throwable? = null) {
    log(LogLevel.Error, text, throwable)
}