package org.stypox.tridenta.util

import android.icu.text.MeasureFormat
import android.icu.util.Measure
import android.icu.util.MeasureUnit
import androidx.compose.runtime.Composable
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.FormatStyle
import java.time.temporal.ChronoField
import java.util.*

private val TIME_FORMAT = DateTimeFormatterBuilder()
    .appendValue(ChronoField.HOUR_OF_DAY)
    .appendLiteral(':')
    .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
    .toFormatter()

private val DATE_FORMAT = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)

fun formatDurationMinutes(minutes: Int): String {
    val measureFormat =
        MeasureFormat.getInstance(Locale.getDefault(), MeasureFormat.FormatWidth.SHORT)
    return measureFormat.format(Measure(minutes, MeasureUnit.MINUTE))
}

fun formatTime(offsetDateTime: OffsetDateTime): String {
    return TIME_FORMAT.format(offsetDateTime)
}

fun formatDate(offsetDateTime: OffsetDateTime): String {
    return DATE_FORMAT.format(offsetDateTime)
}
