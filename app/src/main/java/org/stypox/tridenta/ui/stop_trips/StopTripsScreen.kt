package org.stypox.tridenta.ui.stop_trips

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import org.stypox.tridenta.db.data.DbStop
import org.stypox.tridenta.repo.data.UiTrip
import org.stypox.tridenta.sample.SampleDbStopProvider
import org.stypox.tridenta.sample.SampleUiTripProvider
import org.stypox.tridenta.ui.nav.AppBarDrawerIcon
import org.stypox.tridenta.ui.nav.NavigationIconWrapper
import org.stypox.tridenta.ui.trip.TripView
import java.time.ZonedDateTime

@Destination(navArgsDelegate = StopTripsScreenNavArgs::class)
@Composable
fun StopTripsScreen(navigationIconWrapper: NavigationIconWrapper) {
    val stopTripsViewModel: StopTripsViewModel = hiltViewModel()
    val stopTripsUiState by stopTripsViewModel.uiState.collectAsState()

    StopTripsScreen(
        stop = stopTripsUiState.stop,
        setReferenceDateTime = stopTripsViewModel::setReferenceDateTime,
        trip = stopTripsUiState.trip,
        loading = stopTripsUiState.loading,
        onReload = stopTripsViewModel::onReload,
        prevEnabled = stopTripsUiState.prevEnabled,
        onPrevClicked = stopTripsViewModel::onPrevClicked,
        nextEnabled = stopTripsUiState.nextEnabled,
        onNextClicked = stopTripsViewModel::onNextClicked,
        navigationIcon = navigationIconWrapper.navigationIcon
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StopTripsScreen(
    stop: DbStop?,
    setReferenceDateTime: (ZonedDateTime) -> Unit,
    trip: UiTrip?,
    loading: Boolean,
    onReload: () -> Unit,
    prevEnabled: Boolean,
    onPrevClicked: () -> Unit,
    nextEnabled: Boolean,
    onNextClicked: () -> Unit,
    navigationIcon: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            StopAppBar(
                stop = stop,
                navigationIcon = navigationIcon
            )
        },
        content = { paddingValues ->
            TripView(
                trip = trip,
                setReferenceDateTime = setReferenceDateTime,
                loading = loading,
                onReload = onReload,
                prevEnabled = prevEnabled,
                onPrevClicked = onPrevClicked,
                nextEnabled = nextEnabled,
                onNextClicked = onNextClicked,
                modifier = Modifier.padding(paddingValues)
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StopAppBar(
    stop: DbStop?,
    navigationIcon: @Composable () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            if (stop == null) {
                CircularProgressIndicator()
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stop.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        },
        navigationIcon = navigationIcon,
        actions = {
            // TODO title's width isn't properly calculated when `navigationIcon` and `actions` have
            //  different widths, resulting in `title` and `navigationIcon` overlapping. Remove this
            //  else when the upstream issue gets fixed:
            //  https://issuetracker.google.com/issues/236994621
            Spacer(modifier = Modifier.width(48.dp))
        }
    )
}

@Preview
@Composable
private fun StopAppBarPreview(@PreviewParameter(SampleDbStopProvider::class) stop: DbStop) {
    StopAppBar(stop = stop, navigationIcon = { AppBarDrawerIcon {} })
}

@Preview
@Composable
private fun StopAppBarLoadingPreview() {
    StopAppBar(stop = null, navigationIcon = { AppBarDrawerIcon {} })
}

@Preview
@Composable
private fun LineTripsViewPreview(@PreviewParameter(SampleUiTripProvider::class) trip: UiTrip) {
    StopTripsScreen(
        stop = trip.stopTimes.first().stop,
        setReferenceDateTime = {},
        trip = trip,
        loading = false,
        onReload = {},
        prevEnabled = true,
        onPrevClicked = {},
        nextEnabled = false,
        onNextClicked = {},
        navigationIcon = { AppBarDrawerIcon {} }
    )
}
