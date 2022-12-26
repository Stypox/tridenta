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
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.stypox.tridenta.repo.StopLineReloadHandler
import org.stypox.tridenta.ui.nav.DrawerSheetContent
import org.stypox.tridenta.ui.nav.Navigation
import org.stypox.tridenta.ui.theme.AppTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var stopLineReloadHandler: StopLineReloadHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigation { currentDestination, setDirection ->
                        DrawerSheetContent(
                            currentDestination = currentDestination,
                            setDirection = setDirection
                        )
                    }
                }
            }
        }

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                stopLineReloadHandler.reloadIfOutdatedData()
            }
        }
    }
}
