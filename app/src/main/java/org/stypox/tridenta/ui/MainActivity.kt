@file:OptIn(ExperimentalMaterial3Api::class)

package org.stypox.tridenta.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import org.stypox.tridenta.ui.nav.DrawerSheetContent
import org.stypox.tridenta.ui.nav.Navigation
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
                    Navigation { currentDestination, setDestination ->
                        DrawerSheetContent(
                            currentDestination = currentDestination,
                            setDestination = setDestination
                        )
                    }
                }
            }
        }
    }
}
