package org.stypox.tridenta.ui.lines

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import org.stypox.tridenta.R
import org.stypox.tridenta.db.data.DbLine
import org.stypox.tridenta.enums.Area
import org.stypox.tridenta.sample.SampleDbLineProvider
import org.stypox.tridenta.ui.theme.AppTheme

@Composable
fun LinesView(
    navigationIcon: @Composable () -> Unit,
    linesViewModel: LinesViewModel = viewModel()
) {
    val linesUiState by linesViewModel.uiState.collectAsState()

    LinesView(
        lines = linesUiState.lines,
        selectedArea = linesUiState.selectedArea,
        setSelectedArea = linesViewModel::setSelectedArea,
        showAreaDialog = linesUiState.showAreaDialog,
        setShowAreaDialog = linesViewModel::setShowAreaDialog,
        navigationIcon = navigationIcon
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LinesView(
    lines: List<DbLine>,
    selectedArea: Area,
    setSelectedArea: (Area) -> Unit,
    showAreaDialog: Boolean,
    setShowAreaDialog: (Boolean) -> Unit,
    navigationIcon: @Composable () -> Unit
) {
    if (showAreaDialog) {
        SelectAreaDialog(
            selectedArea = selectedArea,
            setSelectedArea = setSelectedArea,
            setShowAreaDialog = setShowAreaDialog
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
                    IconButton(onClick = { setShowAreaDialog(true) }) {
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
                    LineItem(line = it)
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
    setShowAreaDialog: (Boolean) -> Unit
) {
    AlertDialog(
        onDismissRequest = { setShowAreaDialog(false) },
        confirmButton = {
            TextButton(onClick = { setShowAreaDialog(false) }) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = null,
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                SuburbanAreasMap(
                    onAreaClick = setSelectedArea,
                    modifier = Modifier
                        .widthIn(0.dp, 290.dp)
                        .padding(8.dp)
                )

                AreaChipGroup(
                    selectedArea = selectedArea,
                    onAreaClick = setSelectedArea,
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
        LinesView(
            lines = SampleDbLineProvider().values.toList(),
            selectedArea = selectedArea.value,
            setSelectedArea = {
                selectedArea.value = it
                showAreaDialog.value = false
            },
            showAreaDialog = showAreaDialog.value,
            setShowAreaDialog = { showAreaDialog.value = it },
            navigationIcon = { }
        )
    }
}