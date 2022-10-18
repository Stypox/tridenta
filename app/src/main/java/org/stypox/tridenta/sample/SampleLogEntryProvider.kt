package org.stypox.tridenta.sample

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import org.stypox.tridenta.db.data.LogEntry
import org.stypox.tridenta.log.LogLevel
import org.stypox.tridenta.util.getStackTraceString
import java.time.OffsetDateTime
import java.time.ZoneOffset

class SampleLogEntryProvider : PreviewParameterProvider<LogEntry> {
    private val dateTime = OffsetDateTime.of(2022, 9, 26, 9, 33, 17, 328943849, ZoneOffset.UTC)

    override val values = sequenceOf(
        LogEntry(
            logLevel = LogLevel.Info,
            text = "Some informative message",
            stackTrace = null,
            dateTime = dateTime,
        ),
        LogEntry(
            logLevel = LogLevel.Warning,
            text = "Some warning message, lorem ipsum dolor sit amet consectetur adipisci elit",
            stackTrace = "A short stack trace",
            dateTime = dateTime.plusMinutes(2),
        ),
        LogEntry(
            logLevel = LogLevel.Error,
            text = "The app crashed!!",
            stackTrace = java.lang.RuntimeException().getStackTraceString(),
            dateTime = dateTime.plusMinutes(13),
        ),
    )
}