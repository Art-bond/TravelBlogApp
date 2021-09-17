package ru.d3st.travelblogapp.domain.repository

import com.google.common.truth.Truth
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ru.d3st.travelblogapp.MainCoroutineRule
import ru.d3st.travelblogapp.TestData
import ru.d3st.travelblogapp.data.firebase.FireBaseData
import ru.d3st.travelblogapp.model.domain.BloggerDomain
import ru.d3st.travelblogapp.model.domain.LocationDomain
import ru.d3st.travelblogapp.model.domain.VideoDomain
import ru.d3st.travelblogapp.model.firebase.BloggerFirebase
import ru.d3st.travelblogapp.model.firebase.FirebaseLocation
import ru.d3st.travelblogapp.model.firebase.FirebaseVideo
import ru.d3st.travelblogapp.utils.Resource

class SpectatorRepositoryTest {

    //Поддельные классы
    private val firebaseData: FireBaseData = mockk(relaxed = true)

    //тестируемый класс
    private lateinit var spectatorRepository: SpectatorRepository

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        spectatorRepository = SpectatorRepository(firebaseData)
    }

    @After
    fun tearDown() {
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getBloggerListSuccess() = mainCoroutineRule.runBlockingTest {

        val testFireBaseBloggers: List<BloggerFirebase> = TestData.firebaseBloggers
        val testBloggers: List<BloggerDomain> = TestData.bloggers

        coEvery { firebaseData.loadUsers() } returns testFireBaseBloggers

        val actual = spectatorRepository.getBloggerList()

        Truth.assertThat(actual).isEqualTo(testBloggers)
        coVerify { firebaseData.loadUsers() }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun getBloggerVideosSuccess() = mainCoroutineRule.runBlockingTest {

        val testFirebaseVideos: List<FirebaseVideo> = TestData.firebaseVideos
        val testVideos: List<VideoDomain> = TestData.videos

        val userId = TestData.bloggers.first().uid
        coEvery { firebaseData.loadVideos(userId) } returns testFirebaseVideos

        val actual = spectatorRepository.getBloggerVideos(userId)

        Truth.assertThat(actual).isEqualTo(testVideos)
        coVerify { firebaseData.loadVideos(userId) }

    }

    @ExperimentalCoroutinesApi
    @Test
    fun loadLocationsSuccess() = mainCoroutineRule.runBlockingTest {

        val testFirebaseLocations: List<FirebaseLocation> = TestData.firebaseLocations
        val testBloggerLocations: List<LocationDomain> = TestData.locations

        val userId = TestData.bloggers.first().uid
        val startDate = TestData.startDate
        val endDate = TestData.endDate

        coEvery {
            firebaseData.loadLocations(
                userId,
                startDate,
                endDate
            )
        } returns Resource.Success(testFirebaseLocations)

        val actual = spectatorRepository.loadLocations(userId, startDate, endDate).data

        Truth.assertThat(actual).isEqualTo(testBloggerLocations)
        coVerify { firebaseData.loadLocations(userId, startDate, endDate) }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun loadLocationsFailure() = mainCoroutineRule.runBlockingTest {


        val userId = TestData.bloggers.first().uid
        val startDate = TestData.startDate
        val endDate = TestData.endDate
        coEvery {
            firebaseData.loadLocations(
                userId,
                startDate,
                endDate
            )
        } returns Resource.Error("Exceptions load locations")
        val actual = spectatorRepository.loadLocations(userId, startDate, endDate)

        Truth.assertThat(actual.message).isEqualTo("Exceptions load locations")
        coVerify { firebaseData.loadLocations(userId, startDate, endDate) }
    }


}
