package org.stypox.tridenta.ui.stops

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.stypox.tridenta.R
import org.stypox.tridenta.data.Stop
import org.stypox.tridenta.ui.nav.SearchTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StopsView(
    stops: List<Stop>,
    searchString: String,
    setSearchString: (String) -> Unit,
    onDrawerClick: () -> Unit
) {
    Scaffold(
        topBar = {
            SearchTopAppBar(
                searchString = searchString,
                setSearchString = setSearchString,
                title = stringResource(R.string.stops),
                hint = stringResource(R.string.search_stop_hint),
                onDrawerClick = onDrawerClick
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