package org.stypox.tridenta.ui.line_trips

import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.stypox.tridenta.R
import org.stypox.tridenta.db.data.DbNewsItem
import org.stypox.tridenta.sample.SampleDbNewsItemProvider
import org.stypox.tridenta.ui.theme.BodyText
import org.stypox.tridenta.ui.theme.LabelText
import org.stypox.tridenta.ui.theme.TitleText
import org.stypox.tridenta.util.formatDateShort

@Composable
fun NewsItemsDialog(newsItems: List<DbNewsItem>, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = MaterialTheme.colorScheme.background,
            shape = MaterialTheme.shapes.large
        ) {
            LazyColumn(modifier = Modifier.padding(8.dp)) {
                items(newsItems) { newsItem ->
                    NewsItemView(newsItem = newsItem, onDismiss = onDismiss)
                }
            }
        }
    }
}

@Composable
fun NewsItemView(
    newsItem: DbNewsItem,
    onDismiss: () -> Unit
) {
    Surface(
        shape = MaterialTheme.shapes.medium
    ) {
        val uriHandler = LocalUriHandler.current
        val context = LocalContext.current
        val clipboardManager = LocalClipboardManager.current
        Column(
            modifier = Modifier
                .clickable {
                    onDismiss()
                    try {
                        uriHandler.openUri(newsItem.url)
                    } catch (e: Throwable) {
                        Toast.makeText(context, R.string.no_browser_available, LENGTH_LONG).show()
                        clipboardManager.setText(AnnotatedString(newsItem.url))
                    }
                }
                .padding(16.dp)
        ) {
            TitleText(text = newsItem.header)
            LabelText(text = stringResource(
                R.string.service_type_date_range,
                newsItem.serviceType,
                formatDateShort(newsItem.startDate),
                formatDateShort(newsItem.endDate),
            ))
            BodyText(text = newsItem.details)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun NewsItemViewPreview(@PreviewParameter(SampleDbNewsItemProvider::class) newsItem: DbNewsItem) {
    NewsItemView(newsItem = newsItem, onDismiss = {})
}