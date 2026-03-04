package dev.parcelview.feature.parcels

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable data object ParcelsList : NavKey
@Serializable data class ParcelDetail(val trackingId: String) : NavKey
