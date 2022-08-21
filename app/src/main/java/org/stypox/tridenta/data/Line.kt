package org.stypox.tridenta.data

import androidx.annotation.ColorInt
import java.time.OffsetDateTime

data class Line(
    val lineId: Int,
    val area: Area?,
    @ColorInt val color: Int?,
    val longName: String,
    val shortName: String,
    val type: StopLineType,
    val newsItems: List<NewsItem>,
)

data class NewsItem(
    val serviceType: String,
    val startDate: OffsetDateTime,
    val endDate: OffsetDateTime,
    val header: String,
    val details: String,
    val url: String,
    val affectedLineIds: List<Int>,
)