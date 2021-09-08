package ru.d3st.travelblogapp.presentation.location

import dagger.assisted.AssistedFactory
import ru.d3st.travelblogapp.model.domain.VideoDomain

@AssistedFactory
interface ShowViewModelFactory {
    fun create(videoDomain: VideoDomain, bloggerId: String): ShowViewModel

}
