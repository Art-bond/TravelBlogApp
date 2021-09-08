package ru.d3st.travelblogapp.model.domain

data class Track(
    val videoId: String,
    val locationList: List<LocationDomain>
)