package org.stypox.tridenta.ui.logs

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.stypox.tridenta.R
import org.stypox.tridenta.db.data.LogEntry
import org.stypox.tridenta.sample.SampleLogEntryProvider
import org.stypox.tridenta.ui.theme.BodyText
import org.stypox.tridenta.ui.theme.LabelText
import org.stypox.tridenta.ui.theme.LogLevelIcon
import org.stypox.tridenta.util.formatDateTimeShort

private val ICON_SIZE = 24.dp

@Preview(showBackground = true, backgroundColor = 0xffffff)
@Composable
fun LogItem(@PreviewParameter(SampleLogEntryProvider::class) logEntry: LogEntry) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .animateContentSize()
            .clickable { expanded = !expanded }
    ) {
        val modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
        if (expanded) {
            LogItemExpanded(logEntry = logEntry, modifier = modifier)
        } else {
            LogItemUnexpanded(logEntry = logEntry, modifier = modifier)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xffffff)
@Composable
private fun LogItemUnexpanded(
    @PreviewParameter(SampleLogEntryProvider::class) logEntry: LogEntry,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        LogLevelIcon(
            logLevel = logEntry.logLevel,
            modifier = Modifier.size(ICON_SIZE)
        )
        Text(
            text = logEntry.text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = if (logEntry.stackTrace == null)
                MaterialTheme.typography.bodyMedium
            else
                MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xffffff)
@Composable
private fun LogItemExpanded(
    @PreviewParameter(SampleLogEntryProvider::class) logEntry: LogEntry,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            LogLevelIcon(
                logLevel = logEntry.logLevel,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(ICON_SIZE)
            )

            LabelText(
                text = formatDateTimeShort(logEntry.dateTime),
                modifier = Modifier.align(Alignment.Center)
            )

            val clipboardManager = LocalClipboardManager.current
            IconButton(
                onClick = {
                    clipboardManager.setText(
                        AnnotatedString("${logEntry.text}\n\n-----\n\n${logEntry.stackTrace}")
                    )
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(ICON_SIZE)
            ) {
                Icon(
                    imageVector = Icons.Filled.ContentCopy,
                    contentDescription = stringResource(R.string.copy_to_clipboard)
                )
            }
        }

        Text(
            text = logEntry.text,
            textAlign = TextAlign.Center,
            style = if (logEntry.stackTrace == null)
                MaterialTheme.typography.bodyMedium
            else
                MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
        )

        if (logEntry.stackTrace != null) {
            BodyText(
                text = logEntry.stackTrace,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                lineHeight = 13.sp,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState(0))
            )
        }
    }
}