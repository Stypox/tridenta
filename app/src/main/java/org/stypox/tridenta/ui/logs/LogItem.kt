package org.stypox.tridenta.ui.logs

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.stypox.tridenta.db.data.LogEntry
import org.stypox.tridenta.sample.SampleLogEntryProvider
import org.stypox.tridenta.ui.theme.BodyText
import org.stypox.tridenta.ui.theme.LabelText
import org.stypox.tridenta.ui.theme.LogLevelIcon
import org.stypox.tridenta.util.formatDateTimeShort

@Preview(showBackground = true, backgroundColor = 0xffffff)
@Composable
fun LogItem(@PreviewParameter(SampleLogEntryProvider::class) logEntry: LogEntry) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = Modifier.animateContentSize()
    ) {
        val modifier = Modifier.clickable { expanded = !expanded }
            .fillMaxWidth()
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
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        LogLevelIcon(logLevel = logEntry.logLevel)
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
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            LogLevelIcon(logLevel = logEntry.logLevel)
            LabelText(
                text = formatDateTimeShort(logEntry.dateTime),
                modifier = Modifier.padding(start = 8.dp)
            )
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