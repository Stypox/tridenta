package org.stypox.tridenta.ui.trips

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import org.stypox.tridenta.R
import org.stypox.tridenta.enums.Direction
import org.stypox.tridenta.enums.StopLineType
import org.stypox.tridenta.repo.data.UiTrip
import org.stypox.tridenta.sample.SampleUiTripProvider
import org.stypox.tridenta.ui.theme.*
import org.stypox.tridenta.util.formatDurationMinutes
import org.stypox.tridenta.util.formatTime
import org.stypox.tridenta.util.textColorOnBackground
import org.stypox.tridenta.util.toComposeColor

@Composable
fun TripView(
    trip: UiTrip,
    prevEnabled: Boolean,
    onPrevClicked: () -> Unit,
    nextEnabled: Boolean,
    onNextClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        TripViewTopRow(
            trip = trip,
            modifier = Modifier.padding(start = 12.dp, top = 12.dp, end = 12.dp, bottom = 16.dp)
        )
        TripViewStops(
            trip = trip,
            modifier = Modifier.weight(1.0f)
        )
        TripViewBottomRow(
            trip = trip,
            prevEnabled = prevEnabled,
            onPrevClicked = onPrevClicked,
            nextEnabled = nextEnabled,
            onNextClicked = onNextClicked,
            modifier = Modifier
        )
    }
}

@Composable
private fun TripViewTopRow(trip: UiTrip, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        // shortName
        val shortNameBackground = trip.line.color.toComposeColor()
        Surface(
            color = shortNameBackground,
            shape = MaterialTheme.shapes.medium,
        ) {
            HeadlineText(
                text = trip.line.shortName,
                color = textColorOnBackground(shortNameBackground),
                modifier = Modifier.padding(12.dp, 6.dp),
                maxLines = 1,
            )
        }

        Column(
            modifier = Modifier
                .weight(1.0f)
                .padding(horizontal = 12.dp)
        ) {
            // headSign
            TitleText(
                text = trip.headSign,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // delay
            if (trip.lastEventReceivedAt != null) {
                val delayText = if (trip.delay < 0)
                    stringResource(R.string.early, formatDurationMinutes(-trip.delay))
                else if (trip.delay == 0)
                    stringResource(R.string.on_time)
                else
                    stringResource(R.string.late, formatDurationMinutes(trip.delay))
                BodyText(
                    text = delayText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // type + direction
        Column {
            Icon(
                imageVector = when (trip.type) {
                    StopLineType.Urban -> Icons.Filled.LocationCity
                    StopLineType.Suburban -> Icons.Filled.Landscape
                },
                contentDescription = null
            )
            Icon(
                imageVector = when (trip.direction) {
                    Direction.Forward -> Icons.Filled.TurnSharpRight
                    Direction.Backward -> Icons.Filled.UTurnLeft
                    Direction.ForwardAndBackward -> Icons.Filled.Loop
                },
                contentDescription = null
            )
        }
    }
}

@Composable
private fun TripViewStops(trip: UiTrip, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        itemsIndexed(trip.stopTimes) { index, stopTime ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
            ) {
                Icon(
                    imageVector = if (index < trip.completedStops) {
                        Icons.Filled.CheckCircle
                    } else {
                        Icons.Filled.RadioButtonUnchecked
                    },
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                BodyText(
                    text = stopTime.stop.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1.0f)
                        .padding(horizontal = 6.dp)
                )

                val isLate = index >= trip.completedStops && trip.delay > 0
                val lateDecoration = if (isLate) TextDecoration.LineThrough else null

                LabelText(
                    text = formatTime(stopTime.arrivalTime),
                    maxLines = 1,
                    textDecoration = lateDecoration
                )
                if (stopTime.arrivalTime != stopTime.departureTime) {
                    Icon(
                        imageVector = Icons.Filled.DoubleArrow,
                        contentDescription = null,
                        modifier = Modifier.size(8.dp)
                    )
                    LabelText(
                        text = formatTime(stopTime.departureTime),
                        maxLines = 1,
                        textDecoration = lateDecoration
                    )
                }
                if (isLate) {
                    LabelText(
                        text = formatTime(
                            stopTime.arrivalTime.plusMinutes(trip.delay.toLong())
                        ),
                        modifier = Modifier.padding(start = 5.dp),
                        color = MaterialTheme.colorScheme.error,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun TripViewBottomRow(
    trip: UiTrip,
    prevEnabled: Boolean,
    onPrevClicked: () -> Unit,
    nextEnabled: Boolean,
    onNextClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth()
    ) {
        FloatingActionButton(
            onClick = onPrevClicked,
            modifier = Modifier
                .padding(16.dp)
                .alpha(if (prevEnabled) 1.0f else 0.3f)
        ) {
            Icon(
                imageVector = Icons.Filled.NavigateBefore,
                contentDescription = stringResource(R.string.previous)
            )
        }

        Column(
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            if (trip.lastEventReceivedAt != null) {
                BodyText(
                    text = stringResource(
                        R.string.last_update,
                        formatTime(trip.lastEventReceivedAt)
                    )
                )
            }
            // TODO add time of last reload from network
        }

        FloatingActionButton(
            onClick = onNextClicked,
            modifier = Modifier
                .padding(16.dp)
                .alpha(if (nextEnabled) 1.0f else 0.3f)
        ) {
            Icon(
                imageVector = Icons.Filled.NavigateNext,
                contentDescription = stringResource(R.string.next)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TripViewPreview(@PreviewParameter(SampleUiTripProvider::class) trip: UiTrip) {
    AppTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            TripView(
                trip = trip,
                prevEnabled = true,
                onPrevClicked = {},
                nextEnabled = false,
                onNextClicked = {}
            )
        }
    }
}