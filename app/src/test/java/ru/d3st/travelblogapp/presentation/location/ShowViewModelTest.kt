package ru.d3st.travelblogapp.presentation.location

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ru.d3st.travelblogapp.MainCoroutineRule
import ru.d3st.travelblogapp.TestData
import ru.d3st.travelblogapp.data.repository.SpectatorRepository
import ru.d3st.travelblogapp.model.domain.LocationDomain
import ru.d3st.travelblogapp.model.domain.VideoDomain

class ShowViewModelTest {

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
    private lateinit var viewModel: ShowViewModel

    //поддельный класс
    private val repository: SpectatorRepository = mockk(relaxed = true)

    private val videoDomain: VideoDomain = TestData.videos.first()
    private val bloggerId = "007"
    private val locations = TestData.locations

    //Переменные для тестирования LiveData
    private val testVideoDomain: Observer<VideoDomain> = spyk()
    private val testLocations: Observer<List<LocationDomain>> = spyk()
    private val testSeekPosition: Observer<Float> = spyk()

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        //инициализируем моки
        MockKAnnotations.init(this, relaxed = true)

        viewModel = ShowViewModel(repository, videoDomain, bloggerId)




        //подписываемся на LiveData
        viewModel.selectedVideo.observeForever(testVideoDomain)
        viewModel.locations.observeForever(testLocations)
        viewModel.seekPosition.observeForever(testSeekPosition)

        every { testVideoDomain.onChanged(any()) } just Runs
        every { testLocations.onChanged(any()) } just Runs
        every { testSeekPosition.onChanged(any()) } just Runs
    }
    @After
    fun tearDown() {
        //после тестов отписываемся от LiveData
        viewModel.selectedVideo.removeObserver(testVideoDomain)
        viewModel.locations.removeObserver(testLocations)
        viewModel.seekPosition.removeObserver(testSeekPosition)
    }

    @Test
    fun getSelectedVideo() {
        verifySequence {
            testVideoDomain.onChanged(videoDomain)
        }
    }


    @ExperimentalCoroutinesApi
    @Test
    fun selectLocation() {
        //given
        val latLng = TestData.latLng[1]
        viewModel.setLocation(locations)
        testVideoDomain.onChanged(videoDomain)

        //then
        viewModel.selectLocation(latLng)

        verifySequence {
            testLocations.onChanged(emptyList())
            testLocations.onChanged(locations)
            testSeekPosition.onChanged(0.03f)
        }
    }
}