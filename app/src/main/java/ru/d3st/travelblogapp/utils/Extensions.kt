package ru.d3st.travelblogapp.utils

import android.annotation.SuppressLint
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Удобный вызов метода определения текущего [LatLng]
 */
@SuppressLint("MissingPermission")
suspend fun FusedLocationProviderClient.currentLatLng(): LatLng = suspendCoroutine { continuation ->
    lastLocation.addOnSuccessListener { location ->
        location?.toLatLng()?.run { continuation.resume(this) }
    }
}

/**
 * Удобный вызов метода определения текущего [Location]
 */
@SuppressLint("MissingPermission")
suspend fun FusedLocationProviderClient.currentLocation(): Location = suspendCoroutine { continuation ->
    lastLocation.addOnSuccessListener { location ->
        location?.run { continuation.resume(this) }
    }
}

fun Location.toLatLng():LatLng = LatLng(this.latitude, this.longitude)