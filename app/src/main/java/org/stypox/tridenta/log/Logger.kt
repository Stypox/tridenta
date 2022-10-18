package org.stypox.tridenta.log

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.stypox.tridenta.db.LogDao
import org.stypox.tridenta.db.data.LogEntry
import org.stypox.tridenta.util.getStackTraceString
import java.lang.ref.WeakReference
import java.time.OffsetDateTime

private var logDao: WeakReference<LogDao> = WeakReference(null)
private var scope: WeakReference<CoroutineScope> = WeakReference(null)

private const val RETAIN_LOGS_DAYS = 14L

fun setupLogger(newLogDao: LogDao?, newScope: CoroutineScope?) {
    logDao = WeakReference(newLogDao)
    scope = WeakReference(newScope)
}

fun clearOldLogs() {
    scope.get()?.launch {
        withContext(Dispatchers.IO) {
            logDao.get()?.clearOldLogs(OffsetDateTime.now().minusDays(RETAIN_LOGS_DAYS))
        }
    }
}

fun logToDatabaseBlocking(
    theLogDao: LogDao,
    logLevel: LogLevel,
    text: String,
    throwable: Throwable? = null
) {
    theLogDao.insertLog(
        LogEntry(
            logLevel = logLevel,
            text = text,
            stackTrace = throwable?.getStackTraceString(),
            dateTime = OffsetDateTime.now(),
        )
    )
}

private fun log(logLevel: LogLevel, text: String, throwable: Throwable? = null) {
    // log to the database, if the scope and the DAO are in place
    scope.get()?.launch {
        withContext(Dispatchers.IO) {
            logDao.get()?.let { logToDatabaseBlocking(it, logLevel, text, throwable) }
        }
    }

    // if the scope or the dao are not in place, then something could be wrong
    if (scope.get() == null || logDao.get() == null) {
        Log.w("TridentaLogger", "Scope or DAO not in place, cannot store log to database")
    }

    // also always send logs to logcat
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