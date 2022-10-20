package org.stypox.tridenta.sample

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import org.stypox.tridenta.repo.data.UiStop

class SampleUiStopProvider : PreviewParameterProvider<UiStop> {
    private val sampleDbStops = SampleDbStopProvider().values
    private val sampleDbLines = SampleDbLineProvider().values.toList()

    override val values: Sequence<UiStop> = sampleDbStops.mapIndexed { index, dbStop ->
        UiStop(
            stopId = dbStop.stopId,
            type = dbStop.type,
            latitude = dbStop.latitude,
            longitude = dbStop.longitude,
            name = dbStop.name,
            street = dbStop.street,
            town = dbStop.town,
            wheelchairAccessible = dbStop.wheelchairAccessible,
            lines = sampleDbLines.subList(0, index % sampleDbLines.size),
            isFavorite = dbStop.isFavorite,
        )
    }
}