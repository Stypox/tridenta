package org.stypox.tridenta.ui.lines

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import org.stypox.tridenta.ui.theme.LabelText
import org.stypox.tridenta.util.toComposeColor

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AreaChip(
    area: Area,
    selected: Boolean? = null,
    onClick: ((Area) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val iconTextColor by animateColorAsState(
        targetValue = if (selected == false) Color(0xffdddddd) else Color.White
    )

    var clickableModifier = modifier.clip(MaterialTheme.shapes.small)
    if (onClick != null) {
        clickableModifier = clickableModifier.clickable { onClick(area) }
    }

    Surface(
        color = area.color.toComposeColor(),
        // clip instead of doing shape= to ensure the touch ripple remains inside
        modifier = clickableModifier
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

            // once supported by the compose material3 library, font grade instead of weight should
            // be used, so that the width of the text does not change
            val weight by animateIntAsState(
                targetValue = (if (selected == true) FontWeight.ExtraBold else FontWeight.Normal).weight
            )
            LabelText(
                text = stringResource(id = area.nameRes),
                color = iconTextColor,
                fontWeight = FontWeight(weight),
                modifier = Modifier.padding(start = 4.dp),
            )
        }
    }
}

@Composable
fun AreaChipGroup(selectedArea: Area, onAreaClick: (Area) -> Unit, modifier: Modifier = Modifier) {
    @Composable
    fun AreaChipGroupRow(vararg areas: Area) {
        FlowRow(
            mainAxisSpacing = 4.dp,
            mainAxisAlignment = FlowMainAxisAlignment.Center,
            crossAxisSpacing = 4.dp,
        ) {
            areas.forEach { area ->
                AreaChip(
                    area = area,
                    selected = area == selectedArea,
                    onClick = onAreaClick
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
    var selectedArea by rememberSaveable { mutableStateOf(Area.UrbanAltoGarda) }
    AreaChipGroup(selectedArea = selectedArea, onAreaClick = { selectedArea = it })
}