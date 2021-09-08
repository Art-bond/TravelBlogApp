package ru.d3st.travelblogapp.presentation.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.d3st.travelblogapp.model.domain.BloggerDomain
import kotlinx.coroutines.launch
import ru.d3st.travelblogapp.data.repository.SpectatorRepository
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(private val repository: SpectatorRepository): ViewModel() {

    private val _allUsers = MutableLiveData<List<BloggerDomain>>()
    val allUsers: LiveData<List<BloggerDomain>> get() = _allUsers

    init {
        getUsersInfo()

    }

    private fun getUsersInfo(){
        viewModelScope.launch {

            _allUsers.value = repository.getBloggerList()

        }
    }
}
