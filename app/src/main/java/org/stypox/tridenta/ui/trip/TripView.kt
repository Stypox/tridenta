package org.stypox.tridenta.ui.trip

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import org.stypox.tridenta.R
import org.stypox.tridenta.enums.StopLineType
import org.stypox.tridenta.repo.data.UiTrip
import org.stypox.tridenta.sample.SampleDbStopProvider
import org.stypox.tridenta.sample.SampleUiTripProvider
import org.stypox.tridenta.ui.destinations.StopTripsScreenDestination
import org.stypox.tridenta.ui.error.ErrorPanel
import org.stypox.tridenta.ui.error.ErrorRow
import org.stypox.tridenta.ui.theme.*
import org.stypox.tridenta.util.*
import java.time.ZonedDateTime

@Composable
fun TripView(
    trip: UiTrip?,
    setReferenceDateTime: (ZonedDateTime) -> Unit,
    error: Boolean,
    loading: Boolean,
    onReload: () -> Unit,
    prevEnabled: Boolean,
    onPrevClicked: () -> Unit,
    nextEnabled: Boolean,
    onNextClicked: () -> Unit,
    stopIdToHighlight: Int?,
    stopTypeToHighlight: StopLineType?,
    navigator: DestinationsNavigator,
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

                if (error) {
                    ErrorRow(
                        onRetry = onReload,
                        navigator = navigator,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                TripViewStops(
                    modifier = Modifier.weight(1.0f),
                    trip = trip,
                    stopIdToHighlight = stopIdToHighlight,
                    stopTypeToHighlight = stopTypeToHighlight,
                    onStopClick = { stop ->
                        navigator.navigate(
                            StopTripsScreenDestination(
                                stop.stopId,
                                stop.type
                            )
                        )
                    },
                )
            }

        } else if (loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

        } else if (error) {
            ErrorPanel(
                onRetry = onReload,
                navigator = navigator,
                modifier = Modifier.align(Alignment.Center),
            )

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
            reloadEnabled = loading || trip != null,
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
        // shortName (the trip line will be null only in case of error)
        if (trip.line != null) {
            val shortNameBackground = trip.line.color.toLineColor()
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
            StopLineTypeIcon(stopLineType = trip.type)
            DirectionIcon(direction = trip.direction)
        }
    }
}


@Composable
private fun TripViewBottomRow(
    setReferenceDateTime: (ZonedDateTime) -> Unit,
    loading: Boolean,
    reloadEnabled: Boolean,
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
            onClick = onReload,
            modifier = Modifier.alpha(if (reloadEnabled) 1.0f else 0.5f)
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
                error = false,
                loading = loading,
                onReload = { loading = !loading },
                prevEnabled = true,
                onPrevClicked = {},
                nextEnabled = false,
                onNextClicked = {},
                stopIdToHighlight = stopToHighlight.stopId,
                stopTypeToHighlight = stopToHighlight.type,
                navigator = EmptyDestinationsNavigator,
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
                error = false,
                loading = loading,
                onReload = { loading = !loading },
                prevEnabled = true,
                onPrevClicked = {},
                nextEnabled = false,
                onNextClicked = {},
                stopIdToHighlight = null,
                stopTypeToHighlight = null,
                navigator = EmptyDestinationsNavigator,
            )
        }
    }
}
