package ru.d3st.travelblogapp.presentation.location

import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import ru.d3st.travelblogapp.data.repository.SpectatorRepository
import ru.d3st.travelblogapp.model.domain.LocationDomain
import ru.d3st.travelblogapp.model.domain.VideoDomain

class ShowViewModel @AssistedInject constructor(
    private val repository: SpectatorRepository,
    @Assisted private val videoDomain: VideoDomain,
    @Assisted private val bloggerId: String
) : ViewModel() {

    private val _selectedVideo = MutableLiveData<VideoDomain>()
    val selectedVideo: LiveData<VideoDomain> get() = _selectedVideo

    private val _locations = MutableLiveData<List<LocationDomain>>()
    val locations: LiveData<List<LocationDomain>> get() = _locations

    private val _seekPosition = MutableLiveData<Float>()
    val seekPosition: LiveData<Float> get() = _seekPosition

    init {
        viewModelScope.launch {
            val video = videoDomain
            _selectedVideo.value = video
            val locations = runCatching { repository.loadLocations(bloggerId, video.start, video.end) }.getOrNull().orEmpty()
            _locations.value = locations
        }
    }

    fun selectLocation(latLng: LatLng): Boolean {
        val location = locations.value.orEmpty().firstOrNull { it.latLng == latLng } ?: return false
        val locationTime = location.time.time
        _seekPosition.value = (locationTime - (selectedVideo.value?.start?.time ?: locationTime)) / 1000f
        return true
    }
    companion object {
        /**
         * Позволяем изпользовать [ShowViewModel] так как нам нужен ViewModel
         * c параметром
         * @param videoDomain [VideoDomain]
         * @param bloggerId id [BloggerDomain]
         */
        fun provideFactory(
            assistedFactory: ShowViewModelFactory,
            videoDomain: VideoDomain,
            bloggerId: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return assistedFactory.create(videoDomain, bloggerId) as T
            }
        }
    }
}
