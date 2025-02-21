package org.stypox.tridenta.ui.nav

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.preference.PreferenceManager
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.FULL_ROUTE_PLACEHOLDER
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.rememberNavHostEngine
import com.ramcosta.composedestinations.spec.Direction
import kotlinx.coroutines.launch
import org.stypox.tridenta.R
import org.stypox.tridenta.ui.NavGraphs
import org.stypox.tridenta.ui.destinations.LinesScreenDestination
import org.stypox.tridenta.ui.theme.HeadlineText
import org.stypox.tridenta.util.PreferenceKeys

const val DEEP_LINK_PREFIX = "tridenta://"
const val DEEP_LINK_URL_PATTERN = DEEP_LINK_PREFIX + FULL_ROUTE_PLACEHOLDER

/**
 * @param drawerContent a composable that will be shown as the drawer content. The two parameters
 * passed to it are: the current navigation destination and a callback to change the destination.
 */
@Composable
fun Navigation(
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    drawerContent: @Composable ColumnScope.(NavDestination?, (Direction) -> Unit) -> Unit
) {
    val context = LocalContext.current
    var policyAccepted by rememberSaveable {
        mutableStateOf(
            PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PreferenceKeys.POLICY_ACCEPTED, false)
        )
    }
    if (!policyAccepted) {
        // Make sure the user accepts the "policy" (which is just a disclaimer saying that Tridenta
        // is not affiliated with the government). This code should probably be moved in a separate
        // file, but Play Store removed the app and I needed a simple solution to restore it.
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {
                TextButton(
                    onClick = {
                        PreferenceManager.getDefaultSharedPreferences(context)
                            .edit()
                            .putBoolean(PreferenceKeys.POLICY_ACCEPTED, true)
                            .apply()
                        policyAccepted = true
                    }
                ) {
                    Text(text = stringResource(R.string.accept))
                }
            },
            icon = { Icon(Icons.Default.Info, null) },
            text = { Text(text = stringResource(R.string.policy_disclaimer)) },
        )
        return
    }

    val scope = rememberCoroutineScope()
    val engine = rememberNavHostEngine()
    val navController = engine.rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    // when the drawer is open, back presses should be consumed and used to close the drawer
    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                drawerContent(navBackStackEntry?.destination) { direction ->
                    navController.navigate(direction)
                    scope.launch { drawerState.close() }
                }
            }
        },
        content = {
            DestinationsNavHost(
                navGraph = NavGraphs.root,
                startRoute = LinesScreenDestination,
                engine = engine,
                navController = navController,
                dependenciesContainerBuilder = {
                    dependency(NavigationIconWrapper {
                        AppBarDrawerIcon {
                            scope.launch {
                                if (drawerState.isOpen) {
                                    drawerState.close()
                                } else {
                                    drawerState.open()
                                }
                            }
                        }
                    })
                }
            )
        },
    )
}

@Preview
@Composable
private fun NavigationPreview() {
    Navigation(drawerState = rememberDrawerState(initialValue = DrawerValue.Open)) { _, _ ->
        HeadlineText(
            text = "Drawer content",
            modifier = Modifier
                .align(CenterHorizontally)
                .padding(32.dp)
        )
    }
}
