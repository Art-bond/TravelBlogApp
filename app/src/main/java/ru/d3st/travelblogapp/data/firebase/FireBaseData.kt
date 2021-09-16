package ru.d3st.travelblogapp.data.firebase

import com.google.api.services.youtube.model.Video
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ru.d3st.travelblogapp.model.firebase.BloggerFirebase
import ru.d3st.travelblogapp.model.firebase.FirebaseLocation
import ru.d3st.travelblogapp.model.firebase.FirebaseVideo
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FireBaseData @Inject constructor() : IFireBaseData {

    /**
     * добавляем локацию в Firebase
     * @param user пользователься в базу которого заносятся данные
     * @param geoPosition позиция которая вносится в бд
     * @param time время внесения данной геопозиции
     * @param status статус записи видео (записывает или нет)
     */
    override fun addLocation(user: FirebaseUser, geoPosition: GeoPoint, time: Date, status: Boolean) {

        val locationData = mapOf(
            "geoPosition" to geoPosition,
            "timestamp" to Timestamp(time),
            "record" to status,
        )

        Firebase.firestore
            .collection("bloggers").document(user.uid)
            .collection("locations").document(time.toString())
            .set(locationData)

        Firebase.firestore
            .collection("bloggers").document(user.uid)
            .update("lastLocation", locationData)

        Firebase.firestore
            .collection("bloggers").document(user.uid)
            .update("locations", FieldValue.increment(1))
    }

    /**
     * добавляем блоггера в Firebase
     * @param firebaseUser авторизационные данные пользователя
     */
    override fun createUser(firebaseUser: FirebaseUser){
            val user: FirebaseUser = firebaseUser
            val userData = mapOf(
                "uid" to user.uid,
                "name" to user.displayName,
                "email" to user.email,
                "photoUrl" to user.photoUrl?.toString(),
                "locations" to 0,
                "videos" to 0,
            )
            //добавляем данные в базу
            Firebase.firestore.collection("bloggers").document(user.uid).set(userData)
        }

    /**
     * добавляем информацию о видео в Firebase
     * @param user пользователься в базу которого заносятся данные
     * @param video содержит подробную информацию о загруженном на Youtube видео
     * @param startTS время начала записи видео
     * @param endTS время окончания записи видео
     */
    override fun updateVideo(user: FirebaseUser, video: Video, startTS: Timestamp, endTS: Timestamp) {

        val videoId = video.id

        val videoData = mapOf(
            "id" to videoId,
            "start" to startTS,
            "end" to endTS,
            "title" to video.snippet.title,
            "description" to video.snippet.description,
            "channelId" to video.snippet.channelId,
            "channelTitle" to video.snippet.channelTitle,
            "publishedAt" to video.snippet.publishedAt.value,
            "thumbnail" to video.snippet.thumbnails.high.url,
        )
        Firebase.firestore
            .collection("bloggers").document(user.uid)
            .collection("videos").document(videoId)
            .set(videoData)

        Firebase.firestore
            .collection("bloggers").document(user.uid)
            .update("lastVideo", videoData)

        Firebase.firestore
            .collection("bloggers").document(user.uid)
            .update("videos", FieldValue.increment(1))
    }

    /**
     * Запрашиваем всех пользователей из Firebase
     * @return Список Пользователей в формате Firebase [BloggerFirebase]
     */
    override suspend fun loadUsers(): List<BloggerFirebase> = suspendCoroutine { continuation ->
        Firebase.firestore.collection("bloggers").get().addOnCompleteListener { task ->
            task.result?.let {
                continuation.resume(it.toObjects(BloggerFirebase::class.java))
            }
            task.exception?.let {
                continuation.resumeWithException(it)
                Timber.e("Firebase load users request is failed $it")
            }
        }
    }

    /**
     * Запрашиваем информацию о всех видео выбранного пользователя из Firebase
     * @param userId выбранный пользователь
     * @return Список Видео в формате Firebase [FirebaseVideo]
     */
    override suspend fun loadVideos(userId: String): List<FirebaseVideo> = suspendCoroutine { continuation ->
        Firebase.firestore.collection("bloggers").document(userId)
            .collection("videos")
            .orderBy("start", Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener { task ->
                task.result?.let {
                    continuation.resume(it.toObjects(FirebaseVideo::class.java))
                }
                task.exception?.let {
                    continuation.resumeWithException(it)
                    Timber.e("Firebase load videos request is failed $it")
                }
            }
    }

    /**
     * Запрашиваем информацию о местоположениии выбранного пользователя в заданом интервале времени
     * @param userId выбранный пользователь
     * @param start начальная точка времени
     * @param end конечная точка времени
     * @return Список Локаций в формате Firebase [FirebaseLocation]
     */
    override suspend fun loadLocations(userId: String, start: Date, end: Date): List<FirebaseLocation> =
        suspendCoroutine { continuation ->
            Firebase.firestore.collection("bloggers").document(userId)
                .collection("locations")
                .whereGreaterThanOrEqualTo("timestamp", Timestamp(start))
                .whereLessThanOrEqualTo("timestamp", Timestamp(end))
                .orderBy("timestamp")
                .get()
                .addOnCompleteListener { task ->
                    task.result?.let {
                        continuation.resume(
                            it.toObjects(FirebaseLocation::class.java)
                        )
                    }
                    task.exception?.let {
                        continuation.resumeWithException(it)
                        Timber.e("Firebase load locations request is failed $it")
                    }
                }
        }


}
