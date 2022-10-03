package org.stypox.tridenta.ui.nav

data class DrawerUiState(
    val favorites: List<Any>,
    val history: List<Any>,
    val loading: Boolean
)