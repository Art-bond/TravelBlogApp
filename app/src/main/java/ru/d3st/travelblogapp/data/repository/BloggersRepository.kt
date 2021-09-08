package ru.d3st.travelblogapp.data.repository

import android.location.Location
import com.google.api.services.youtube.model.Video
import com.google.firebase.Timestamp
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.GeoPoint
import ru.d3st.travelblogapp.data.firebase.FireBaseData
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BloggersRepository @Inject constructor(private val fireBaseData: FireBaseData) {


    fun addLocation(location: Location, user: FirebaseUser, status: Boolean) {
        val time: Date = Date(location.time)
        val geoPosition: GeoPoint = GeoPoint(location.latitude, location.longitude)
        fireBaseData.addLocation(user, geoPosition, time, status)
    }

    fun authUser(credential: AuthCredential) {
        fireBaseData.authUser(credential)

    }

    fun updateVideo(user: FirebaseUser, video: Video, startTS: Timestamp, endTS: Timestamp) {
        fireBaseData.updateVideo(user, video, startTS, endTS)

    }
}