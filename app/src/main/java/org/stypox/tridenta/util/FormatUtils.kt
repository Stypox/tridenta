package org.stypox.tridenta.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import org.stypox.tridenta.R
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.FormatStyle
import java.time.temporal.ChronoField

private val timeFormat = DateTimeFormatterBuilder()
    .appendValue(ChronoField.HOUR_OF_DAY)
    .appendLiteral(':')
    .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
    .toFormatter()

private val dateFormatFull = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)

private val dateFormatShort = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)

private val dateTimeFormatShort = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)

@Composable
fun formatDurationMinutes(minutes: Int): String {
    return stringResource(R.string.short_minute_format, minutes)
}

fun formatTime(offsetDateTime: OffsetDateTime): String {
    return timeFormat.format(offsetDateTime)
}

fun formatDateFull(offsetDateTime: OffsetDateTime): String {
    return dateFormatFull.format(offsetDateTime)
}

fun formatDateShort(offsetDateTime: OffsetDateTime): String {
    return dateFormatShort.format(offsetDateTime)
}

fun formatDateTimeShort(offsetDateTime: OffsetDateTime): String {
    return dateTimeFormatShort.format(offsetDateTime)
}

fun formatConcatStrings(vararg strings: String?): String {
    return strings.filter { it?.isBlank() == false }.joinToString(" • ")
}
