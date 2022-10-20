package org.stypox.tridenta.sample

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import org.stypox.tridenta.repo.data.UiStop

class SampleUiStopProvider : PreviewParameterProvider<UiStop> {
    private val sampleDbStops = SampleDbStopProvider().values
    private val sampleDbLines = SampleDbLineProvider().values.toList()

    override val values: Sequence<UiStop> = sampleDbStops.mapIndexed { index, dbStop ->
        UiStop(
            dbStop = dbStop,
            lines = sampleDbLines.subList(0, index % sampleDbLines.size),
        )
    }
}