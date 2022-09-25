package org.stypox.tridenta.sample

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import org.stypox.tridenta.repo.data.UiLine

class SampleUiLineProvider : PreviewParameterProvider<UiLine> {
    private val sampleDbLines = SampleDbLineProvider().values
    private val sampleDbNewsItems = SampleDbNewsItemProvider().values.toList()

    override val values: Sequence<UiLine> = sampleDbLines.mapIndexed { index, dbLine ->
        UiLine(
            lineId = dbLine.lineId,
            type = dbLine.type,
            area = dbLine.area,
            color = dbLine.color,
            longName = dbLine.longName,
            shortName = dbLine.shortName,
            newsItems = sampleDbNewsItems.subList(0, index % sampleDbNewsItems.size)
        )
    }
}