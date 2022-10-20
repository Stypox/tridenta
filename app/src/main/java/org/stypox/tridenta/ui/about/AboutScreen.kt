@file:OptIn(ExperimentalMaterial3Api::class)

package org.stypox.tridenta.ui.about

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import org.stypox.tridenta.R
import org.stypox.tridenta.ui.nav.*

@Destination(
    deepLinks = [DeepLink(uriPattern = DEEP_LINK_URL_PATTERN)]
)
@Composable
fun AboutScreen(navigationIconWrapper: NavigationIconWrapper) {
    AboutScreen(navigationIcon = navigationIconWrapper.navigationIcon)
}

@Preview
@Composable
private fun AboutScreen(navigationIcon: @Composable () -> Unit = { }) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = stringResource(R.string.about))
                },
                navigationIcon = navigationIcon,
                actions = {
                    val uriHandler = LocalUriHandler.current
                    AppBarOpenIcon {
                        uriHandler.openUri(GITHUB_REPO_URI)
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(state = rememberScrollState())
            ) {
                AboutItem(
                    icon = { modifier -> AppLauncherIcon(modifier) },
                    clipIcon = true,
                    title = stringResource(R.string.app_name),
                    description = stringResource(R.string.app_description),
                    buttonTextAndUri = null
                )

                AboutItem(
                    icon = Icons.Filled.BugReport,
                    clipIcon = false,
                    title = stringResource(R.string.found_a_bug),
                    description = stringResource(R.string.found_a_bug_description),
                    buttonTextAndUri = Pair(
                        stringResource(R.string.found_a_bug_open_issue),
                        GITHUB_ISSUE_URI
                    )
                )

                AboutItem(
                    icon = R.drawable.stypox,
                    clipIcon = true,
                    title = stringResource(R.string.author),
                    description = stringResource(R.string.author_description),
                    buttonTextAndUri = Pair(
                        stringResource(R.string.author_view_profile),
                        GITHUB_STYPOX_URI
                    )
                )

                AboutItem(
                    icon = R.drawable.fdroid,
                    clipIcon = false,
                    title = stringResource(R.string.fdroid),
                    description = stringResource(R.string.fdroid_description),
                    buttonTextAndUri = Pair(
                        stringResource(R.string.find_out_more),
                        FDROID_URI
                    )
                )

                AboutItem(
                    icon = R.drawable.mindshub,
                    clipIcon = false,
                    title = stringResource(R.string.mindshub),
                    description = stringResource(R.string.mindshub_description),
                    buttonTextAndUri = Pair(
                        stringResource(R.string.find_out_more),
                        MINDSHUB_URI
                    )
                )
            }
        }
    )
}

private const val GITHUB_STYPOX_URI = "https://github.com/Stypox"
private const val GITHUB_REPO_URI = "$GITHUB_STYPOX_URI/tridenta"
private const val GITHUB_ISSUE_URI = "$GITHUB_REPO_URI/issues"
private const val FDROID_URI = "https://f-droid.org"
private const val MINDSHUB_URI = "https://mindshub.it"