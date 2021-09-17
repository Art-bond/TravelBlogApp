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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import org.jetbrains.annotations.TestOnly
import ru.d3st.travelblogapp.di.IoDispatcher
import ru.d3st.travelblogapp.domain.repository.BloggersRepository
import ru.d3st.travelblogapp.utils.Status
import timber.log.Timber
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import javax.inject.Inject

@HiltViewModel
class CameraWithMapViewModel @Inject constructor(
    private val bloggersRepository: BloggersRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher

) : ViewModel() {

    private val _statusRecording = MutableLiveData(false)
    val statusRecording: LiveData<Boolean> get() = _statusRecording

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _statusLoading = MutableLiveData<Status>()
    val statusLoading: LiveData<Status>
        get() = _statusLoading


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
    fun addLocation(location: Location, user: FirebaseUser?) {
        if (user != null) {
            bloggersRepository.addLocation(location, user, statusRecording.value ?: false)
        } else {
            Timber.e("User is null, we cannot find location")
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
    fun uploadVideo(
        youTube: YouTube,
        file: File,
        startTS: Timestamp,
        endTS: Timestamp,
        user: FirebaseUser
    ) {
        _statusLoading.postValue(Status.LOADING)
        viewModelScope.launch(ioDispatcher) {

            val stream = fileToStream(file)

            uploadYoutubeVideo(
                youTube,
                stream,
                startTS,
                endTS,
                user
            )
        }
    }

    private fun fileToStream(file: File): InputStreamContent =
        InputStreamContent(
            "application/octet-stream",
            BufferedInputStream(FileInputStream(file))
        ).setLength(file.length())




    private fun uploadYoutubeVideo(
        youTube: YouTube,
        mediaContent: InputStreamContent,
        startTS: Timestamp,
        endTS: Timestamp,
        user: FirebaseUser
    ) = try {

        val video = Video()
        val snippet = VideoSnippet()
        snippet.channelTitle = "Travel Blog"
        snippet.description = "TravelBlog video ${startTS.toDate()}"
        snippet.title = "TravelBlog video test"
        video.snippet = snippet

        val response: Video =
            youTube.videos().insert(listOf("id,snippet,statistics"), video, mediaContent).execute()
        Timber.i("Video uploaded on youtube: $response")
        updateVideo(response, startTS, endTS, user)
        _statusLoading.postValue(Status.SUCCESS)
    } catch (e: Exception) {
        Timber.e(e, "Failed to upload youtube video")
        _statusLoading.postValue(Status.FAILURE)
        _errorMessage.postValue(e.message)
    }

    private fun updateVideo(
        video: Video,
        startTS: Timestamp,
        endTS: Timestamp,
        user: FirebaseUser
    ) {
        bloggersRepository.updateVideo(user, video, startTS, endTS)

    }

    @TestOnly
    fun testUploadYoutubeVideo(
        youTube: YouTube,
        mediaContent: InputStreamContent,
        startTS: Timestamp,
        endTS: Timestamp,
        user: FirebaseUser
    ) {
        uploadYoutubeVideo(
            youTube,
            mediaContent,
            startTS,
            endTS,
            user
        )

    }
}




