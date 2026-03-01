package dev.parcelview.app.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable data object ParcelsList : NavKey
@Serializable data class ParcelDetail(val trackingId: String) : NavKey

@Serializable data object Scanner : NavKey

@Serializable data object Settings : NavKey

enum class Tab { PARCELS, SCANNER, SETTINGS }
