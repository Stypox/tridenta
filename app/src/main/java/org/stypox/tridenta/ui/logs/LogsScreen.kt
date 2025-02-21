@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package org.stypox.tridenta.ui.logs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import org.stypox.tridenta.R
import org.stypox.tridenta.db.data.LogEntry
import org.stypox.tridenta.sample.SampleLogEntryProvider
import org.stypox.tridenta.ui.nav.DEEP_LINK_URL_PATTERN
import org.stypox.tridenta.ui.nav.NavigationIconWrapper

@Destination(
    deepLinks = [DeepLink(uriPattern = DEEP_LINK_URL_PATTERN)]
)
@Composable
fun LogsScreen(navigationIconWrapper: NavigationIconWrapper) {
    val logsViewModel: LogsViewModel = hiltViewModel()
    val logs by logsViewModel.logs.observeAsState(initial = null)

    LogsScreen(
        loading = logs == null,
        logs = logs ?: listOf(),
        onClearLogsClick = logsViewModel::clearLogs,
        navigationIcon = navigationIconWrapper.navigationIcon,
    )
}

@Composable
private fun LogsScreen(
    loading: Boolean,
    logs: List<LogEntry>,
    onClearLogsClick: () -> Unit,
    navigationIcon: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.logs))
                },
                navigationIcon = navigationIcon,
                actions = {
                    IconButton(onClick = onClearLogsClick) {
                        Icon(
                            imageVector = Icons.Filled.DeleteSweep,
                            contentDescription = stringResource(R.string.clear)
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            val pullToRefreshState = rememberPullToRefreshState()

            // need to rewrite PullToRefreshBox because it does not expose the `enabled` parameter
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxHeight()
                    .pullToRefresh(
                        state = pullToRefreshState,
                        isRefreshing = loading,
                        onRefresh = {},
                        enabled = false, // live data reloads automatically
                    ),
                contentAlignment = Alignment.TopStart
            ) {
                LazyColumn {
                    items(logs) {
                        LogItem(logEntry = it)
                    }
                }

                Indicator(
                    modifier = Modifier.align(Alignment.TopCenter),
                    isRefreshing = loading,
                    state = pullToRefreshState,
                )
            }
        }
    )
}

@Preview(backgroundColor = 0xffffff, showBackground = true)
@Composable
private fun LogsScreenPreview() {
    LogsScreen(
        loading = false,
        logs = SampleLogEntryProvider().values.toList(),
        onClearLogsClick = { },
        navigationIcon = { }
    )
}

@Preview(backgroundColor = 0xffffff, showBackground = true)
@Composable
private fun LogsScreenPreviewLoading() {
    LogsScreen(
        loading = true,
        logs = SampleLogEntryProvider().values.toList(),
        onClearLogsClick = { },
        navigationIcon = { }
    )
}