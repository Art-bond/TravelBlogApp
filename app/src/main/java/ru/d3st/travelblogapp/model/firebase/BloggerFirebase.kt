package ru.d3st.travelblogapp.model.firebase

import androidx.annotation.Keep

@Keep
data class BloggerFirebase(
    val uid: String,
    val email: String,
    val name: String,
    val photoUrl: String?,
    val videos: Long,
    val locations: Long,
) {
    @Suppress("unused")
    constructor() : this("", "", "", "", 0, 0)
}
