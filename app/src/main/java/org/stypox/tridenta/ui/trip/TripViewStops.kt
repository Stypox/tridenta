package org.stypox.tridenta.ui.trip

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DoubleArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.stypox.tridenta.R
import org.stypox.tridenta.enums.StopLineType
import org.stypox.tridenta.repo.data.UiStopTime
import org.stypox.tridenta.repo.data.UiTrip
import org.stypox.tridenta.ui.theme.BodyText
import org.stypox.tridenta.ui.theme.LabelText
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
                highlight = stopTime.stop.stopId == stopIdToHighlight &&
                    stopTime.stop.type == stopTypeToHighlight,
                completed = index < trip.completedStops,
                stopTime = stopTime
            )
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
                && !completed
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