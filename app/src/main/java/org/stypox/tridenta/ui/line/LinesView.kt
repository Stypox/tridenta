package org.stypox.tridenta.ui.line

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
import org.stypox.tridenta.R
import org.stypox.tridenta.data.Area
import org.stypox.tridenta.data.Line
import org.stypox.tridenta.data.StopLineType
import org.stypox.tridenta.ui.theme.AppTheme

@Composable
fun LinesView(lines: List<Line>, selectedArea: MutableState<Area>) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        LinesViewHeader(selectedArea)

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(lines) {
                LineItem(line = it)
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LinesViewHeader(selectedArea: MutableState<Area>) {
    var expanded by rememberSaveable { mutableStateOf(true) }

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        if (expanded) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val onAreaClick = { area: Area ->
                    selectedArea.value = area
                    expanded = false // un-expand once the user tapped on an area chip
                }

                SuburbanAreasMap(
                    onAreaClick = onAreaClick,
                    modifier = Modifier
                        .widthIn(0.dp, 300.dp)
                        .padding(8.dp)
                )

                AreaChipGroup(
                    selectedArea = selectedArea.value,
                    onAreaClick = onAreaClick,
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
                            .clickable { expanded = true }
                            .padding(16.dp, 12.dp, 16.dp, 12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.selected_line),
                            style = MaterialTheme.typography.headlineMedium
                        )

                        AreaChip(area = selectedArea.value)
                    }
                }
            }

            IconButton(
                onClick = { expanded = !expanded },
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
@Composable
fun LinesViewPreview() {
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
            selectedArea = mutableStateOf(Area.Suburban3)
        )
    }
}