package ru.d3st.travelblogapp

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import ru.d3st.travelblogapp.model.domain.BloggerDomain
import ru.d3st.travelblogapp.model.domain.LocationDomain
import ru.d3st.travelblogapp.model.domain.VideoDomain
import ru.d3st.travelblogapp.model.firebase.BloggerFirebase
import ru.d3st.travelblogapp.model.firebase.FirebaseLocation
import ru.d3st.travelblogapp.model.firebase.FirebaseVideo
import java.util.*

object TestData {


    val startDate: Date = Date(1631643203)
    private val middleDate = Date(1631643233)
    val endDate: Date = Date(1631643253)

    private val firebaseLocation1 =
        FirebaseLocation(GeoPoint(30.316600, 59.950023), Timestamp(startDate))
    private val firebaseLocation2 =
        FirebaseLocation(GeoPoint(30.316601, 59.950024), Timestamp(middleDate))
    private val firebaseLocation3 =
        FirebaseLocation(GeoPoint(30.316602, 59.950025), Timestamp(endDate))
    val firebaseLocations: List<FirebaseLocation> =
        listOf(firebaseLocation1, firebaseLocation2, firebaseLocation3)

    private val firebaseVideo = FirebaseVideo(
        "007",
        "video",
        "descriptions",
        "001",
        "ChannelTitle",
        Timestamp(startDate),
        Timestamp(endDate),
        1631643203,
        "/thumbnails"
    )
    private val video = VideoDomain(
        "007",
        "video",
        "descriptions",
        "001",
        "ChannelTitle",
        startDate,
        endDate,
        "/thumbnails"
    )

    val videos: List<VideoDomain> = listOf(video)
    val firebaseVideos: List<FirebaseVideo> = listOf(firebaseVideo)
    private val location1 = LocationDomain(LatLng(30.316600, 59.950023), startDate)
    private val location2 = LocationDomain(LatLng(30.316601, 59.950024), middleDate)
    private val location3 = LocationDomain(LatLng(30.316602, 59.950025), endDate)

    val locations: List<LocationDomain> = listOf(location1, location2, location3)

    private val latLng1 = LatLng(30.316600, 59.950023)
    private val latLng2 = LatLng(30.316601, 59.950024)
    private val latLng3 = LatLng(30.316602, 59.950025)

    val latLng: List<LatLng> = listOf(latLng1, latLng2, latLng3)

    private val bloggerDomain1 =
        BloggerDomain("007", "blogger@yandex.ru", "Иван Блогеров", "/photo1", 5)
    private val bloggerDomain2 =
        BloggerDomain("069", "blogger@gmail.ru", "Валентина Путешественикова", "/photo8", 3)

    val bloggers: List<BloggerDomain> = listOf(bloggerDomain1, bloggerDomain2)

    private val firebaseBlogger1 =
        BloggerFirebase("007", "blogger@yandex.ru", "Иван Блогеров", "/photo1", 5, 0)
    private val firebaseBlogger2 =
        BloggerFirebase("069", "blogger@gmail.ru", "Валентина Путешественикова", "/photo8", 3, 0)

    val firebaseBloggers: List<BloggerFirebase> = listOf(firebaseBlogger1, firebaseBlogger2)
}