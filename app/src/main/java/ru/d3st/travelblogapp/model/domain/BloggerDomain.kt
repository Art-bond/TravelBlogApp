package ru.d3st.travelblogapp.model.domain

data class BloggerDomain(
    val uid: String,
    val email: String,
    val name: String,
    val photoUrl: String?,
    val videos: Long,
)
