package org.stypox.tridenta.ui.lines

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import org.stypox.tridenta.R
import org.stypox.tridenta.db.data.DbLine
import org.stypox.tridenta.enums.Area
import org.stypox.tridenta.sample.SampleDbLineProvider
import org.stypox.tridenta.ui.destinations.LineTripsScreenDestination
import org.stypox.tridenta.ui.nav.AppBarDrawerIcon
import org.stypox.tridenta.ui.nav.DEEP_LINK_URL_PATTERN
import org.stypox.tridenta.ui.nav.NavigationIconWrapper
import org.stypox.tridenta.ui.theme.AppTheme

@Destination(
    deepLinks = [DeepLink(uriPattern = DEEP_LINK_URL_PATTERN)]
)
@Composable
fun LinesScreen(
    navigationIconWrapper: NavigationIconWrapper,
    navigator: DestinationsNavigator
) {
    val linesViewModel: LinesViewModel = hiltViewModel()
    val linesUiState by linesViewModel.uiState.collectAsState()

    LinesScreen(
        loading = linesUiState.loading,
        onReload = linesViewModel::onReload,
        lines = linesUiState.lines,
        selectedArea = linesUiState.selectedArea,
        setSelectedArea = linesViewModel::setSelectedArea,
        navigationIcon = navigationIconWrapper.navigationIcon,
        navigator = navigator
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LinesScreen(
    loading: Boolean,
    onReload: () -> Unit,
    lines: List<DbLine>,
    selectedArea: Area,
    setSelectedArea: (Area) -> Unit,
    navigationIcon: @Composable () -> Unit,
    navigator: DestinationsNavigator
) {
    var showAreaDialog by rememberSaveable { mutableStateOf(false) }
    if (showAreaDialog) {
        SelectAreaDialog(
            selectedArea = selectedArea,
            setSelectedArea = setSelectedArea,
            onDismiss = { showAreaDialog = false }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = stringResource(R.string.selected_area))
                        AreaChip(
                            area = selectedArea,
                            onClick = { showAreaDialog = true }
                        )
                    }
                },
                navigationIcon = navigationIcon,
                actions = {
                    IconButton(onClick = { showAreaDialog = true }) {
                        Icon(
                            imageVector = Icons.Filled.FilterAlt,
                            contentDescription = stringResource(R.string.select_area)
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing = loading),
                onRefresh = onReload,
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxHeight()
            ) {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(lines) {
                        LineItem(
                            line = it,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navigator.navigate(
                                        LineTripsScreenDestination(
                                            lineId = it.lineId,
                                            lineType = it.type
                                        )
                                    )
                                }
                        )
                    }
                }
            }
        }
    )
}

@Preview
@Preview(heightDp = 500) // smaller height to ensure scrolling works
@Composable
private fun LinesViewPreview() {
    val selectedArea = rememberSaveable { mutableStateOf(Area.Suburban3) }
    val showAreaDialog = rememberSaveable { mutableStateOf(true) }
    AppTheme {
        LinesScreen(
            loading = true,
            onReload = {},
            lines = SampleDbLineProvider().values.toList(),
            selectedArea = selectedArea.value,
            setSelectedArea = {
                selectedArea.value = it
                showAreaDialog.value = false
            },
            navigationIcon = { AppBarDrawerIcon {} },
            navigator = EmptyDestinationsNavigator
        )
    }
}