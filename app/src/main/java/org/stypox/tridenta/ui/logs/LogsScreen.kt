@file:OptIn(ExperimentalMaterial3Api::class)

package org.stypox.tridenta.ui.logs

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import org.stypox.tridenta.R
import org.stypox.tridenta.db.data.LogEntry
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
        navigationIcon = navigationIconWrapper.navigationIcon,
    )
}

@Composable
private fun LogsScreen(
    loading: Boolean,
    logs: List<LogEntry>,
    navigationIcon: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = stringResource(R.string.logs))
                },
                navigationIcon = navigationIcon,
            )
        },
        content = { paddingValues ->
            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing = loading),
                onRefresh = { },
                swipeEnabled = false, // live data reloads automatically
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxHeight()
            ) {
                LazyColumn {
                    items(logs) {
                        LogItem(logEntry = it)
                    }
                }
            }
        }
    )
}