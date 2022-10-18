package org.stypox.tridenta.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import org.stypox.tridenta.R
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.FormatStyle
import java.time.temporal.ChronoField

private val TIME_FORMAT = DateTimeFormatterBuilder()
    .appendValue(ChronoField.HOUR_OF_DAY)
    .appendLiteral(':')
    .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
    .toFormatter()

private val DATE_FORMAT_FULL = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)

private val DATE_FORMAT_SHORT = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)

private val DATE_TIME_FORMAT_SHORT = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)

@Composable
fun formatDurationMinutes(minutes: Int): String {
    return stringResource(R.string.short_minute_format, minutes)
}

fun formatTime(offsetDateTime: OffsetDateTime): String {
    return TIME_FORMAT.format(offsetDateTime)
}

fun formatDateFull(offsetDateTime: OffsetDateTime): String {
    return DATE_FORMAT_FULL.format(offsetDateTime)
}

fun formatDateShort(offsetDateTime: OffsetDateTime): String {
    return DATE_FORMAT_SHORT.format(offsetDateTime)
}

fun formatDateTimeShort(offsetDateTime: OffsetDateTime): String {
    return DATE_TIME_FORMAT_SHORT.format(offsetDateTime)
}
