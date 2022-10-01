package org.stypox.tridenta.ui.lines

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import org.stypox.tridenta.R
import org.stypox.tridenta.db.data.DbLine
import org.stypox.tridenta.enums.Area
import org.stypox.tridenta.sample.SampleDbLineProvider
import org.stypox.tridenta.ui.destinations.LineTripsScreenDestination
import org.stypox.tridenta.ui.nav.AppBarDrawerIcon
import org.stypox.tridenta.ui.nav.NavigationIconWrapper
import org.stypox.tridenta.ui.theme.AppTheme

@Destination
@Composable
fun LinesScreen(
    navigationIconWrapper: NavigationIconWrapper,
    navigator: DestinationsNavigator
) {
    val linesViewModel: LinesViewModel = hiltViewModel()
    val linesUiState by linesViewModel.uiState.collectAsState()

    LinesScreen(
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
                        AreaChip(area = selectedArea)
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
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxWidth()
            ) {
                items(lines) {
                    LineItem(
                        line = it,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navigator.navigate(LineTripsScreenDestination(it.lineId, it.type))
                            }
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SelectAreaDialog(
    selectedArea: Area,
    setSelectedArea: (Area) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = null,
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                val onAreaClick = { area: Area ->
                    setSelectedArea(area)
                    onDismiss()
                }

                SuburbanAreasMap(
                    onAreaClick = onAreaClick,
                    modifier = Modifier
                        .widthIn(0.dp, 290.dp)
                        .padding(8.dp)
                )

                AreaChipGroup(
                    selectedArea = selectedArea,
                    onAreaClick = onAreaClick,
                    modifier = Modifier
                        .widthIn(0.dp, 500.dp)
                        .padding(top = 16.dp)
                )
            }
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
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