package dev.parcelview.feature.parcels

interface ParcelsRepository {
    suspend fun getParcels(): List<String>
}
