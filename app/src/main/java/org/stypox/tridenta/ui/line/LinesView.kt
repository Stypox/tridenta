package org.stypox.tridenta.ui.line

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
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
                SuburbanAreasMap(
                    onAreaClick = { selectedArea.value = it },
                    modifier = Modifier
                        .widthIn(0.dp, 300.dp)
                        .padding(8.dp)
                )

                AreaChipGroup(
                    selectedArea = selectedArea,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            val alpha by animateFloatAsState(targetValue = if (expanded) 0.0f else 1.0f)
            AreaChip(
                area = selectedArea.value,
                modifier = Modifier
                    .padding(12.dp)
                    .alpha(alpha)
            )

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