package ru.d3st.travelblogapp.presentation.profile

import dagger.assisted.AssistedFactory

@AssistedFactory
interface ProfileViewModelFactory {
        fun create(userId : String): ProfileViewModel

}