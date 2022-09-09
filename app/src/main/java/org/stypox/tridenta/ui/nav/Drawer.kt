@file:OptIn(ExperimentalMaterial3Api::class)

package org.stypox.tridenta.ui.nav

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.stypox.tridenta.R
import org.stypox.tridenta.ui.theme.HeadlineText

data class DrawerItem(
    @StringRes val name: Int,
    val icon: ImageVector,
    val content: @Composable (@Composable () -> Unit) -> Unit,
)

@Preview
@Composable
fun DrawerHeader() {
    HeadlineText(
        text = stringResource(R.string.app_name),
        modifier = Modifier.padding(32.dp)
    )
}

@Composable
fun DrawerSheet(items: List<DrawerItem>, selectedIndex: Int, setSelectedIndex: (Int) -> Unit) {
    ModalDrawerSheet {
        DrawerHeader()
        items.forEachIndexed { index, item ->
            NavigationDrawerItem(
                icon = { Icon(item.icon, contentDescription = null) },
                label = { Text(stringResource(item.name)) },
                selected = index == selectedIndex,
                onClick = { setSelectedIndex(index) },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
    }
}

@Composable
fun Drawer(
    items: List<DrawerItem>,
    initialSelectedIndex: Int,
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed)
) {
    val scope = rememberCoroutineScope()
    var selectedIndex by rememberSaveable { mutableStateOf(initialSelectedIndex) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Column {
                DrawerSheet(
                    items = items,
                    selectedIndex = selectedIndex,
                    setSelectedIndex = {
                        selectedIndex = it
                        scope.launch { drawerState.close() }
                    }
                )
            }
        },
        content = items.getOrNull(selectedIndex)?.let { item ->
            {
                item.content {
                    // the drawer icon can be rotated according to the current drawer position with
                    // Modifier.rotate(drawerState.offset.value / LocalDensity.current.density)
                    AppBarDrawerIcon {
                        scope.launch {
                            if (drawerState.isOpen) {
                                drawerState.close()
                            } else {
                                drawerState.open()
                            }
                        }
                    }
                }
            }
        } ?: { Text("Error") }
    )
}

@Preview
@Composable
fun DrawerPreview() {
    Drawer(
        items = listOf(
            DrawerItem(
                name = R.string.app_name,
                icon = Icons.Filled.BugReport,
                content = { navigationIcon ->
                    Column {
                        navigationIcon()
                        HeadlineText(
                            text = "Content",
                            modifier = Modifier.padding(32.dp)
                        )
                    }
                }
            )
        ),
        initialSelectedIndex = 0,
        drawerState = rememberDrawerState(DrawerValue.Open)
    )
}
