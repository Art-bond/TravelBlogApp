package ru.d3st.travelblogapp.data.repository

import android.location.Location
import android.net.Uri
import com.google.api.services.youtube.model.Video
import com.google.common.truth.Truth
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.GeoPoint
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import org.junit.After
import org.junit.Before
import org.junit.Test
import ru.d3st.travelblogapp.model.firebase.BloggerFirebase
import ru.d3st.travelblogapp.model.firebase.FirebaseLocation
import ru.d3st.travelblogapp.model.firebase.FirebaseVideo
import java.util.*


class BloggersRepositoryTest {

    //Поддельные классы
    private val firebaseData = FakeFireBaseData()

    private val user: FirebaseUser = mockk(relaxed = true)

    private var fakeLocation = mockk<Location>(relaxed = true)

    private var fakeVideo = mockk<Video>(relaxed = true)


    //тестируемый класс
    private lateinit var bloggersRepository: BloggersRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        bloggersRepository = BloggersRepository(firebaseData)


    }

    @After
    fun tearDown() {

    }

    @Test
    fun addLocationInFireBase() {
        every { fakeLocation.latitude } returns 30.316600
        every { fakeLocation.longitude } returns 59.950023
        every { fakeLocation.time } returns 1631605484


        bloggersRepository.addLocation(fakeLocation, user, false)
        val actual = firebaseData.fakeLocation
        val expected = FirebaseLocation(
            GeoPoint(fakeLocation.latitude, fakeLocation.longitude),
            Timestamp(Date(fakeLocation.time))
        )

        Truth.assertThat(actual).isEqualTo(expected)
    }


    @Test
    fun createNewUserInFirebase() {
        every { user.displayName } returns "Ivan Blogger"
        every { user.uid } returns "01"
        every { user.email } returns "blogger@fake.ru"
        every { user.photoUrl } returns Uri.EMPTY


        bloggersRepository.authUser(user)
        val actual = firebaseData.fakeFireBaseUser
        val expected = BloggerFirebase(
            user.uid, user.email!!, user.displayName!!, user.photoUrl.toString(), 0, 0
        )


        Truth.assertThat(actual).isEqualTo(expected)

    }

    @Test
    fun updateVideo() {

        val startTS = Timestamp(Date(1631605684))
        val endTS = Timestamp(Date(1631605684) )

        every { fakeVideo.id } returns "007"
        every { fakeVideo.snippet.title } returns "video"
        every { fakeVideo.snippet.description } returns "descriptions"
        every { fakeVideo.snippet.channelId } returns "001"
        every { fakeVideo.snippet.channelTitle } returns "ChannelTitle"
        every { fakeVideo.snippet.publishedAt.value } returns 1631605484
        every { fakeVideo.snippet.thumbnails.high.url } returns "/thumbnails"



        bloggersRepository.updateVideo(user,fakeVideo, startTS, endTS)
        val actual = firebaseData.fakeVideo
        val expected = FirebaseVideo(
            fakeVideo.id,
            fakeVideo.snippet.title,
            fakeVideo.snippet.description,
            fakeVideo.snippet.channelId,
            fakeVideo.snippet.channelTitle,
            startTS,
            endTS,
            fakeVideo.snippet.publishedAt.value,
            fakeVideo.snippet.thumbnails.high.url

            )
        Truth.assertThat(actual).isEqualTo(expected)

    }
}