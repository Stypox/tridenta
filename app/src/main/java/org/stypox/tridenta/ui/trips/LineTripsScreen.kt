package org.stypox.tridenta.ui.trips

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import org.stypox.tridenta.R
import org.stypox.tridenta.repo.data.UiLine
import org.stypox.tridenta.repo.data.UiTrip
import org.stypox.tridenta.sample.SampleUiLineProvider
import org.stypox.tridenta.sample.SampleUiTripProvider
import org.stypox.tridenta.ui.lines.LineShortName
import org.stypox.tridenta.ui.nav.AppBarDrawerIcon
import org.stypox.tridenta.ui.nav.NavigationIconWrapper
import java.time.ZonedDateTime

@Destination(navArgsDelegate = LineTripsScreenNavArgs::class)
@Composable
fun LineTripsScreen(navigationIconWrapper: NavigationIconWrapper) {
    val lineTripsViewModel: LineTripsViewModel = hiltViewModel()
    val lineTripsUiState by lineTripsViewModel.uiState.collectAsState()

    LineTripsScreen(
        line = lineTripsUiState.line,
        setReferenceDateTime = lineTripsViewModel::setReferenceDateTime,
        trip = lineTripsUiState.trip,
        loading = lineTripsUiState.loading,
        onReload = lineTripsViewModel::onReload,
        prevEnabled = lineTripsUiState.prevEnabled,
        onPrevClicked = lineTripsViewModel::onPrevClicked,
        nextEnabled = lineTripsUiState.nextEnabled,
        onNextClicked = lineTripsViewModel::onNextClicked,
        navigationIcon = navigationIconWrapper.navigationIcon
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LineTripsScreen(
    line: UiLine?,
    setReferenceDateTime: (ZonedDateTime) -> Unit,
    trip: UiTrip?,
    loading: Boolean,
    onReload: () -> Unit,
    prevEnabled: Boolean,
    onPrevClicked: () -> Unit,
    nextEnabled: Boolean,
    onNextClicked: () -> Unit,
    navigationIcon: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            LineAppBar(
                line = line,
                setReferenceDateTime = setReferenceDateTime,
                navigationIcon = navigationIcon
            )
        },
        content = { paddingValues ->
            if (trip == null) {
                // TODO show loading or no-trip-found
            } else {
                TripView(
                    trip = trip,
                    loading = loading,
                    onReload = onReload,
                    prevEnabled = prevEnabled,
                    onPrevClicked = onPrevClicked,
                    nextEnabled = nextEnabled,
                    onNextClicked = onNextClicked,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LineAppBar(
    line: UiLine?,
    setReferenceDateTime: (ZonedDateTime) -> Unit,
    navigationIcon: @Composable () -> Unit
) {
    // TODO title's width isn't properly calculated when `navigationIcon` and `actions` have
    //  different widths, resulting in the overlap of the `title` and the widest of `actions` or
    //  `navigationIcon`, see https://issuetracker.google.com/issues/236994621
    CenterAlignedTopAppBar(
        title = {
            if (line != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = stringResource(R.string.trips_for_line))
                    LineShortName(color = line.color, shortName = line.shortName)
                }
            }
        },
        navigationIcon = navigationIcon,
        actions = {
            IconButton(onClick = { /* TODO */ }) {
                Icon(
                    imageVector = Icons.Filled.EditCalendar,
                    contentDescription = stringResource(R.string.choose_date_time)
                )
            }
            if (line != null && line.newsItems.isNotEmpty()) {
                IconButton(onClick = { /* TODO */ }) {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = stringResource(R.string.news_and_warnings)
                    )
                }
            }
        }
    )
}

@Preview
@Composable
private fun LineAppBarPreview(@PreviewParameter(SampleUiLineProvider::class) line: UiLine) {
    LineAppBar(line = line, setReferenceDateTime = {}, navigationIcon = { AppBarDrawerIcon {} })
}

@Preview
@Composable
private fun LineTripsViewPreview(@PreviewParameter(SampleUiTripProvider::class) trip: UiTrip) {
    LineTripsScreen(
        line = SampleUiLineProvider().values.find { uiLine -> uiLine.lineId == trip.line.lineId }!!,
        setReferenceDateTime = {},
        trip = trip,
        loading = false,
        onReload = {},
        prevEnabled = true,
        onPrevClicked = {},
        nextEnabled = false,
        onNextClicked = {},
        navigationIcon = { AppBarDrawerIcon {} }
    )
}
