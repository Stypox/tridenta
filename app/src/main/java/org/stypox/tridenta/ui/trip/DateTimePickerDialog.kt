package org.stypox.tridenta.ui.trip

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

/**
 * Opens a date time picker using old android views without applying a theme.
 * TODO as soon as Compose properly implements date time pickers, use those
 */
fun pickDateTime(context: Context, handleResult: (ZonedDateTime) -> Unit) {
    val currentDateTime = Calendar.getInstance()
    val startYear = currentDateTime.get(Calendar.YEAR)
    val startMonth = currentDateTime.get(Calendar.MONTH)
    val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
    val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
    val startMinute = currentDateTime.get(Calendar.MINUTE)

    DatePickerDialog(
        context,
        { _, year, month, day ->
            TimePickerDialog(
                context,
                { _, hour, minute ->
                    handleResult(
                        ZonedDateTime.of(
                            year,
                            month + 1,
                            day,
                            hour,
                            minute,
                            0,
                            0,
                            ZoneId.systemDefault()
                        )
                    )
                },
                startHour,
                startMinute,
                false
            )
                .show()
        },
        startYear,
        startMonth,
        startDay
    )
        .show()
}