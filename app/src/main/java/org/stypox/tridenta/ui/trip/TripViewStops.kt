package org.stypox.tridenta.ui.trip

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import org.stypox.tridenta.R
import org.stypox.tridenta.enums.StopLineType
import org.stypox.tridenta.extractor.data.ExTrip
import org.stypox.tridenta.repo.data.UiStopTime
import org.stypox.tridenta.repo.data.UiTrip
import org.stypox.tridenta.sample.SampleUiTripProvider
import org.stypox.tridenta.ui.theme.BodyText
import org.stypox.tridenta.ui.theme.LabelText
import org.stypox.tridenta.util.formatConcatStrings
import org.stypox.tridenta.util.formatTime


@Composable
fun TripViewStops(
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
            TripViewStopItem(
                trip = trip,
                highlight = stopTime.stop != null &&
                    stopTime.stop.stopId == stopIdToHighlight &&
                    stopTime.stop.type == stopTypeToHighlight,
                completed = index < trip.completedStops,
                stopTime = stopTime
            )
        }

        item {
            LabelText(
                text = formatConcatStrings(
                    if (trip.lastEventReceivedAt == null) {
                        stringResource(R.string.no_update)
                    } else {
                        stringResource(R.string.last_update, formatTime(trip.lastEventReceivedAt))
                    },
                    if (trip.busId == ExTrip.BUS_ID_UNKNOWN) {
                        null
                    } else {
                        stringResource(R.string.bus_id, trip.busId)
                    }
                ),
                modifier = Modifier.padding(8.dp)
            )
        }

        item {
            // space for FABs
            Spacer(modifier = Modifier.size(height = 84.dp, width = 0.dp))
        }
    }
}

@Composable
private fun TripViewStopItem(
    trip: UiTrip,
    highlight: Boolean,
    completed: Boolean,
    stopTime: UiStopTime
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 0.dp)
    ) {
        Icon(
            imageVector = if (trip.lastEventReceivedAt == null) {
                Icons.Filled.ArrowRight
            } else if (completed) {
                Icons.Filled.CheckCircle
            } else {
                Icons.Filled.RadioButtonUnchecked
            },
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(end = 6.dp)
        )

        if (stopTime.stop?.isFavorite == true) {
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = stringResource(R.string.favorite),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 6.dp)
            )
        }

        BodyText(
            text = stopTime.stop?.name ?: stringResource(R.string.error),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1.0f)
                .run {
                    if (stopTime.arrivalTime == null && stopTime.departureTime == null) {
                        this // do not apply end padding if there is nothing after
                    } else {
                        padding(end = 6.dp)
                    }
                },
            fontWeight = if (highlight) FontWeight.Bold else null,
            color = if (stopTime.stop == null) {
                MaterialTheme.colorScheme.error
            } else if (highlight) {
                MaterialTheme.colorScheme.primary
            } else {
                Color.Unspecified
            }
        )

        val isLate = trip.lastEventReceivedAt != null
                && !completed
                && trip.delay > 0
        val lateDecoration = if (isLate) TextDecoration.LineThrough else null

        if (stopTime.arrivalTime != null) {
            // TODO `key` forces recompositions when `lateDecoration` changes, needed because of
            //  probably a bug in Compose (also see below)
            key(lateDecoration) {
                LabelText(
                    text = formatTime(stopTime.arrivalTime),
                    maxLines = 1,
                    textDecoration = lateDecoration
                )
            }
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
                key(lateDecoration) {
                    LabelText(
                        text = formatTime(stopTime.departureTime),
                        maxLines = 1,
                        textDecoration = lateDecoration
                    )
                }
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

@Preview
@Composable
fun TripViewStopsPreview(@PreviewParameter(SampleUiTripProvider::class) uiTrip: UiTrip) {
    TripViewStops(trip = uiTrip, stopIdToHighlight = null, stopTypeToHighlight = null)
}
