package ru.d3st.travelblogapp.presentation.blogger

import android.location.Location
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.google.api.client.http.InputStreamContent
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.Video
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ru.d3st.travelblogapp.MainCoroutineRule
import ru.d3st.travelblogapp.TestData
import ru.d3st.travelblogapp.domain.repository.BloggersRepository
import ru.d3st.travelblogapp.utils.Status
import java.io.File


class CameraWithMapViewModelTest {

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    //Добавляем правила для тестирования с возможностью ассинхронных запросов
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    private val testDispatcher = TestCoroutineDispatcher()


    //тестируемый класс
    private lateinit var viewModel: CameraWithMapViewModel

    //поддельный класс
    private val repository: BloggersRepository = mockk(relaxed = true)

    private var user: FirebaseUser? = mockk(relaxed = true)

    private val testLocation: Location = mockk(relaxed = true)

    private val youTube: YouTube = mockk(relaxed = true)
    private val startTS = Timestamp(TestData.startDate)
    private val endTS = Timestamp(TestData.endDate)
    private val file: File = mockk(relaxed = true)
    private val mediaContent: InputStreamContent = mockk(relaxed = true)


    //Переменные для тестирования LiveData
    private val testStatusRecord: Observer<Boolean> = spyk()
    private val testStatusLoading: Observer<Status> = spyk()
    private val testErrorObserver: Observer<String> = spyk()


    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        //инициализируем моки
        MockKAnnotations.init(this, relaxed = true)

        viewModel = CameraWithMapViewModel(repository, testDispatcher)


        //подписываемся на LiveData
        viewModel.statusLoading.observeForever(testStatusLoading)
        viewModel.statusRecording.observeForever(testStatusRecord)
        viewModel.errorMessage.observeForever(testErrorObserver)

        every { testStatusRecord.onChanged(any()) } just Runs
        every { testStatusLoading.onChanged(any()) } just Runs
        every { testErrorObserver.onChanged(any()) } just Runs
    }

    @After
    fun tearDown() {
        //после тестов отписываемся от LiveData
        viewModel.statusLoading.removeObserver(testStatusLoading)
        viewModel.statusRecording.removeObserver(testStatusRecord)
        viewModel.errorMessage.removeObserver(testErrorObserver)
    }


    @Test
    fun startRecord() {
        //Given
        //Then
        viewModel.startRecord()
        verifySequence {
            testStatusRecord.onChanged(false)
            testStatusRecord.onChanged(true)
        }
        verify { testErrorObserver wasNot Called }
    }

    @Test
    fun stopRecord() {
        //Given
        testStatusRecord.onChanged(true)
        //Then
        viewModel.stopRecord()

        verifySequence {
            testStatusRecord.onChanged(false)
            testStatusRecord.onChanged(true)
            testStatusRecord.onChanged(false)
        }
        verify { testErrorObserver wasNot Called }
    }


    @Test
    fun addLocation_user_not_logged_in() {
        //Given
        user = null
        //Then
        viewModel.addLocation(testLocation, user)
        verify { testErrorObserver.onChanged("User is not authorised") }
    }

    @Test
    fun addLocation_success() {
        //Given
        //Then
        viewModel.addLocation(testLocation, user)
        verify { testErrorObserver wasNot Called }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun uploadVideo_success() = runBlockingTest {
        //Given
        every {
            youTube.videos().insert(listOf("id,snippet,statistics"), any(), mediaContent).execute()
        } returns Video()
        //Then
        testStatusLoading.onChanged(Status.LOADING)
        viewModel.testUploadYoutubeVideo(youTube, mediaContent, startTS, endTS, user!!)


        verifySequence {
            testStatusLoading.onChanged(Status.LOADING)
            testStatusLoading.onChanged(Status.SUCCESS)

        }
        verify { testErrorObserver wasNot Called }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun uploadVideo_failed() = runBlockingTest {
        //Given
        every {
            youTube.videos().insert(listOf("id,snippet,statistics"), any(), mediaContent).execute()
        } throws Exception("failure loading video")
        //Then
        testStatusLoading.onChanged(Status.LOADING)
        viewModel.testUploadYoutubeVideo(youTube, mediaContent, startTS, endTS, user!!)


        verifySequence {
            testStatusLoading.onChanged(Status.LOADING)
            testStatusLoading.onChanged(Status.FAILURE)
            testErrorObserver.onChanged("failure loading video")
        }
    }


}