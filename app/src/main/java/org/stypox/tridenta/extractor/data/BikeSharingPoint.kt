package org.stypox.tridenta.extractor.data

data class BikeSharingPoint(
    val name: String,
    val address: String,
    val id: String,
    val usedSlots: Int,
    val availableSlots: Int,
    val totalSlots: Int,
    val latitude: Double,
    val longitude: Double,
)