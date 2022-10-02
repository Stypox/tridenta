package org.stypox.tridenta.ui.lines

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.stypox.tridenta.enums.Area


@Composable
fun SelectAreaDialog(
    selectedArea: Area,
    setSelectedArea: (Area) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = MaterialTheme.colorScheme.background,
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                val onAreaClick = { area: Area ->
                    setSelectedArea(area)
                    onDismiss()
                }

                SuburbanAreasMap(
                    onAreaClick = onAreaClick,
                    modifier = Modifier
                        .widthIn(0.dp, 290.dp)
                )

                AreaChipGroup(
                    selectedArea = selectedArea,
                    onAreaClick = onAreaClick,
                    modifier = Modifier
                        .widthIn(0.dp, 500.dp)
                        .padding(top = 16.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun SelectAreaDialogPreview() {
    var selectedArea by rememberSaveable { mutableStateOf(Area.Suburban2) }
    SelectAreaDialog(
        selectedArea = selectedArea,
        setSelectedArea = { selectedArea = it },
        onDismiss = {}
    )
}