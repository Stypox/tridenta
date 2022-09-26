package org.stypox.tridenta.ui.trips

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import org.stypox.tridenta.R
import org.stypox.tridenta.db.data.DbLine
import org.stypox.tridenta.extractor.data.ExLine
import org.stypox.tridenta.repo.data.UiLine
import org.stypox.tridenta.repo.data.UiTrip
import org.stypox.tridenta.sample.SampleDbLineProvider
import org.stypox.tridenta.sample.SampleUiLineProvider
import org.stypox.tridenta.sample.SampleUiTripProvider
import org.stypox.tridenta.ui.lines.LineShortName
import org.stypox.tridenta.ui.nav.AppBarDrawerIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LineTripsView(
    line: UiLine,
    navigationIcon: @Composable () -> Unit,
    trip: UiTrip,
    prevEnabled: Boolean,
    onPrevClicked: () -> Unit,
    nextEnabled: Boolean,
    onNextClicked: () -> Unit
) {
    Scaffold(
        topBar = {
            LineAppBar(line = line, navigationIcon = navigationIcon)
        },
        content = { paddingValues ->
            TripView(
                trip = trip,
                prevEnabled = prevEnabled,
                onPrevClicked = onPrevClicked,
                nextEnabled = nextEnabled,
                onNextClicked = onNextClicked,
                modifier = Modifier.padding(paddingValues)
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LineAppBar(line: UiLine, navigationIcon: @Composable () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = stringResource(R.string.trips_for_line))
                LineShortName(color = line.color, shortName = line.shortName)
            }
        },
        navigationIcon = navigationIcon,
        actions = {
            if (line.newsItems.isNotEmpty()) {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = stringResource(R.string.news_and_warnings)
                    )
                }
            } else {
                // TODO title's width isn't properly calculated when `navigationIcon` and
                //  `actions` have different widths, resulting in `title` and
                //  `navigationIcon` overlapping. Remove this else when the upstream issue
                //  gets fixed: https://issuetracker.google.com/issues/236994621
                Spacer(modifier = Modifier.width(48.dp))
            }
        }
    )
}

@Preview
@Composable
private fun LineAppBarPreview(@PreviewParameter(SampleUiLineProvider::class) line: UiLine) {
    LineAppBar(line = line) { AppBarDrawerIcon {} }
}

@Preview
@Composable
private fun LineTripsViewPreview(@PreviewParameter(SampleUiTripProvider::class) trip: UiTrip) {
    LineTripsView(
        line = SampleUiLineProvider().values.first(),
        navigationIcon = { AppBarDrawerIcon {} },
        trip = trip,
        prevEnabled = true,
        onPrevClicked = {},
        nextEnabled = false,
        onNextClicked = {}
    )
}
