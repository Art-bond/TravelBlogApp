package ru.d3st.travelblogapp.presentation.login

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.d3st.travelblogapp.domain.repository.BloggersRepository
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val bloggersRepository: BloggersRepository) : ViewModel() {

    /**
     * Отправляем данные о пользователе в Firebase через [BloggersRepository]
     */
    fun createUser(credential: AuthCredential) {
        Firebase.auth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                val user: FirebaseUser = authResult.user!!
                 bloggersRepository.authUser(user)
            }
    }




}