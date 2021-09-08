package ru.d3st.travelblogapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            // подключаем Timber к логам
            Timber.plant(Timber.DebugTree())
        }
    }
}