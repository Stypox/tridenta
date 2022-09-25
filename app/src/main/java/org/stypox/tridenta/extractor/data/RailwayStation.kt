package org.stypox.tridenta.extractor.data

import java.time.OffsetTime

data class RailwayStation(
    val stop: ExStop,
    val url: String,
    val categories: List<String>,
    val accessibilityTopics: List<AccessibilityTopic>,
    val timetable: List<OpeningHours>,
    val notes: String,
)

data class AccessibilityTopic(
    val code: String,
    val values: List<AccessibilityValue>
)

data class AccessibilityValue(
    val description: String,
    val value: String,
)

data class OpeningHours(
    val morning: TimeRange?,
    val afternoon: TimeRange?,
)

data class TimeRange(
    val from: OffsetTime,
    val to: OffsetTime,
)