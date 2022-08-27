package org.stypox.tridenta.ui.lines

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.stypox.tridenta.R
import org.stypox.tridenta.data.Area
import org.stypox.tridenta.data.Line
import org.stypox.tridenta.data.StopLineType
import org.stypox.tridenta.ui.theme.AppTheme

@Composable
fun LinesView(linesViewModel: LinesViewModel = viewModel()) {
    val linesUiState by linesViewModel.uiState.collectAsState()

    LinesView(
        lines = linesUiState.lines,
        selectedArea = linesUiState.selectedArea,
        headerExpanded = linesUiState.headerExpanded,
        setSelectedArea = linesViewModel::setSelectedArea,
        setHeaderExpanded = linesViewModel::setHeaderExpanded
    )
}

@Composable
private fun LinesView(
    lines: List<Line>,
    selectedArea: Area,
    headerExpanded: Boolean,
    setSelectedArea: (Area) -> Unit,
    setHeaderExpanded: (Boolean) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        item {
            LinesViewHeader(selectedArea, headerExpanded, setSelectedArea, setHeaderExpanded)
        }
        items(lines) {
            LineItem(line = it)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun LinesViewHeader(
    selectedArea: Area,
    expanded: Boolean,
    setSelectedArea: (Area) -> Unit,
    setHeaderExpanded: (Boolean) -> Unit
) {
    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        if (expanded) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                SuburbanAreasMap(
                    onAreaClick = setSelectedArea,
                    modifier = Modifier
                        .widthIn(0.dp, 300.dp)
                        .padding(8.dp)
                )

                AreaChipGroup(
                    selectedArea = selectedArea,
                    onAreaClick = setSelectedArea,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            AnimatedContent(targetState = expanded) { targetState ->
                if (targetState) {
                    Spacer(modifier = Modifier)
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .clickable { setHeaderExpanded(true) }
                            .padding(16.dp, 12.dp, 16.dp, 12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.selected_area),
                            style = MaterialTheme.typography.headlineMedium
                        )

                        AreaChip(area = selectedArea)
                    }
                }
            }

            IconButton(
                onClick = { setHeaderExpanded(!expanded) },
                modifier = Modifier.padding(8.dp)
            ) {
                val rotation by animateFloatAsState(
                    targetValue = if (expanded) 0.0f else 180.0f,
                    animationSpec = tween()
                )

                Icon(
                    imageVector = Icons.Filled.ExpandMore,
                    contentDescription = if (expanded) {
                        stringResource(R.string.show_less)
                    } else {
                        stringResource(R.string.show_more)
                    },
                    modifier = Modifier.rotate(rotation)
                )
            }
        }
    }
}

@Preview
@Preview(heightDp = 500) // smaller height to ensure scrolling works
@Composable
private fun LinesViewPreview() {
    val selectedArea = rememberSaveable { mutableStateOf(Area.Suburban3) }
    val expanded = rememberSaveable { mutableStateOf(true) }
    AppTheme {
        LinesView(
            lines = listOf(
                Line(
                    0,
                    Area.Suburban2,
                    null,
                    "Trento-Vezzano-Sarche-Tione",
                    "B201",
                    StopLineType.Suburban,
                    listOf()
                ),
                Line(
                    0,
                    Area.UrbanTrento,
                    0xc52720,
                    "Cortesano Gardolo P.Dante Villazzano 3",
                    "3",
                    StopLineType.Urban,
                    listOf()
                )
            ),
            selectedArea = selectedArea.value,
            headerExpanded = expanded.value,
            setSelectedArea = {
                selectedArea.value = it
                expanded.value = false
            },
            setHeaderExpanded = { expanded.value = it }
        )
    }
}