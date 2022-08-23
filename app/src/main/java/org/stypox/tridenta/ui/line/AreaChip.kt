package org.stypox.tridenta.ui.line

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowRow
import org.stypox.tridenta.data.Area

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AreaChip(area: Area, selected: Boolean, onClick: (Area) -> Unit) {
    Surface(
        color = Color(0xff000000 + area.color),
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.clickable { onClick(area) },
    ) {
        Row(
            modifier = Modifier.padding(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AnimatedContent(
                targetState = selected,
            ) { targetState ->
                Image(
                    imageVector = if (targetState) Icons.Filled.RadioButtonChecked else area.icon,
                    contentDescription = area.icon.name,
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }

            val weight by animateIntAsState(
                targetValue = (if (selected) FontWeight.Bold else FontWeight.Normal).weight
            )
            Text(
                text = stringResource(id = area.nameRes),
                color = Color.White,
                fontWeight = FontWeight(weight),
                modifier = Modifier.padding(4.dp, 0.dp, 0.dp, 0.dp),
            )
        }
    }
}

@Composable
fun AreaChipGroup(selectedArea: MutableState<Area>) {
    @Composable
    fun AreaChipGroupRow(vararg areas: Area) {
        areas.forEach { area ->
            AreaChip(
                area = area,
                selected = area == selectedArea.value,
                onClick = { clickedArea -> selectedArea.value = clickedArea }
            )
        }
        Spacer(modifier = Modifier.fillMaxWidth()) // complete current row
        Spacer(modifier = Modifier.fillMaxWidth()) // add a separator
    }

    Column {
        FlowRow(
            mainAxisSpacing = 8.dp,
            crossAxisSpacing = 4.dp,
        ) {
            AreaChipGroupRow(
                Area.Suburban1,
                Area.Suburban2,
                Area.Suburban3,
                Area.Suburban4,
                Area.Suburban5,
                Area.Suburban6,
            )

            AreaChipGroupRow(
                Area.UrbanPergine,
                Area.UrbanAltoGarda,
                Area.UrbanRovereto,
                Area.UrbanTrento
            )

            AreaChipGroupRow(
                Area.Railway,
                Area.Funicular,
            )
        }
    }
}

@Preview
@Composable
fun AreaChipPreview() {
    var selected by remember { mutableStateOf(false) }
    AreaChip(area = Area.Railway, selected = selected, onClick = {
        selected = !selected
    })
}

// ensure it wraps correctly
@Preview(name = "Area chip group, max width")
@Preview(name = "Area chip group, small width", widthDp = 200)
@Composable
fun AreaChipGroupPreview() {
    AreaChipGroup(selectedArea = mutableStateOf(Area.UrbanAltoGarda))
}