package ru.d3st.travelblogapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.d3st.travelblogapp.data.firebase.FireBaseData


@InstallIn(SingletonComponent::class)
@Module
object FireBaseModule {

    fun provideFirebaseData(): FireBaseData{
        return FireBaseData()
    }
}

