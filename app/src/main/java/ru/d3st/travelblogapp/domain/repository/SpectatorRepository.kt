package ru.d3st.travelblogapp.domain.repository

import ru.d3st.travelblogapp.data.firebase.IFireBaseData
import ru.d3st.travelblogapp.model.DataTransferObjects.asDomainModel
import ru.d3st.travelblogapp.model.domain.BloggerDomain
import ru.d3st.travelblogapp.model.domain.LocationDomain
import ru.d3st.travelblogapp.model.domain.VideoDomain
import ru.d3st.travelblogapp.utils.Resource
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpectatorRepository @Inject constructor(private val fireBaseData: IFireBaseData) {

    /**
     * Получаем список блогеров
     */
    suspend fun getBloggerList(): List<BloggerDomain> {
        return fireBaseData.loadUsers().asDomainModel()
    }

    /**
     * Получаем список видео данного блогера
     * @param bloggerId Id блогера
     */
    suspend fun getBloggerVideos(bloggerId: String): List<VideoDomain> {
        return fireBaseData.loadVideos(bloggerId).asDomainModel()
    }

    /**
     * Получаем местоположение данного блогера в определенный момент времени
     * @param uid выбранный блоггер
     * @param start время начала отрезка времени в котором нам нужны координаты
     * @param end время окончания
     */
    suspend fun loadLocations(uid: String, start: Date, end: Date): Resource<List<LocationDomain>> {
        return when (val listLocations = fireBaseData.loadLocations(uid, start, end)) {
            is Resource.Error -> Resource.Error(listLocations.message?:"Unknown error")
            is Resource.Loading -> Resource.Loading()
            is Resource.Success -> Resource.Success(
                listLocations.data?.asDomainModel() ?: emptyList()
            )
        }

    }

}








