package ru.d3st.travelblogapp.model.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class VideoDomain(
    val id: String,
    val title: String,
    val description: String,
    val channelId: String,
    val channelTitle: String,
    val start: Date,
    val end: Date,
    val thumbnail: String,
) : Parcelable
