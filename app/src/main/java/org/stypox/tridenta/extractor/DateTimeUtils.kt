package org.stypox.tridenta.extractor

import java.io.IOException
import java.time.*
import java.time.temporal.ChronoUnit

private val ROME_ZONE_ID = ZoneId.of("Europe/Rome")

fun dateTimeFromEpochString(s: String): OffsetDateTime {
    if (s.length <= 8) {
        throw IOException("Invalid epoch string date: $s")
    }

    // the format is `/Date(1672444800)/`
    val epochSecond = s.substring(6, s.length-2).toLong()
    return OffsetDateTime.ofInstant(Instant.ofEpochSecond(epochSecond), ZoneOffset.UTC)
}

fun dateTimeFromISOString(s: String?): OffsetDateTime? {
    return if (s.isNullOrEmpty() || s == "null") {
        null
    } else {
        OffsetDateTime.parse(s).atZoneSameInstant(ROME_ZONE_ID).toOffsetDateTime()
    }
}

class ZonedTimeHelper(private var referenceDateTime: ZonedDateTime) {
    private var prevTime = LocalTime.of(0, 0, 0) // midnight, no time can come before this

    fun timeFromRomeString(s: String?): OffsetDateTime? {
        if (s.isNullOrEmpty() || s == "null") {
            return null
        }

        val isoString = if (s.startsWith("24:")) {
            "00:" + s.substring(3)
        } else {
            s
        }

        // If the time went e.g. from 23:58 to 00:01, then prevTime-time will be 23 hours,
        // and we can conclude that a day has passed. We use -2 to make sure that if the
        // difference is less than 2 hours, then probably this is an off-by-one-minute
        // error, and a day has not actually elapsed (e.g. from 18:02 to 18:01).
        val time = LocalTime.parse(isoString)
        if (ChronoUnit.HOURS.between(prevTime, time) <= -2) {
            referenceDateTime = referenceDateTime.plusDays(1)
        }
        prevTime = time

        return referenceDateTime.with(time).toOffsetDateTime()
    }
}