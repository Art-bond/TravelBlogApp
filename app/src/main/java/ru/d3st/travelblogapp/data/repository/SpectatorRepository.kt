package ru.d3st.travelblogapp.data.repository

import com.google.android.gms.maps.model.LatLng
import ru.d3st.travelblogapp.data.firebase.FireBaseData
import ru.d3st.travelblogapp.model.asDomainModel
import ru.d3st.travelblogapp.model.domain.BloggerDomain
import ru.d3st.travelblogapp.model.domain.LocationDomain
import ru.d3st.travelblogapp.model.domain.VideoDomain
import ru.d3st.travelblogapp.model.firebase.FirebaseLocation
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpectatorRepository @Inject constructor(private val fireBaseData: FireBaseData){

    /**
     * Получаем список блогеров
     */
    suspend fun getBloggerList(): List<BloggerDomain>{
        return fireBaseData.loadUsers().asDomainModel()
    }
    /**
     * Получаем список видео данного блогера
     * @param bloggerId Id блоггера
     */
    suspend fun getBloggerVideos(bloggerId : String): List<VideoDomain>{
        return fireBaseData.loadVideos(bloggerId).asDomainModel()
    }
    /**
     * Получаем местоположения данного блоггера
     */
    suspend fun loadLocations(uid: String, start: Date, end: Date): List<LocationDomain> {
       return fireBaseData.loadLocations(uid,start,end).asDomainModel()
    }

}

private fun List<FirebaseLocation>.asDomainModel(): List<LocationDomain> {
    return map{ firebaseLocation ->
        val latLng = LatLng(firebaseLocation.geoposition.latitude, firebaseLocation.geoposition.longitude)
        LocationDomain(
            latLng = latLng,
            time = firebaseLocation.timestamp.toDate()
        )
    }
}




