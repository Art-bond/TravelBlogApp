package ru.d3st.travelblogapp.model

import com.google.android.gms.maps.model.LatLng
import ru.d3st.travelblogapp.model.domain.BloggerDomain
import ru.d3st.travelblogapp.model.domain.LocationDomain
import ru.d3st.travelblogapp.model.domain.VideoDomain
import ru.d3st.travelblogapp.model.firebase.BloggerFirebase
import ru.d3st.travelblogapp.model.firebase.FirebaseLocation
import ru.d3st.travelblogapp.model.firebase.FirebaseVideo

object DataTransferObjects {
    /**
     * Удобная трансформация списка [FirebaseVideo] в [VideoDomain]
     */
    fun List<FirebaseVideo>.asDomainModel(): List<VideoDomain> {
        return map {
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
    /**
     * Удобная трансформация списка [BloggerFirebase] в [BloggerDomain]
     */
    @JvmName("asDomainModelBloggerFirebase")
    fun List<BloggerFirebase>.asDomainModel(): List<BloggerDomain> {
        return map {
            BloggerDomain(
                uid = it.uid,
                email = it.email,
                name = it.name,
                photoUrl = it.photoUrl,
                videos = it.videos
            )
        }
    }

    /**
     * Удобная трансформация списка [FirebaseLocation] в [LocationDomain]
     */
    @JvmName("asDomainModelFirebaseLocation")
    fun List<FirebaseLocation>.asDomainModel(): List<LocationDomain> {
        return map { firebaseLocation ->
            val latLng = LatLng(
                firebaseLocation.geoPosition.latitude,
                firebaseLocation.geoPosition.longitude
            )
            LocationDomain(
                latLng = latLng,
                time = firebaseLocation.timestamp.toDate()
            )
        }
    }

}

