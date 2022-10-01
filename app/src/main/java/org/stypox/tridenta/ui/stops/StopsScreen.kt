package org.stypox.tridenta.ui.stops

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.stypox.tridenta.R
import org.stypox.tridenta.repo.data.UiStop
import org.stypox.tridenta.ui.destinations.LineTripsScreenDestination
import org.stypox.tridenta.ui.destinations.StopTripsScreenDestination
import org.stypox.tridenta.ui.nav.NavigationIconWrapper
import org.stypox.tridenta.ui.nav.SearchTopAppBar

@Destination
@Composable
fun StopsScreen(
    navigationIconWrapper: NavigationIconWrapper,
    navigator: DestinationsNavigator
) {
    val stopsViewModel: StopsViewModel = hiltViewModel()
    val stopsUiState by stopsViewModel.uiState.collectAsState()

    StopsScreen(
        stops = stopsUiState.stops,
        searchString = stopsUiState.searchString,
        setSearchString = stopsViewModel::setSearchString,
        navigationIcon = navigationIconWrapper.navigationIcon,
        navigator = navigator
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StopsScreen(
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
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxWidth()
            ) {
                items(stops) { stop ->
                    StopItem(
                        stop = stop,
                        onLineClick = { line ->
                            navigator.navigate(LineTripsScreenDestination(line.lineId, line.type))
                        },
                        modifier = Modifier.clickable {
                            navigator.navigate(StopTripsScreenDestination(stop.stopId, stop.type))
                        }
                    )
                }
            }
        }
    )
}