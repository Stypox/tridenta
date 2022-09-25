package org.stypox.tridenta.repo.data

import androidx.annotation.ColorInt
import org.stypox.tridenta.db.data.DbNewsItem
import org.stypox.tridenta.enums.Area
import org.stypox.tridenta.enums.StopLineType

data class UiLine(
    val lineId: Int,
    val type: StopLineType,
    val area: Area?,
    @ColorInt val color: Int?,
    val longName: String,
    val shortName: String,
    val newsItems: List<DbNewsItem>,
)
