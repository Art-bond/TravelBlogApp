package ru.d3st.travelblogapp.model.firebase

import androidx.annotation.Keep
import com.google.firebase.Timestamp

@Keep
data class FirebaseVideo(
    val id: String,
    val title: String,
    val description: String,
    val channelId: String,
    val channelTitle: String,
    val start: Timestamp,
    val end: Timestamp,
    val publishedAt: Long,
    val thumbnail: String,
) {
    @Suppress("unused")
    constructor() : this("", "", "", "", "", Timestamp.now(), Timestamp.now(), 0, "")
}
