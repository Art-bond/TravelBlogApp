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

    /**
     * добавляем локацию в Firebase
     * @param user пользователься в базу которого заносятся данные
     * @param location все данные о местоположении (время, координаты и тд.)
     * @param status статус записи видео (записывает или нет)
     */
    fun addLocation(location: Location, user: FirebaseUser, status: Boolean) {
        val time = Date(location.time)
        val geoPosition = GeoPoint(location.latitude, location.longitude)
        fireBaseData.addLocation(user, geoPosition, time, status)
    }

    /**
     * добавляем блоггера в Firebase
     * @param credential авторизационные данные для входа в Firebase
     */
    fun authUser(credential: AuthCredential) {
        fireBaseData.authUser(credential)

    }

    /**
     * добавляем информацию о видео в Firebase
     * @param user пользователься в базу которого заносятся данные
     * @param video содержит подробную информацию о загруженном на Youtube видео
     * @param startTS время начала записи видео
     * @param endTS время окончания записи видео
     */
    fun updateVideo(user: FirebaseUser, video: Video, startTS: Timestamp, endTS: Timestamp) {
        fireBaseData.updateVideo(user, video, startTS, endTS)
    }
}