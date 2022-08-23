package org.stypox.tridenta.ui.line

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import org.stypox.tridenta.data.Area

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AreaChip(
    area: Area,
    selected: Boolean? = null,
    onClick: (Area) -> Unit = { },
    modifier: Modifier = Modifier
) {
    val iconTextColor by animateColorAsState(
        targetValue = if (selected == false) Color(0xffdddddd) else Color.White
    )

    Surface(
        color = Color(0xff000000 + area.color),
        // clip instead of doing shape= to ensure the touch ripple remains inside
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .clickable { onClick(area) },
    ) {
        Row(
            modifier = Modifier.padding(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AnimatedContent(targetState = selected) { targetState ->
                Image(
                    imageVector = if (targetState == true) Icons.Filled.RadioButtonChecked else area.icon,
                    contentDescription = area.icon.name,
                    colorFilter = ColorFilter.tint(iconTextColor)
                )
            }

            val weight by animateIntAsState(
                targetValue = (if (selected == true) FontWeight.ExtraBold else FontWeight.Normal).weight
            )
            Text(
                text = stringResource(id = area.nameRes),
                color = iconTextColor,
                fontWeight = FontWeight(weight),
                modifier = Modifier.padding(4.dp, 0.dp, 0.dp, 0.dp),
            )
        }
    }
}

@Composable
fun AreaChipGroup(selectedArea: MutableState<Area>, modifier: Modifier = Modifier) {
    @Composable
    fun AreaChipGroupRow(vararg areas: Area) {
        FlowRow(
            mainAxisSpacing = 8.dp,
            mainAxisAlignment = FlowMainAxisAlignment.Center,
            crossAxisSpacing = 4.dp,
        ) {
            areas.forEach { area ->
                AreaChip(
                    area = area,
                    selected = area == selectedArea.value,
                    onClick = { clickedArea -> selectedArea.value = clickedArea }
                )
            }
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
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

@Preview
@Composable
fun AreaChipPreview() {
    var selected by rememberSaveable { mutableStateOf(false) }
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