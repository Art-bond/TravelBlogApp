package ru.d3st.travelblogapp.model.firebase

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

@Keep
data class FirebaseLocation(
    val geoPosition: GeoPoint,
    val timestamp: Timestamp,
) {
    @Suppress("unused")
    constructor() : this(GeoPoint(0.0, 0.0), Timestamp.now())
}