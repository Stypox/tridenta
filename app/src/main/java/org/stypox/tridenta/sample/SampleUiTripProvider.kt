package org.stypox.tridenta.sample

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import org.stypox.tridenta.enums.Direction
import org.stypox.tridenta.enums.StopLineType
import org.stypox.tridenta.repo.data.UiStop
import org.stypox.tridenta.repo.data.UiStopTime
import org.stypox.tridenta.repo.data.UiTrip
import java.time.OffsetDateTime
import java.time.ZoneOffset

class SampleUiTripProvider : PreviewParameterProvider<UiTrip> {
    private val sampleDbStops = SampleDbStopProvider().values.toList()
    private val sampleDbLines = SampleDbLineProvider().values.toList()
    private val referenceDateTime =
        OffsetDateTime.of(2022, 9, 26, 9, 33, 17, 328943849, ZoneOffset.UTC)

    override val values: Sequence<UiTrip> = sequenceOf(
        UiTrip(
            delay = 1,
            direction = Direction.Backward,
            lastEventReceivedAt = referenceDateTime.minusMinutes(3),
            line = sampleDbLines.first(),
            headSign = "Conci \"Villazzano 3\"",
            tripId = "0003726592022061120220911",
            type = StopLineType.Urban,
            completedStops = 2,
            stopTimes = sampleDbStops.mapIndexed { index, dbStop ->
                UiStopTime(
                    arrivalTime = referenceDateTime.plusMinutes((index - 2).toLong()),
                    departureTime = referenceDateTime.plusMinutes((index + index % 2 - 2).toLong()),
                    stop = dbStop
                )
            }
        ),
        UiTrip(
            delay = 0,
            direction = Direction.Forward,
            lastEventReceivedAt = null,
            line = sampleDbLines.last(),
            headSign = "Trento-Vezzano-Sarche-Tione",
            tripId = "0003726592022061120220911",
            type = StopLineType.Suburban,
            completedStops = 0,
            stopTimes = sampleDbStops.mapIndexed { index, dbStop ->
                UiStopTime(
                    arrivalTime = referenceDateTime.plusMinutes((index - 2).toLong()),
                    departureTime = referenceDateTime.plusMinutes((index + index % 2 - 2).toLong()),
                    stop = dbStop
                )
            }
        )
    )
}