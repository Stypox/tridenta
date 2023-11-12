package org.stypox.tridenta.extractor.data

import androidx.annotation.ColorInt
import org.stypox.tridenta.enums.Area
import org.stypox.tridenta.enums.StopLineType
import java.time.OffsetDateTime

data class ExLine(
    val lineId: Int,
    val type: StopLineType,
    val area: Area,
    @ColorInt val color: Int?,
    val longName: String,
    val shortName: String,
    val newsItems: List<ExNewsItem>,
)

data class ExNewsItem(
    val serviceType: String,
    val startDate: OffsetDateTime,
    val endDate: OffsetDateTime,
    val header: String,
    val details: String,
    val url: String,
)