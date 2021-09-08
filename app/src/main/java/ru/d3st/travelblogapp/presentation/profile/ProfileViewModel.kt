package ru.d3st.travelblogapp.presentation.profile

import androidx.lifecycle.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import ru.d3st.travelblogapp.data.repository.SpectatorRepository
import ru.d3st.travelblogapp.model.domain.BloggerDomain
import ru.d3st.travelblogapp.model.domain.VideoDomain

class ProfileViewModel @AssistedInject constructor(
    private val repository: SpectatorRepository,
    @Assisted private val userId: String
) : ViewModel() {

    private val _selectedUser = MutableLiveData<BloggerDomain>()
    val selectedBloggerDomain: LiveData<BloggerDomain> get() = _selectedUser

    private val _allVideo = MutableLiveData<List<VideoDomain>>()
    val allVideo: LiveData<List<VideoDomain>> get() = _allVideo

    init {
        getUserInfo()
        getUserVideos(userId)
    }

    private fun getUserInfo() {
        viewModelScope.launch {
            _selectedUser.value = repository.getBloggerList().first {
                it.uid == userId
            }
        }
    }

    private fun getUserVideos(userId: String) {
        viewModelScope.launch {
           _allVideo.value = repository.getBloggerVideos(userId)

        }
    }
    companion object {
        /**
         * Позволяем изпользовать [ProfileViewModelFactory] так как нам нужен ViewModel
         * c параметром
         * @param  bloggerId Для [BloggerDomain]
         */
        fun provideFactory(
            assistedFactory: ProfileViewModelFactory,
            bloggerId: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return assistedFactory.create(bloggerId) as T
            }
        }
    }

}