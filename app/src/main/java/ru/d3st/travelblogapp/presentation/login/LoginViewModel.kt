package ru.d3st.travelblogapp.presentation.login

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.AuthCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.d3st.travelblogapp.data.repository.BloggersRepository
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val bloggersRepository: BloggersRepository) : ViewModel() {

    fun authUser(credential: AuthCredential) {
        bloggersRepository.authUser(credential)
    }


}