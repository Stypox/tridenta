@file:OptIn(ExperimentalMaterial3Api::class)

package org.stypox.tridenta.ui.nav

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.rememberNavHostEngine
import kotlinx.coroutines.launch
import org.stypox.tridenta.R
import org.stypox.tridenta.ui.NavGraphs
import org.stypox.tridenta.ui.destinations.DirectionDestination
import org.stypox.tridenta.ui.destinations.LinesScreenDestination
import org.stypox.tridenta.ui.destinations.PlaceholderScreenDestination
import org.stypox.tridenta.ui.theme.HeadlineText

data class DrawerItem(
    @StringRes val name: Int,
    val icon: ImageVector,
    val directionDestination: DirectionDestination,
)

@Preview
@Composable
private fun DrawerHeader() {
    HeadlineText(
        text = stringResource(R.string.app_name),
        modifier = Modifier.padding(32.dp)
    )
}

@Composable
private fun DrawerSheet(
    items: List<DrawerItem>,
    selectedRoute: String?,
    setDirectionDestination: (DirectionDestination) -> Unit
) {
    ModalDrawerSheet {
        DrawerHeader()
        items.forEach { item ->
            NavigationDrawerItem(
                icon = { Icon(item.icon, contentDescription = null) },
                label = { Text(stringResource(item.name)) },
                selected = item.directionDestination.route == selectedRoute,
                onClick = { setDirectionDestination(item.directionDestination) },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
    }
}

@Composable
fun Drawer(
    items: List<DrawerItem>,
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed)
) {
    val scope = rememberCoroutineScope()
    val engine = rememberNavHostEngine()
    val navController = engine.rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column {
                DrawerSheet(
                    items = items,
                    selectedRoute = currentDestination?.route,
                    setDirectionDestination = { directionDestination ->
                        navController.navigate(directionDestination)
                        scope.launch { drawerState.close() }
                    }
                )
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
        }
    )
}

@Preview
@Composable
private fun DrawerPreview() {
    Drawer(
        items = listOf(
            DrawerItem(
                name = R.string.app_name,
                icon = Icons.Filled.BugReport,
                directionDestination = PlaceholderScreenDestination
            )
        ),
        drawerState = rememberDrawerState(DrawerValue.Open)
    )
}
