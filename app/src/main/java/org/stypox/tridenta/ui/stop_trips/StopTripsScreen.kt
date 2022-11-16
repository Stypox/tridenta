package org.stypox.tridenta.ui.stop_trips

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import org.stypox.tridenta.db.data.DbStop
import org.stypox.tridenta.repo.data.UiTrip
import org.stypox.tridenta.sample.SampleDbStopProvider
import org.stypox.tridenta.sample.SampleUiTripProvider
import org.stypox.tridenta.ui.nav.AppBarDrawerIcon
import org.stypox.tridenta.ui.nav.AppBarFavoriteIcon
import org.stypox.tridenta.ui.nav.DEEP_LINK_URL_PATTERN
import org.stypox.tridenta.ui.nav.NavigationIconWrapper
import org.stypox.tridenta.ui.theme.SmallCircularProgressIndicator
import org.stypox.tridenta.ui.trip.TripView
import org.stypox.tridenta.util.LifecycleAwareRepeatedAction
import java.time.ZonedDateTime

@Destination(
    navArgsDelegate = StopTripsScreenNavArgs::class,
    deepLinks = [DeepLink(uriPattern = DEEP_LINK_URL_PATTERN)]
)
@Composable
fun StopTripsScreen(
    navigationIconWrapper: NavigationIconWrapper,
    navigator: DestinationsNavigator
) {
    val stopTripsViewModel: StopTripsViewModel = hiltViewModel()
    val stopTripsUiState by stopTripsViewModel.uiState.collectAsState()
    val isFavorite by stopTripsViewModel.isFavorite.observeAsState(initial = false)

    LifecycleAwareRepeatedAction(millisInterval = 10000) {
        if (
            !stopTripsUiState.loading &&
            stopTripsUiState.trip?.let { it.completedStops < it.stopTimes.size } == true
        ) {
            stopTripsViewModel.onReload() // reload the trip every ten seconds while app is active
        }
    }

    StopTripsScreen(
        stop = stopTripsUiState.stop,
        setReferenceDateTime = stopTripsViewModel::setReferenceDateTime,
        trip = stopTripsUiState.trip,
        error = stopTripsUiState.error,
        loading = stopTripsUiState.loading,
        onReload = stopTripsViewModel::onReload,
        prevEnabled = stopTripsUiState.prevEnabled,
        onPrevClicked = stopTripsViewModel::onPrevClicked,
        nextEnabled = stopTripsUiState.nextEnabled,
        onNextClicked = stopTripsViewModel::onNextClicked,
        isFavorite = isFavorite,
        onFavoriteClicked = stopTripsViewModel::onFavoriteClicked,
        navigator = navigator,
        navigationIcon = navigationIconWrapper.navigationIcon
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StopTripsScreen(
    stop: DbStop?,
    setReferenceDateTime: (ZonedDateTime) -> Unit,
    trip: UiTrip?,
    error: Boolean,
    loading: Boolean,
    onReload: () -> Unit,
    prevEnabled: Boolean,
    onPrevClicked: () -> Unit,
    nextEnabled: Boolean,
    onNextClicked: () -> Unit,
    isFavorite: Boolean,
    onFavoriteClicked: () -> Unit,
    navigator: DestinationsNavigator,
    navigationIcon: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            StopAppBar(
                stop = stop,
                isFavorite = isFavorite,
                onFavoriteClicked = onFavoriteClicked,
                navigationIcon = navigationIcon
            )
        },
        content = { paddingValues ->
            TripView(
                trip = trip,
                setReferenceDateTime = setReferenceDateTime,
                error = error,
                loading = loading,
                onReload = onReload,
                prevEnabled = prevEnabled,
                onPrevClicked = onPrevClicked,
                nextEnabled = nextEnabled,
                onNextClicked = onNextClicked,
                stopIdToHighlight = stop?.stopId,
                stopTypeToHighlight = stop?.type,
                navigator = navigator,
                modifier = Modifier.padding(paddingValues)
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StopAppBar(
    stop: DbStop?,
    isFavorite: Boolean,
    onFavoriteClicked: () -> Unit,
    navigationIcon: @Composable () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            if (stop == null) {
                SmallCircularProgressIndicator()
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
            //  different widths, resulting in `title` and `navigationIcon` overlapping. Fortunately
            //  the favorite icon here compensates that, otherwise 48dp of Spacer would be needed.
            //  https://issuetracker.google.com/issues/236994621
            AppBarFavoriteIcon(
                isFavorite = isFavorite,
                onFavoriteClicked = onFavoriteClicked
            )
        }
    )
}

@Preview
@Composable
private fun StopAppBarPreview(@PreviewParameter(SampleDbStopProvider::class) stop: DbStop) {
    StopAppBar(
        stop = stop,
        isFavorite = false,
        onFavoriteClicked = {},
        navigationIcon = { AppBarDrawerIcon {} }
    )
}

@Preview
@Composable
private fun StopAppBarLoadingPreview() {
    StopAppBar(
        stop = null,
        isFavorite = true,
        onFavoriteClicked = {},
        navigationIcon = { AppBarDrawerIcon {} }
    )
}

@Preview
@Composable
private fun LineTripsViewPreview(@PreviewParameter(SampleUiTripProvider::class) trip: UiTrip) {
    StopTripsScreen(
        stop = trip.stopTimes.first().stop,
        setReferenceDateTime = {},
        trip = trip,
        error = false,
        loading = false,
        onReload = {},
        prevEnabled = true,
        onPrevClicked = {},
        nextEnabled = false,
        onNextClicked = {},
        isFavorite = false,
        onFavoriteClicked = {},
        navigator = EmptyDestinationsNavigator,
        navigationIcon = { AppBarDrawerIcon {} }
    )
}
