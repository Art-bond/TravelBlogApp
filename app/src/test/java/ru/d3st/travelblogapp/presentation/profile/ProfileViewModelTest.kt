package ru.d3st.travelblogapp.presentation.profile

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
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
import ru.d3st.travelblogapp.data.repository.SpectatorRepository
import ru.d3st.travelblogapp.model.domain.BloggerDomain
import ru.d3st.travelblogapp.model.domain.VideoDomain

class ProfileViewModelTest {

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
    private lateinit var viewModel: ProfileViewModel

    //поддельный класс
    private val repository: SpectatorRepository = mockk(relaxed = true)

    private val testUser: Observer<BloggerDomain> = spyk()
    private val testVideos: Observer<List<VideoDomain>> = spyk()

    private val bloggerId = "007"
    private val videos = TestData.videos
    private val bloggers = TestData.bloggers


    @Before
    fun setUp() {
        //инициализируем моки
        MockKAnnotations.init(this, relaxed = true)

        viewModel = ProfileViewModel(repository,bloggerId)

        viewModel.allVideo.observeForever(testVideos)
        viewModel.selectedUser.observeForever(testUser)

        every { testUser.onChanged(any()) } just Runs
        every { testVideos.onChanged(any()) } just Runs
    }

    @After
    fun tearDown() {
        viewModel.selectedUser.removeObserver(testUser)
        viewModel.allVideo.removeObserver(testVideos)

    }

    @ExperimentalCoroutinesApi
    @Test
    fun blogger_info_success() = mainCoroutineRule.runBlockingTest {
        //given
        coEvery { repository.getBloggerList() } returns bloggers

        // Then
        viewModel.testBloggerInfo()

        //Состояние LiveData должно менятся по данной схеме
        verifySequence {
            testUser.onChanged(null)
            testUser.onChanged(bloggers.first())
        }

    }

    @ExperimentalCoroutinesApi
    @Test
    fun getAllVideo_success() = mainCoroutineRule.runBlockingTest {
        //given
        coEvery { repository.getBloggerVideos(any()) } returns videos

        // Then
        viewModel.testVideos()

        //Состояние LiveData должно менятся по данной схеме
        coVerifySequence {
            testVideos.onChanged(emptyList())
            testVideos.onChanged(videos)
        }

    }
}