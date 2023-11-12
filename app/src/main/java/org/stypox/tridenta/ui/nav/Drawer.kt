@file:OptIn(ExperimentalMaterial3Api::class)

package org.stypox.tridenta.ui.nav

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination
import com.ramcosta.composedestinations.spec.Direction
import org.stypox.tridenta.R
import org.stypox.tridenta.db.data.DbLine
import org.stypox.tridenta.db.data.DbStop
import org.stypox.tridenta.db.views.HistoryLineOrStop
import org.stypox.tridenta.ui.destinations.*
import org.stypox.tridenta.ui.lines.LineShortName
import org.stypox.tridenta.ui.theme.*

data class DrawerItem(
    val name: String,
    val icon: @Composable () -> Unit,
    val destination: Direction?,
)

data class DrawerSection(
    val name: String?,
    val icon: ImageVector?,
    val items: List<DrawerItem>?
)


@Composable
fun DrawerSheetContent(
    currentDestination: NavDestination?,
    setDirection: (Direction) -> Unit
) {
    val drawerViewModel: DrawerViewModel = hiltViewModel()
    val favorites by drawerViewModel.favorites.observeAsState(initial = null)
    val history by drawerViewModel.history.observeAsState(initial = null)

    DrawerSheetContent(
        currentDestination = currentDestination,
        setDirection = setDirection,
        sections = listOf(
            getBaseDrawerSection(),
            getHistoryDrawerSection(
                name = stringResource(R.string.favorites),
                icon = Icons.Filled.Favorite,
                items = favorites
            ),
            getHistoryDrawerSection(
                name = stringResource(R.string.history),
                icon = Icons.Filled.History,
                items = history
            ),
        )
    )
}

@Composable
fun DrawerSheetContent(
    currentDestination: NavDestination?,
    setDirection: (Direction) -> Unit,
    sections: List<DrawerSection>
) {
    DrawerHeader(
        setDirection = setDirection,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )

    LazyColumn {
        sections.forEach { section ->
            drawerSectionView(
                currentDestination = currentDestination,
                setDirection = setDirection,
                section = section
            )
        }
        item { 
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DrawerHeader(setDirection: (Direction) -> Unit, modifier: Modifier = Modifier) {
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
                modifier = Modifier.padding(horizontal = 28.dp, vertical = 24.dp)
            )

            Row(
                modifier = Modifier.padding(20.dp)
            ) {
                IconButton(onClick = { setDirection(LogsScreenDestination) }) {
                    Icon(
                        imageVector = Icons.Filled.BugReport,
                        contentDescription = stringResource(R.string.logs),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }

                IconButton(onClick = { setDirection(AboutScreenDestination) }) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = stringResource(R.string.about),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

private fun LazyListScope.drawerSectionView(
    currentDestination: NavDestination?,
    setDirection: (Direction) -> Unit,
    section: DrawerSection
) {
    if (section.items?.isEmpty() == true) {
        return // if the items are loaded and there are no items in this section, do not show it
    }

    if (section.name != null && section.icon != null) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 32.dp, top = 32.dp, end = 8.dp, bottom = 8.dp)
            ) {
                Icon(
                    imageVector = section.icon,
                    contentDescription = section.name,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                LabelText(
                    text = section.name,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }

    if (section.items == null) {
        item {
            SmallCircularProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        return
    }

    items(section.items) { item ->
        NavigationDrawerItem(
            icon = item.icon,
            label = {
                Text(
                    text = item.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            selected = item.destination?.let { it.route == currentDestination?.route } ?: false,
            onClick = { item.destination?.let(setDirection) },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
    }
}

@Composable
private fun getBaseDrawerSection(): DrawerSection {
    return DrawerSection(
        name = null,
        icon = null,
        items = listOf(
            DrawerItem(
                name = stringResource(R.string.lines),
                icon = {
                    Icon(
                        imageVector = Icons.Filled.DirectionsBus,
                        contentDescription = stringResource(R.string.lines)
                    )
                },
                destination = LinesScreenDestination
            ),
            DrawerItem(
                name = stringResource(R.string.stops),
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Traffic,
                        contentDescription = stringResource(R.string.stops)
                    )
                },
                destination = StopsScreenDestination
            )
        )
    )
}

private fun getHistoryDrawerSection(
    name: String,
    icon: ImageVector,
    items: List<HistoryLineOrStop>?
): DrawerSection {
    return DrawerSection(
        name = name,
        icon = icon,
        items = items?.map { historyEntry ->
            when (val item = historyEntry.intoLineOrStop()) {
                is DbLine -> DrawerItem(
                    name = item.longName,
                    icon = {
                        LineShortName(
                            color = item.color,
                            shortName = item.shortName,
                            isFavorite = false,
                        )
                    },
                    destination = LineTripsScreenDestination(item.lineId, item.type)
                )

                is DbStop -> DrawerItem(
                    name = item.name,
                    icon = {
                        Row {
                            StopLineTypeIcon(stopLineType = item.type)
                            if (item.cardinalPoint != null) {
                                TitleText(
                                    modifier = Modifier.padding(start = 4.dp),
                                    text = stringResource(item.cardinalPoint.shortName),
                                )
                            }
                        }
                    },
                    destination = StopTripsScreenDestination(item.stopId, item.type)
                )

                else -> DrawerItem(
                    name = "Error",
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Error,
                            contentDescription = "Error"
                        )
                    },
                    destination = null
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun DrawerHeaderPreview() {
    AppTheme {
        DrawerHeader({})
    }
}