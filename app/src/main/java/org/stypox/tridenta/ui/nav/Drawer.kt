package org.stypox.tridenta.ui.nav

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.launch

data class DrawerItem(
    @StringRes val name: Int,
    val icon: ImageVector,
    val content: @Composable () -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerSheet(items: List<DrawerItem>, selectedIndex: Int, setSelectedIndex: (Int) -> Unit) {
    ModalDrawerSheet {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Drawer(items: List<DrawerItem>, initialSelectedIndex: Int) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedIndex by rememberSaveable { mutableStateOf(initialSelectedIndex) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerSheet(
                items = items,
                selectedIndex = selectedIndex,
                setSelectedIndex = {
                    selectedIndex = it
                    scope.launch { drawerState.close() }
                }
            )
        },
        content = items.getOrNull(selectedIndex)?.content ?: { Text("Error") }
    )
}