package org.stypox.tridenta.sample

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import org.stypox.tridenta.repo.data.UiLine

class SampleUiLineProvider : PreviewParameterProvider<UiLine> {
    private val sampleDbLines = SampleDbLineProvider().values
    private val sampleDbNewsItems = SampleDbNewsItemProvider().values.toList()

    override val values: Sequence<UiLine> = sampleDbLines.mapIndexed { index, dbLine ->
        UiLine(
            dbLine = dbLine,
            newsItems = sampleDbNewsItems.subList(0, index % sampleDbNewsItems.size),
        )
    }
}