package org.stypox.tridenta.ui.line_trips

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import org.stypox.tridenta.R
import org.stypox.tridenta.enums.Direction
import org.stypox.tridenta.enums.StopLineType
import org.stypox.tridenta.repo.data.UiLine
import org.stypox.tridenta.repo.data.UiTrip
import org.stypox.tridenta.sample.SampleUiLineProvider
import org.stypox.tridenta.sample.SampleUiTripProvider
import org.stypox.tridenta.ui.lines.LineShortName
import org.stypox.tridenta.ui.nav.AppBarDrawerIcon
import org.stypox.tridenta.ui.nav.AppBarFavoriteIcon
import org.stypox.tridenta.ui.nav.DEEP_LINK_URL_PATTERN
import org.stypox.tridenta.ui.nav.NavigationIconWrapper
import org.stypox.tridenta.ui.theme.DirectionIcon
import org.stypox.tridenta.ui.theme.SmallCircularProgressIndicator
import org.stypox.tridenta.ui.trip.TripView
import org.stypox.tridenta.util.LifecycleAwareRepeatedAction
import java.time.ZonedDateTime

@Destination(
    navArgsDelegate = LineTripsScreenNavArgs::class,
    deepLinks = [DeepLink(uriPattern = DEEP_LINK_URL_PATTERN)]
)
@Composable
fun LineTripsScreen(
    navigationIconWrapper: NavigationIconWrapper,
    navigator: DestinationsNavigator
) {
    val lineTripsViewModel: LineTripsViewModel = hiltViewModel()
    val lineTripsUiState by lineTripsViewModel.uiState.collectAsState()
    val isFavorite by lineTripsViewModel.isFavorite.observeAsState(initial = false)

    LifecycleAwareRepeatedAction(millisInterval = 10000) {
        if (
            !lineTripsUiState.loading &&
            lineTripsUiState.trip?.let { it.completedStops < it.stopTimes.size } == true
        ) {
            lineTripsViewModel.onReload() // reload the trip every ten seconds while app is active
        }
    }

    LineTripsScreen(
        line = lineTripsUiState.line,
        setReferenceDateTime = lineTripsViewModel::setReferenceDateTime,
        trip = lineTripsUiState.trip,
        error = lineTripsUiState.error,
        loading = lineTripsUiState.loading,
        onReload = lineTripsViewModel::onReload,
        prevEnabled = lineTripsUiState.prevEnabled,
        onPrevClicked = lineTripsViewModel::onPrevClicked,
        nextEnabled = lineTripsUiState.nextEnabled,
        onNextClicked = lineTripsViewModel::onNextClicked,
        stopIdToHighlight = lineTripsUiState.stopIdToHighlight,
        stopTypeToHighlight = lineTripsUiState.stopTypeToHighlight,
        isFavorite = isFavorite,
        onFavoriteClicked = lineTripsViewModel::onFavoriteClicked,
        directionFilter = lineTripsUiState.directionFilter,
        onDirectionClicked = lineTripsViewModel::onDirectionClicked,
        navigator = navigator,
        navigationIcon = navigationIconWrapper.navigationIcon
    )
}

@Composable
private fun LineTripsScreen(
    line: UiLine?,
    setReferenceDateTime: (ZonedDateTime) -> Unit,
    trip: UiTrip?,
    error: Boolean,
    loading: Boolean,
    onReload: () -> Unit,
    prevEnabled: Boolean,
    onPrevClicked: () -> Unit,
    nextEnabled: Boolean,
    onNextClicked: () -> Unit,
    stopIdToHighlight: Int?,
    stopTypeToHighlight: StopLineType?,
    isFavorite: Boolean,
    onFavoriteClicked: () -> Unit,
    directionFilter: Direction,
    onDirectionClicked: () -> Unit,
    navigator: DestinationsNavigator,
    navigationIcon: @Composable () -> Unit,
) {
    Scaffold(
        topBar = {
            LineAppBar(
                line = line,
                isFavorite = isFavorite,
                onFavoriteClicked = onFavoriteClicked,
                directionFilter = directionFilter,
                onDirectionClicked = onDirectionClicked,
                navigationIcon = navigationIcon,
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
                stopIdToHighlight = stopIdToHighlight,
                stopTypeToHighlight = stopTypeToHighlight,
                navigator = navigator,
                modifier = Modifier.padding(paddingValues)
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LineAppBar(
    line: UiLine?,
    isFavorite: Boolean,
    onFavoriteClicked: () -> Unit,
    directionFilter: Direction,
    onDirectionClicked: () -> Unit,
    navigationIcon: @Composable () -> Unit,
) {
    var showNewsItemsDialog by rememberSaveable { mutableStateOf(false) }
    if (showNewsItemsDialog) {
        if (line == null) {
            showNewsItemsDialog = false
        } else {
            NewsItemsDialog(
                newsItems = line.newsItems,
                onDismiss = { showNewsItemsDialog = false }
            )
        }
    }

    TopAppBar(
        title = {
            if (line == null) {
                SmallCircularProgressIndicator()
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = stringResource(R.string.trips_for_line))
                    LineShortName(
                        color = line.color,
                        shortName = line.shortName,
                        isFavorite = false
                    )
                }
            }
        },
        navigationIcon = navigationIcon,
        actions = {
            if (line != null && line.newsItems.isNotEmpty()) {
                IconButton(onClick = { showNewsItemsDialog = true }) {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = stringResource(R.string.news_and_warnings)
                    )
                }
            }
            IconButton(onClick = onDirectionClicked) {
                // without `key` it would miss some updates... this is a bug in Compose
                key(directionFilter) {
                    DirectionIcon(direction = directionFilter)
                }
            }
            AppBarFavoriteIcon(isFavorite = isFavorite, onFavoriteClicked = onFavoriteClicked)
        }
    )
}

@Preview
@Composable
private fun LineAppBarPreview(@PreviewParameter(SampleUiLineProvider::class) line: UiLine) {
    LineAppBar(
        line = line,
        isFavorite = true,
        onFavoriteClicked = {},
        directionFilter = Direction.entries[line.lineId % Direction.entries.size],
        onDirectionClicked = {},
        navigationIcon = { AppBarDrawerIcon {} },
    )
}

@Preview
@Composable
private fun LineAppBarLoadingPreview() {
    LineAppBar(
        line = null,
        isFavorite = false,
        onFavoriteClicked = {},
        directionFilter = Direction.ForwardAndBackward,
        onDirectionClicked = {},
        navigationIcon = { AppBarDrawerIcon {} },
    )
}

@Preview
@Composable
private fun LineTripsViewPreview(@PreviewParameter(SampleUiTripProvider::class) trip: UiTrip) {
    val stopToHighlight = trip.stopTimes.lastOrNull()?.stop!!
    LineTripsScreen(
        line = SampleUiLineProvider().values.find { uiLine -> uiLine.lineId == trip.lineId }!!,
        setReferenceDateTime = {},
        trip = trip,
        error = false,
        loading = false,
        onReload = {},
        prevEnabled = false,
        onPrevClicked = {},
        nextEnabled = true,
        onNextClicked = {},
        stopIdToHighlight = stopToHighlight.stopId,
        stopTypeToHighlight = stopToHighlight.type,
        isFavorite = true,
        onFavoriteClicked = {},
        navigator = EmptyDestinationsNavigator,
        directionFilter = Direction.entries[trip.completedStops % Direction.entries.size],
        onDirectionClicked = {},
        navigationIcon = { AppBarDrawerIcon {} }
    )
}
