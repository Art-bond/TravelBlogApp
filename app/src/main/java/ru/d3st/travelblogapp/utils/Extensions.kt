package ru.d3st.travelblogapp.utils

import android.annotation.SuppressLint
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@SuppressLint("MissingPermission")
suspend fun FusedLocationProviderClient.currentLocation(): LatLng = suspendCoroutine { continuation ->
    lastLocation.addOnSuccessListener { location ->
        location?.let { LatLng(it.latitude, it.longitude) }?.run { continuation.resume(this) }
    }
}