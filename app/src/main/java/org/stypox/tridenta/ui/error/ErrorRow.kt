package org.stypox.tridenta.ui.error

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import org.stypox.tridenta.R
import org.stypox.tridenta.ui.destinations.LogsScreenDestination
import org.stypox.tridenta.ui.theme.BodyText

@Composable
fun ErrorRow(
    onRetry: () -> Unit,
    navigator: DestinationsNavigator,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.errorContainer,
        modifier = modifier,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            BodyText(
                text = stringResource(R.string.error),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(start = 16.dp, top = 4.dp, end = 4.dp, bottom = 4.dp)
                    .weight(1.0f),
                color = MaterialTheme.colorScheme.onErrorContainer,
            )

            IconButton(onClick = { navigator.navigate(LogsScreenDestination) }) {
                Icon(
                    imageVector = Icons.Filled.BugReport,
                    contentDescription = stringResource(R.string.logs),
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                )
            }

            IconButton(
                onClick = onRetry,
                modifier = Modifier.padding(end = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = stringResource(R.string.retry),
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xffffff)
@Composable
private fun ErrorRowPreview() {
    ErrorRow(onRetry = {}, navigator = EmptyDestinationsNavigator)
}