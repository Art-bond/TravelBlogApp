package ru.d3st.travelblogapp.utils

import android.Manifest

//список всех запрашиваемых разрешений
val PERMISSIONS_REQUIRED = listOf(
    Manifest.permission.CAMERA, //камера
    Manifest.permission.RECORD_AUDIO, //запись звука
    Manifest.permission.ACCESS_FINE_LOCATION) // точное местоположение