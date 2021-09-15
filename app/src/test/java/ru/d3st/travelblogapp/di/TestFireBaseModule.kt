package ru.d3st.travelblogapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import ru.d3st.travelblogapp.data.firebase.IFireBaseData
import ru.d3st.travelblogapp.data.repository.FakeFireBaseData
import javax.inject.Singleton


@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [FireBaseModule::class]
)
object TestFireBaseModule {

    @Singleton
    @Provides
    fun provideFirebaseData(): IFireBaseData{
        return FakeFireBaseData()
    }
}

