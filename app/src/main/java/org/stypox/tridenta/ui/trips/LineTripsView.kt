package org.stypox.tridenta.ui.trips

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.EntryPointAccessors
import org.stypox.tridenta.R
import org.stypox.tridenta.repo.data.UiLine
import org.stypox.tridenta.repo.data.UiTrip
import org.stypox.tridenta.sample.SampleUiLineProvider
import org.stypox.tridenta.sample.SampleUiTripProvider
import org.stypox.tridenta.ui.MainActivity
import org.stypox.tridenta.ui.lines.LineShortName
import org.stypox.tridenta.ui.nav.AppBarDrawerIcon
import java.time.ZonedDateTime

@Composable
fun LineTripsView(
    line: UiLine,
    navigationIcon: @Composable () -> Unit,
    lineTripsViewModel: LineTripsViewModel = viewModel(
        factory = LineTripsViewModel.provideFactory(
            EntryPointAccessors
                .fromActivity(
                    LocalContext.current as Activity,
                    MainActivity.ViewModelFactoryProvider::class.java
                )
                .lineTripsViewModelFactory(),
            line
        )
    )
) {
    val lineTripsUiState = lineTripsViewModel.uiState.value

    LineTripsView(
        line = line,
        navigationIcon = navigationIcon,
        setReferenceDateTime = lineTripsViewModel::setReferenceDateTime,
        trip = lineTripsUiState.trip,
        prevEnabled = lineTripsUiState.prevEnabled,
        onPrevClicked = lineTripsViewModel::onPrevClicked,
        nextEnabled = lineTripsUiState.nextEnabled,
        onNextClicked = lineTripsViewModel::onNextClicked
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LineTripsView(
    line: UiLine,
    navigationIcon: @Composable () -> Unit,
    setReferenceDateTime: (ZonedDateTime) -> Unit,
    trip: UiTrip?,
    prevEnabled: Boolean,
    onPrevClicked: () -> Unit,
    nextEnabled: Boolean,
    onNextClicked: () -> Unit
) {
    Scaffold(
        topBar = {
            LineAppBar(
                line = line,
                navigationIcon = navigationIcon,
                setReferenceDateTime = setReferenceDateTime
            )
        },
        content = { paddingValues ->
            if (trip == null) {
                // TODO show loading or no-trip-found
            } else {
                TripView(
                    trip = trip,
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
    line: UiLine,
    navigationIcon: @Composable () -> Unit,
    setReferenceDateTime: (ZonedDateTime) -> Unit
) {
    // TODO title's width isn't properly calculated when `navigationIcon` and `actions` have
    //  different widths, resulting in the overlap of the `title` and the widest of `actions` or
    //  `navigationIcon`, see https://issuetracker.google.com/issues/236994621
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
            IconButton(onClick = { /* TODO */ }) {
                Icon(
                    imageVector = Icons.Filled.EditCalendar,
                    contentDescription = stringResource(R.string.choose_date_time)
                )
            }
            if (line.newsItems.isNotEmpty()) {
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
    LineAppBar(line = line, navigationIcon = { AppBarDrawerIcon {} }, setReferenceDateTime = {})
}

@Preview
@Composable
private fun LineTripsViewPreview(@PreviewParameter(SampleUiTripProvider::class) trip: UiTrip) {
    LineTripsView(
        line = SampleUiLineProvider().values.find { uiLine -> uiLine.lineId == trip.line.lineId }!!,
        navigationIcon = { AppBarDrawerIcon {} },
        setReferenceDateTime = {},
        trip = trip,
        prevEnabled = true,
        onPrevClicked = {},
        nextEnabled = false,
        onNextClicked = {}
    )
}
