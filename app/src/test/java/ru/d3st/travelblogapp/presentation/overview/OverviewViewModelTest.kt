package ru.d3st.travelblogapp.presentation.overview

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
import ru.d3st.travelblogapp.domain.repository.SpectatorRepository
import ru.d3st.travelblogapp.model.domain.BloggerDomain

class OverviewViewModelTest {

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
    private lateinit var viewModel: OverviewViewModel

    //поддельный класс
    private val repository: SpectatorRepository = mockk(relaxed = true)

    private val testUsers: Observer<List<BloggerDomain>> = spyk()
    private val bloggers = TestData.bloggers


    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        //инициализируем моки
        MockKAnnotations.init(this, relaxed = true)

        viewModel = OverviewViewModel(repository)

        //подписываемся на LiveData
        viewModel.allUsers.observeForever(testUsers)


        every { testUsers.onChanged(any()) } just Runs

    }
    @After
    fun tearDown() {
        //после тестов отписываемся от LiveData
        viewModel.allUsers.removeObserver(testUsers)

    }
    @ExperimentalCoroutinesApi
    @Test
    fun getAllUsers()= mainCoroutineRule.runBlockingTest {
        //given
        coEvery { repository.getBloggerList() } returns bloggers
        //then
        viewModel.getUsers()

        verifySequence {
            testUsers.onChanged(emptyList())
            testUsers.onChanged(bloggers)
        }
    }
}