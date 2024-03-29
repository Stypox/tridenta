package org.stypox.tridenta.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.stypox.tridenta.R
import org.stypox.tridenta.enums.Direction
import org.stypox.tridenta.enums.StopLineType
import org.stypox.tridenta.log.LogLevel

@Composable
fun StopLineTypeIcon(stopLineType: StopLineType, modifier: Modifier = Modifier) {
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
        ),
        modifier = modifier,
    )
}

@Composable
fun DirectionIcon(direction: Direction) {
    Icon(
        imageVector = when (direction) {
            Direction.Forward -> Icons.Filled.TurnSharpRight
            Direction.Backward -> Icons.Filled.UTurnLeft
            Direction.ForwardAndBackward -> Icons.Filled.SwapCalls
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

@Composable
fun LogLevelIcon(logLevel: LogLevel, modifier: Modifier = Modifier) {
    Icon(
        imageVector = when (logLevel) {
            LogLevel.Info -> Icons.Filled.Info
            LogLevel.Warning -> Icons.Filled.Warning
            LogLevel.Error -> Icons.Filled.Error
        },
        contentDescription = null,
        tint = when (logLevel) {
            LogLevel.Info -> MaterialTheme.colorScheme.onBackground
            LogLevel.Warning -> MaterialTheme.colorScheme.onBackground
            LogLevel.Error -> MaterialTheme.colorScheme.error
        },
        modifier = modifier
    )
}