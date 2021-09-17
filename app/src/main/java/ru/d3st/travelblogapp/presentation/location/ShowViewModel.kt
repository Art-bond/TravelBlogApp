package ru.d3st.travelblogapp.presentation.location

import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import org.jetbrains.annotations.TestOnly
import ru.d3st.travelblogapp.domain.repository.SpectatorRepository
import ru.d3st.travelblogapp.model.domain.LocationDomain
import ru.d3st.travelblogapp.model.domain.VideoDomain
import ru.d3st.travelblogapp.utils.Resource
import ru.d3st.travelblogapp.utils.Status
import timber.log.Timber

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

    private val _networkLoading = MutableLiveData<Status>()
    val networkLoading: LiveData<Status>
        get() = _networkLoading

    private val _networkError = MutableLiveData<String>()
    val networkError: LiveData<String>
        get() = _networkError

    init {
        getTravel()
    }

    private fun getTravel() {
        viewModelScope.launch {
            val video = videoDomain
            _selectedVideo.value = video
            when(val locations = loadLocation(video)){
                is Resource.Error -> {
                    _networkError.value = locations.message ?: "Unknown error"
                    _networkLoading.value = Status.FAILURE
                    Timber.e("find locations error ${locations.message}")

                }
                is Resource.Loading -> {
                    _networkLoading.value = Status.LOADING
                }
                is Resource.Success -> {
                    _locations.value = locations.data?: emptyList()
                    _networkLoading.value = Status.SUCCESS
                    Timber.i("find ${locations.data?.size} locations")

                }
            }
        }
    }

    private suspend fun loadLocation(video: VideoDomain) =
        repository.loadLocations(
            bloggerId,
            video.start,
            video.end
        )


    /**
     * Метод позволяет найти нужный момент видео при клике на точку маршрута на карте
     * @param latLng координаты точки маршрута
     * @return true если точка найдена в видео
     */
    fun selectLocation(latLng: LatLng): Boolean {
        val location = locations.value
            .orEmpty()
            .firstOrNull { it.latLng == latLng }
            ?: return false
        val locationTime = location.time.time
        _seekPosition.value =
            (locationTime - (selectedVideo.value?.start?.time ?: locationTime)) / 1000f
        return true
    }

    @TestOnly
    fun setLocation(locations: List<LocationDomain>) {
        _locations.value = locations
    }

    @TestOnly
    fun setSeekPosition(seekPosition: Float) {
        _seekPosition.value = seekPosition
    }

    companion object {
        /**
         * Позволяем изпользовать [ShowViewModel] так как нам нужен ViewModel
         * c параметром
         * @param videoDomain [VideoDomain]
         * @param bloggerId id блогера
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
