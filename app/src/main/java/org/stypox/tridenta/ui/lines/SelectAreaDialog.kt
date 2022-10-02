package org.stypox.tridenta.ui.lines

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import org.stypox.tridenta.R
import org.stypox.tridenta.enums.Area


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SelectAreaDialog(
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
