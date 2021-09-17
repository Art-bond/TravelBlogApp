package ru.d3st.travelblogapp.data.firebase

import com.google.api.services.youtube.model.Video
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.GeoPoint
import ru.d3st.travelblogapp.model.firebase.BloggerFirebase
import ru.d3st.travelblogapp.model.firebase.FirebaseLocation
import ru.d3st.travelblogapp.model.firebase.FirebaseVideo
import ru.d3st.travelblogapp.utils.Resource
import java.util.*

interface IFireBaseData {
    /**
     * добавляем локацию в Firebase
     * @param user пользователься в базу которого заносятся данные
     * @param geoPosition позиция которая вносится в бд
     * @param time время внесения данной геопозиции
     * @param status статус записи видео (записывает или нет)
     */
    fun addLocation(user: FirebaseUser, geoPosition: GeoPoint, time: Date, status: Boolean)

    /**
     * добавляем блоггера в Firebase
     * @param firebaseUser авторизационные данные пользователя в Firebase
     */
    fun createUser(firebaseUser: FirebaseUser)

    /**
     * добавляем информацию о видео в Firebase
     * @param user пользователься в базу которого заносятся данные
     * @param video содержит подробную информацию о загруженном на Youtube видео
     * @param startTS время начала записи видео
     * @param endTS время окончания записи видео
     */
    fun updateVideo(user: FirebaseUser, video: Video, startTS: Timestamp, endTS: Timestamp)

    /**
     * Запрашиваем всех пользователей из Firebase
     * @return Список Пользователей в формате Firebase [BloggerFirebase]
     */
    suspend fun loadUsers(): List<BloggerFirebase>

    /**
     * Запрашиваем информацию о всех видео выбранного пользователя из Firebase
     * @param userId выбранный пользователь
     * @return Список Видео в формате Firebase [FirebaseVideo]
     */
    suspend fun loadVideos(userId: String): List<FirebaseVideo>

    /**
     * Запрашиваем информацию о местоположениии выбранного пользователя в заданом интервале времени
     * @param userId выбранный пользователь
     * @param start начальная точка времени
     * @param end конечная точка времени
     * @return Список Локаций в формате Firebase [FirebaseLocation]
     */
    suspend fun loadLocations(userId: String, start: Date, end: Date): Resource<List<FirebaseLocation>>

}