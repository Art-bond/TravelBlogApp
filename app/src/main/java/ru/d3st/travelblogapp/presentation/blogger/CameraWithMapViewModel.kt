package ru.d3st.travelblogapp.presentation.blogger

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.api.client.http.InputStreamContent
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.Video
import com.google.api.services.youtube.model.VideoSnippet
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.d3st.travelblogapp.data.repository.BloggersRepository
import timber.log.Timber
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import javax.inject.Inject

@HiltViewModel
class CameraWithMapViewModel @Inject constructor(
    private val bloggersRepository: BloggersRepository
) : ViewModel() {


    private val _currentUser = MutableLiveData<FirebaseUser>()
    val currentUser: LiveData<FirebaseUser> get() = _currentUser

    private val _statusRecording = MutableLiveData(false)
    val statusRecording: LiveData<Boolean> get() = _statusRecording

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    init {
        getCurrentUser()
    }

    /**
     * Получаем залогиненного пользователя
     */
    private fun getCurrentUser() {
        viewModelScope.launch {
            _currentUser.value = Firebase.auth.currentUser
        }
    }

    /**
     * Обновление состояние записи на ЗАПИСЬ
     */
    fun startRecord() {
        _statusRecording.value = true
    }

    /**
     * Обновление состояние записи на ОСТАНОВЛЕНО
     */
    fun stopRecord() {
        _statusRecording.value = false
    }

    /**
     * Добавляем данные о новом местоположении в репозиторий
     * @param location данные о местоположении
     */
    fun addLocation(location: Location) {
        if (_currentUser.value != null) {
            bloggersRepository.addLocation(location, _currentUser.value!!, statusRecording.value ?: false)
        } else {
            Timber.e("User is null, we cannot find location")
            _errorMessage.value = "User is not authorised"

        }
    }

    private fun updateVideo(video: Video, startTS: Timestamp, endTS: Timestamp) {
        val videoId = video.id

        if (_currentUser.value != null) {
            bloggersRepository.updateVideo(_currentUser.value!!, video, startTS, endTS)
        } else {
            Timber.e("User is null, we cannot upload info about video: $videoId")
            _errorMessage.value = "User is not authorised"

        }
    }

    /**
     * Метод настраивает загрузку видео в сервис Youtube
     * @param youTube экземпляр класса подключенное библиотеки Youtube
     * @param file видеофаил сохраненный на устройстве
     * @param startTS время начала видео
     * @param endTS время окончания видео
     */
    fun uploadVideo(youTube: YouTube, file: File, startTS: Timestamp, endTS: Timestamp) {
        viewModelScope.launch(Dispatchers.IO) {
            uploadYoutubeVideo(
                youTube,
                InputStreamContent(
                    "application/octet-stream",
                    BufferedInputStream(FileInputStream(file))
                ).setLength(file.length()), startTS, endTS
            )
        }
    }

    private fun uploadYoutubeVideo(
        youTube: YouTube,
        mediaContent: InputStreamContent,
        startTS: Timestamp,
        endTS: Timestamp
    ) = try {

        val date = startTS.toDate()

        val video = Video()
        val snippet = VideoSnippet()
        snippet.description = "TravelBlog video $date"
        snippet.title = "TravelBlog video test day 3"
        video.snippet = snippet

        val response: Video =
            youTube.videos().insert(listOf("id,snippet,statistics"), video, mediaContent).execute()
        Timber.i("Video uploaded on youtube: $response")
        updateVideo(response, startTS, endTS)
    } catch (e: Exception) {
        Timber.e(e, "Failed to upload youtube video")
        _errorMessage.postValue(e.message)
    }
}


