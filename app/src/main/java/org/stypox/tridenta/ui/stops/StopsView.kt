package org.stypox.tridenta.ui.stops

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
import androidx.lifecycle.viewmodel.compose.viewModel
import org.stypox.tridenta.R
import org.stypox.tridenta.repo.data.UiStop
import org.stypox.tridenta.ui.nav.SearchTopAppBar

@Composable
fun StopsView(
    navigationIcon: @Composable () -> Unit,
    stopsViewModel: StopsViewModel = viewModel()
) {
    val stopsUiState by stopsViewModel.uiState.collectAsState()

    StopsView(
        stops = stopsUiState.stops,
        searchString = stopsUiState.searchString,
        setSearchString = stopsViewModel::setSearchString,
        navigationIcon = navigationIcon
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StopsView(
    stops: List<UiStop>,
    searchString: String,
    setSearchString: (String) -> Unit,
    navigationIcon: @Composable () -> Unit
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
                items(stops) {
                    StopItem(stop = it)
                }
            }
        }
    )
}