package ru.d3st.travelblogapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.d3st.travelblogapp.data.firebase.FireBaseData
import ru.d3st.travelblogapp.data.firebase.IFireBaseData
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object FireBaseModule {

    @Singleton
    @Provides
    fun provideFirebaseData(): IFireBaseData{
        return FireBaseData()
    }
}

