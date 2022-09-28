package org.stypox.tridenta.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Traffic
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import org.stypox.tridenta.R
import org.stypox.tridenta.ui.destinations.LinesScreenDestination
import org.stypox.tridenta.ui.destinations.StopsScreenDestination
import org.stypox.tridenta.ui.nav.Drawer
import org.stypox.tridenta.ui.nav.DrawerItem
import org.stypox.tridenta.ui.theme.AppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    @OptIn(ExperimentalMaterial3Api::class)
                    Drawer(
                        items = listOf(
                            DrawerItem(R.string.lines, Icons.Filled.DirectionsBus, LinesScreenDestination),
                            DrawerItem(R.string.stops, Icons.Filled.Traffic, StopsScreenDestination)
                        )
                    )
                }
            }
        }
    }
}
