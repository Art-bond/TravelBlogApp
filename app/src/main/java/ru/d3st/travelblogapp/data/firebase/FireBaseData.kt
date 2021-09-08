package ru.d3st.travelblogapp.data.firebase

import com.google.api.services.youtube.model.Video
import com.google.firebase.Timestamp
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ru.d3st.travelblogapp.model.firebase.BloggerFirebase
import ru.d3st.travelblogapp.model.firebase.FirebaseLocation
import ru.d3st.travelblogapp.model.firebase.FirebaseVideo
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FireBaseData @Inject constructor() {


    fun addLocation(user: FirebaseUser, geoPosition: GeoPoint, time: Date, status: Boolean) {

        val locationData = mapOf(
            "geoPosition" to geoPosition,
            "timestamp" to Timestamp(time),
            "record" to status,
        )

        Firebase.firestore
            .collection("bloggers").document(user.uid)
            .collection("locations").document()
            .set(locationData)

        Firebase.firestore
            .collection("bloggers").document(user.uid)
            .update("lastLocation", locationData)

        Firebase.firestore
            .collection("bloggers").document(user.uid)
            .update("locations", FieldValue.increment(1))
    }

    fun authUser(credential: AuthCredential) {
        Firebase.auth.signInWithCredential(credential).onSuccessTask { authResult ->
            val user = authResult.user!!
            val userData = mapOf(
                "uid" to user.uid,
                "name" to user.displayName,
                "email" to user.email,
                "photoUrl" to user.photoUrl?.toString(),
                "locations" to 0,
                "videos" to 0,
            )
            Firebase.firestore.collection("bloggers").document(user.uid).set(userData)
        }
    }

    fun updateVideo(user: FirebaseUser, video: Video, startTS: Timestamp, endTS: Timestamp) {

        val videoId= video.id

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

    suspend fun loadUsers(): List<BloggerFirebase> = suspendCoroutine { continuation ->
        Firebase.firestore.collection("bloggers").get().addOnCompleteListener { task ->
            task.result?.let {
                continuation.resume(it.toObjects(BloggerFirebase::class.java))
            }
            task.exception?.let { continuation.resumeWithException(it) }
        }
    }
    suspend fun loadVideos(userId: String): List<FirebaseVideo> = suspendCoroutine { continuation ->
        Firebase.firestore.collection("bloggers").document(userId)
            .collection("videos")
            .orderBy("start", Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener { task ->
                task.result?.let {
                    continuation.resume(it.toObjects(FirebaseVideo::class.java))
                }
                task.exception?.let { continuation.resumeWithException(it) }
            }
    }

    suspend fun loadLocations(userId: String, start: Date, end: Date): List<FirebaseLocation> =
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
                            it.toObjects(FirebaseLocation::class.java))
                    }
                    task.exception?.let { continuation.resumeWithException(it) }
                }
        }


}
