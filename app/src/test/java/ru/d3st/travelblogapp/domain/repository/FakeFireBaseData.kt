package ru.d3st.travelblogapp.domain.repository

import com.google.api.services.youtube.model.Video
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.GeoPoint
import ru.d3st.travelblogapp.data.firebase.IFireBaseData
import ru.d3st.travelblogapp.model.firebase.BloggerFirebase
import ru.d3st.travelblogapp.model.firebase.FirebaseLocation
import ru.d3st.travelblogapp.model.firebase.FirebaseVideo
import ru.d3st.travelblogapp.utils.Resource
import java.util.*

class FakeFireBaseData : IFireBaseData {

    var fakeLocation: FirebaseLocation = FirebaseLocation(GeoPoint(10.0, 20.0), Timestamp.now())
    var fakeFireBaseUser: BloggerFirebase = BloggerFirebase("", "", "", "", 0, 0)
    var fakeVideo: FirebaseVideo =
        FirebaseVideo("", "", "", "", "", Timestamp.now(), Timestamp.now(), 0, "")

    override fun addLocation(
        user: FirebaseUser,
        geoPosition: GeoPoint,
        time: Date,
        status: Boolean
    ) {
        fakeLocation = FirebaseLocation(geoPosition, Timestamp(time))
    }

    override fun createUser(firebaseUser: FirebaseUser) {
        fakeFireBaseUser = BloggerFirebase(
            firebaseUser.uid,
            firebaseUser.email!!,
            firebaseUser.displayName!!,
            firebaseUser.photoUrl.toString(),
            0,
            0
        )
    }


    override fun updateVideo(
        user: FirebaseUser,
        video: Video,
        startTS: Timestamp,
        endTS: Timestamp
    ) {
        fakeVideo = FirebaseVideo(
            video.id,
            video.snippet.title,
            video.snippet.description,
            video.snippet.channelId,
            video.snippet.channelTitle,
            startTS,
            endTS,
            video.snippet.publishedAt.value,
            video.snippet.thumbnails.high.url
        )
    }

    override suspend fun loadUsers(): List<BloggerFirebase> {
        TODO("Not yet implemented")
    }

    override suspend fun loadVideos(userId: String): List<FirebaseVideo> {
        TODO("Not yet implemented")
    }

    override suspend fun loadLocations(
        userId: String,
        start: Date,
        end: Date
    ): Resource<List<FirebaseLocation>> {
        TODO("Not yet implemented")
    }


}