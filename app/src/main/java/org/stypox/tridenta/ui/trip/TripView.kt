package org.stypox.tridenta.ui.trip

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import org.stypox.tridenta.R
import org.stypox.tridenta.enums.Direction
import org.stypox.tridenta.enums.StopLineType
import org.stypox.tridenta.repo.data.UiTrip
import org.stypox.tridenta.sample.SampleDbStopProvider
import org.stypox.tridenta.sample.SampleUiTripProvider
import org.stypox.tridenta.ui.theme.*
import org.stypox.tridenta.util.*
import java.time.ZonedDateTime

@Composable
fun TripView(
    trip: UiTrip?,
    setReferenceDateTime: (ZonedDateTime) -> Unit,
    loading: Boolean,
    onReload: () -> Unit,
    prevEnabled: Boolean,
    onPrevClicked: () -> Unit,
    nextEnabled: Boolean,
    onNextClicked: () -> Unit,
    stopIdToHighlight: Int?,
    stopTypeToHighlight: StopLineType?,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxHeight()) {
        if (trip != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.TopCenter)
            ) {
                TripViewTopRow(
                    trip = trip,
                    modifier = Modifier.padding(start = 12.dp, top = 12.dp, end = 12.dp, bottom = 16.dp)
                )
                TripViewStops(
                    trip = trip,
                    stopIdToHighlight = stopIdToHighlight,
                    stopTypeToHighlight = stopTypeToHighlight,
                    modifier = Modifier.weight(1.0f)
                )
            }

        } else if (loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

        } else {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                HeadlineText(
                    text = stringResource(R.string.no_trip_found),
                    textAlign = TextAlign.Center
                )
                BodyText(
                    text = stringResource(R.string.no_trip_found_description),
                    textAlign = TextAlign.Center
                )
            }
        }

        TripViewBottomRow(
            setReferenceDateTime = setReferenceDateTime,
            loading = loading,
            onReload = onReload,
            prevEnabled = prevEnabled,
            onPrevClicked = onPrevClicked,
            nextEnabled = nextEnabled,
            onNextClicked = onNextClicked,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
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
            TitleText(
                text = trip.line.shortName,
                color = textColorOnBackground(shortNameBackground),
                modifier = Modifier.padding(8.dp),
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

            // date or delay
            val dateOrDelayText = if (trip.lastEventReceivedAt == null) {
                trip.stopTimes.asSequence()
                    .map { it.arrivalTime }
                    .filter { it != null }
                    .first()
                    ?.let { firstArrival -> formatDateFull(firstArrival) }
                    ?: stringResource(R.string.no_date_time_information)
            } else {
                if (trip.delay < 0)
                    stringResource(R.string.early, formatDurationMinutes(-trip.delay))
                else if (trip.delay == 0)
                    stringResource(R.string.on_time)
                else
                    stringResource(R.string.late, formatDurationMinutes(trip.delay))
            }
            BodyText(
                text = dateOrDelayText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
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
private fun TripViewStops(
    trip: UiTrip,
    stopIdToHighlight: Int?,
    stopTypeToHighlight: StopLineType?,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    LaunchedEffect(trip.tripId) {
        // when the user changes trip, smooth scroll to the last completed stop
        listState.animateScrollToItem(maxOf(0, trip.completedStops - 5))
    }

    LazyColumn(
        modifier = modifier,
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        itemsIndexed(trip.stopTimes) { index, stopTime ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 0.dp)
            ) {
                if (trip.lastEventReceivedAt != null) {
                    Icon(
                        imageVector = if (index < trip.completedStops) {
                            Icons.Filled.CheckCircle
                        } else {
                            Icons.Filled.RadioButtonUnchecked
                        },
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                }

                val highlight = stopTime.stop.stopId == stopIdToHighlight &&
                        stopTime.stop.type == stopTypeToHighlight
                BodyText(
                    text = stopTime.stop.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1.0f)
                        .padding(end = 6.dp),
                    fontWeight = if (highlight) FontWeight.Bold else null,
                    color = if (highlight) MaterialTheme.colorScheme.primary else Color.Unspecified
                )

                val isLate = trip.lastEventReceivedAt != null
                        && index >= trip.completedStops
                        && trip.delay > 0
                val lateDecoration = if (isLate) TextDecoration.LineThrough else null

                if (stopTime.arrivalTime != null) {
                    LabelText(
                        text = formatTime(stopTime.arrivalTime),
                        maxLines = 1,
                        textDecoration = lateDecoration
                    )
                }
                if (stopTime.arrivalTime != stopTime.departureTime) {
                    if (stopTime.arrivalTime != null) {
                        Icon(
                            imageVector = Icons.Filled.DoubleArrow,
                            contentDescription = null,
                            modifier = Modifier.size(8.dp)
                        )
                    }
                    if (stopTime.departureTime != null) {
                        LabelText(
                            text = formatTime(stopTime.departureTime),
                            maxLines = 1,
                            textDecoration = lateDecoration
                        )
                    }
                }
                if (isLate) {
                    sequenceOf(stopTime.arrivalTime, stopTime.departureTime)
                        .filter { it != null }
                        .firstOrNull()
                        ?.let { time ->
                            LabelText(
                                text = formatTime(
                                    time.plusMinutes(trip.delay.toLong())
                                ),
                                modifier = Modifier.padding(start = 5.dp),
                                color = MaterialTheme.colorScheme.error,
                                maxLines = 1
                            )
                        }
                }
            }
        }

        item {
            LabelText(
                text = if (trip.lastEventReceivedAt == null) {
                    stringResource(R.string.no_update)
                } else {
                    stringResource(R.string.last_update, formatTime(trip.lastEventReceivedAt))
                },
                modifier = Modifier.padding(8.dp)
            )
        }

        item {
            Spacer(modifier = Modifier.size(height = 84.dp, width = 0.dp))
        }
    }
}

@Composable
private fun TripViewBottomRow(
    setReferenceDateTime: (ZonedDateTime) -> Unit,
    loading: Boolean,
    onReload: () -> Unit,
    prevEnabled: Boolean,
    onPrevClicked: () -> Unit,
    nextEnabled: Boolean,
    onNextClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        FloatingActionButton(
            onClick = onPrevClicked,
            modifier = Modifier.alpha(if (prevEnabled) 1.0f else 0.5f)
        ) {
            Icon(
                imageVector = Icons.Filled.NavigateBefore,
                contentDescription = stringResource(R.string.previous)
            )
        }

        val context = LocalContext.current
        FloatingActionButton(
            onClick = { pickDateTime(context, setReferenceDateTime) }
        ) {
            Icon(
                imageVector = Icons.Filled.EditCalendar,
                contentDescription = stringResource(R.string.choose_date_time)
            )
        }

        FloatingActionButton(
            onClick = onReload
        ) {
            if (loading) {
                SmallCircularProgressIndicator()
            } else {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = stringResource(R.string.reload)
                )
            }
        }

        FloatingActionButton(
            onClick = onNextClicked,
            modifier = Modifier.alpha(if (nextEnabled) 1.0f else 0.5f)
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
            var loading by rememberSaveable { mutableStateOf(true) }
            val stopToHighlight = SampleDbStopProvider().values.first()
            TripView(
                trip = trip,
                setReferenceDateTime = {},
                loading = loading,
                onReload = { loading = !loading },
                prevEnabled = true,
                onPrevClicked = {},
                nextEnabled = false,
                onNextClicked = {},
                stopIdToHighlight = stopToHighlight.stopId,
                stopTypeToHighlight = stopToHighlight.type,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TripViewPreviewLoading() {
    AppTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            var loading by rememberSaveable { mutableStateOf(true) }
            TripView(
                trip = null,
                setReferenceDateTime = {},
                loading = loading,
                onReload = { loading = !loading },
                prevEnabled = true,
                onPrevClicked = {},
                nextEnabled = false,
                onNextClicked = {},
                stopIdToHighlight = null,
                stopTypeToHighlight = null,
            )
        }
    }
}
