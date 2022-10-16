package org.stypox.tridenta.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import org.stypox.tridenta.R
import org.stypox.tridenta.enums.Direction
import org.stypox.tridenta.enums.StopLineType

@Composable
fun StopLineTypeIcon(stopLineType: StopLineType) {
    Icon(
        imageVector = when (stopLineType) {
            StopLineType.Urban -> Icons.Filled.LocationCity
            StopLineType.Suburban -> Icons.Filled.Landscape
        },
        contentDescription = stringResource(
            when (stopLineType) {
                StopLineType.Urban -> R.string.urban
                StopLineType.Suburban -> R.string.suburban
            }
        )
    )
}

@Composable
fun DirectionIcon(direction: Direction) {
    Icon(
        imageVector = when (direction) {
            Direction.Forward -> Icons.Filled.TurnSharpRight
            Direction.Backward -> Icons.Filled.UTurnLeft
            Direction.ForwardAndBackward -> Icons.Filled.Loop
        },
        contentDescription = stringResource(
            when (direction) {
                Direction.Forward -> R.string.forward
                Direction.Backward -> R.string.backward
                Direction.ForwardAndBackward -> R.string.forward_and_backward
            }
        )
    )
}