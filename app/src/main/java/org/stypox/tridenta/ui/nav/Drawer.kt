@file:OptIn(ExperimentalMaterial3Api::class)

package org.stypox.tridenta.ui.nav

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Traffic
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import org.stypox.tridenta.R
import org.stypox.tridenta.ui.destinations.DirectionDestination
import org.stypox.tridenta.ui.destinations.LinesScreenDestination
import org.stypox.tridenta.ui.destinations.StopsScreenDestination
import org.stypox.tridenta.ui.theme.AppTheme
import org.stypox.tridenta.ui.theme.HeadlineText

data class DrawerItem(
    @StringRes val name: Int,
    val icon: ImageVector,
    val directionDestination: DirectionDestination,
)

private val BASE_DRAWER_ITEMS = listOf(
    DrawerItem(R.string.lines, Icons.Filled.DirectionsBus, LinesScreenDestination),
    DrawerItem(R.string.stops, Icons.Filled.Traffic, StopsScreenDestination)
)

@Composable
fun DrawerSheetContent(
    currentDestination: NavDestination?,
    setDestination: (DirectionDestination) -> Unit
) {
    DrawerHeader(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
    BASE_DRAWER_ITEMS.forEach { item ->
        NavigationDrawerItem(
            icon = { Icon(item.icon, contentDescription = null) },
            label = { Text(stringResource(item.name)) },
            selected = item.directionDestination.route == currentDestination?.route,
            onClick = { setDestination(item.directionDestination) },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
    }
}

@Composable
private fun DrawerHeader(modifier: Modifier = Modifier) {
    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            HeadlineText(
                text = stringResource(R.string.app_name),
                modifier = Modifier.padding(32.dp)
            )
            // TODO the icon looks bad, add info/settings button instead
            Icon(
                painter = painterResource(R.mipmap.ic_launcher_foreground),
                contentDescription = stringResource(R.string.app_name),
                tint = Color.Unspecified
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DrawerHeaderPreview() {
    AppTheme {
        DrawerHeader()
    }
}