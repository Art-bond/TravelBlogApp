package ru.d3st.travelblogapp.model.domain

import com.google.android.gms.maps.model.LatLng
import java.util.*

data class LocationDomain(
    val latLng: LatLng,
    val time: Date
)