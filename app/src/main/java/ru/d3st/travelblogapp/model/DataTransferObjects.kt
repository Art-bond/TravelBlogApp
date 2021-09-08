package ru.d3st.travelblogapp.model

import ru.d3st.travelblogapp.model.domain.BloggerDomain
import ru.d3st.travelblogapp.model.domain.VideoDomain
import ru.d3st.travelblogapp.model.firebase.BloggerFirebase
import ru.d3st.travelblogapp.model.firebase.FirebaseVideo


fun List<FirebaseVideo>.asDomainModel(): List<VideoDomain> {
    return map{
        VideoDomain(
            id = it.id,
            title = it.title,
            description = it.description,
            channelId = it.channelId,
            channelTitle = it.channelTitle,
            start = it.start.toDate(),
            end = it.end.toDate(),
            thumbnail = it.thumbnail
        )
    }
}

@JvmName("asDomainModelBloggerFirebase")
fun List<BloggerFirebase>.asDomainModel(): List<BloggerDomain> {
    return map { firebaseUser ->
        BloggerDomain(
            uid = firebaseUser.uid,
            email = firebaseUser.email,
            name = firebaseUser.name,
            photoUrl = firebaseUser.photoUrl,
            videos = firebaseUser.videos
        )
    }
}

