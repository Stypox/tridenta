package org.stypox.tridenta.ui.nav

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.FULL_ROUTE_PLACEHOLDER
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.rememberNavHostEngine
import com.ramcosta.composedestinations.spec.Direction
import kotlinx.coroutines.launch
import org.stypox.tridenta.ui.NavGraphs
import org.stypox.tridenta.ui.destinations.LinesScreenDestination
import org.stypox.tridenta.ui.theme.HeadlineText

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
