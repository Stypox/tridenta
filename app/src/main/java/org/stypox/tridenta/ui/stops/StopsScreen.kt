package org.stypox.tridenta.ui.stops

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import org.stypox.tridenta.R
import org.stypox.tridenta.repo.data.UiStop
import org.stypox.tridenta.sample.SampleUiStopProvider
import org.stypox.tridenta.ui.destinations.LineTripsScreenDestination
import org.stypox.tridenta.ui.destinations.StopTripsScreenDestination
import org.stypox.tridenta.ui.error.ErrorPanel
import org.stypox.tridenta.ui.nav.AppBarDrawerIcon
import org.stypox.tridenta.ui.nav.DEEP_LINK_URL_PATTERN
import org.stypox.tridenta.ui.nav.NavigationIconWrapper
import org.stypox.tridenta.ui.nav.SearchTopAppBar

@Destination(
    deepLinks = [DeepLink(uriPattern = DEEP_LINK_URL_PATTERN)]
)
@Composable
fun StopsScreen(
    navigationIconWrapper: NavigationIconWrapper,
    navigator: DestinationsNavigator
) {
    val stopsViewModel: StopsViewModel = hiltViewModel()
    val stopsUiState by stopsViewModel.uiState.collectAsState()

    StopsScreen(
        error = stopsUiState.error,
        loading = stopsUiState.loading,
        onReload = stopsViewModel::onReload,
        stops = stopsUiState.stops,
        searchString = stopsUiState.searchString,
        setSearchString = stopsViewModel::setSearchString,
        navigationIcon = navigationIconWrapper.navigationIcon,
        navigator = navigator
    )
}

@Composable
private fun StopsScreen(
    error: Boolean,
    loading: Boolean,
    onReload: () -> Unit,
    stops: List<UiStop>,
    searchString: String,
    setSearchString: (String) -> Unit,
    navigationIcon: @Composable () -> Unit,
    navigator: DestinationsNavigator
) {
    Scaffold(
        topBar = {
            SearchTopAppBar(
                searchString = searchString,
                setSearchString = setSearchString,
                title = stringResource(R.string.stops),
                hint = stringResource(R.string.search_stop_hint),
                navigationIcon = navigationIcon
            )
        },
        content = { paddingValues ->
            if (error) {
                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                ) {
                    ErrorPanel(
                        onRetry = onReload,
                        navigator = navigator,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

            } else {
                SwipeRefresh(
                    state = rememberSwipeRefreshState(isRefreshing = loading),
                    onRefresh = onReload,
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxHeight()
                ) {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(stops) { stop ->
                            StopItem(
                                stop = stop,
                                onLineClick = { line ->
                                    navigator.navigate(
                                        LineTripsScreenDestination(
                                            lineId = line.lineId,
                                            lineType = line.type,
                                            stopIdToHighlight = stop.stopId,
                                            stopTypeToHighlight = stop.type
                                        )
                                    )
                                },
                                modifier = Modifier.clickable {
                                    navigator.navigate(
                                        StopTripsScreenDestination(
                                            stopId = stop.stopId,
                                            stopType = stop.type
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}

@Preview
@Composable
private fun StopsScreenPreview() {
    var showSearchString by rememberSaveable { mutableStateOf(true) }
    StopsScreen(
        error = false,
        loading = false,
        onReload = {},
        stops = SampleUiStopProvider().values.toList(),
        searchString = if (showSearchString) "Search string" else "",
        setSearchString = {},
        navigationIcon = {
            AppBarDrawerIcon { showSearchString = !showSearchString }
        },
        navigator = EmptyDestinationsNavigator,
    )
}