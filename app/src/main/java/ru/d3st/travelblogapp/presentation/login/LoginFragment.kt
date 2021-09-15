package ru.d3st.travelblogapp.presentation.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import ru.d3st.travelblogapp.R
import ru.d3st.travelblogapp.databinding.FragmentLoginBinding
import ru.d3st.travelblogapp.utils.PERMISSIONS_REQUIRED
import timber.log.Timber

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private var _binding: FragmentLoginBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        initGoogleSignIn()

        auth = Firebase.auth

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBlogger.setOnClickListener {
            signInLauncher.launch(googleSignInClient.signInIntent)
        }
        binding.btnUser.setOnClickListener {
            goToSpectator()
        }

        val currentUser = auth.currentUser
        updateUI(currentUser)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initGoogleSignIn() {
        // Configure Google Sign In
        val gso: GoogleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .requestScopes(Scope("https://www.googleapis.com/auth/youtube"))
                .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Timber.d("signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                    viewModel.createUser(credential)
                    //Проверка есть ли пользователь в базе
                    val creationTimestamp = user?.metadata?.creationTimestamp
                    val lastSignInTimestamp = user?.metadata?.lastSignInTimestamp
                    if (creationTimestamp == lastSignInTimestamp) {
                        //отправляем данные в Firebase
                        viewModel.createUser(credential)
                    }
                    checkPermissionAndGoToBlogger()
                    //goToBlogger()
                } else {
                    // If sign in fails, display a message to the user.
                    showAuthFailed(task.exception)
                    Timber.tag(TAG).e(task.exception, "signInWithCredential:failure")
                    updateUI(null)
                }
            }
    }

    private fun checkPermissionAndGoToBlogger() {
        requestPermissionLauncher.launch(PERMISSIONS_REQUIRED.toTypedArray())
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user == null) {
            showSnackBar("You are not logged in")
        } else showSnackBar("Logged in as ${user.displayName}")

    }

    private fun showAuthFailed(exception: Exception?) {
        showSnackBar(exception?.message ?: "Failed to auth")
    }


    private fun goToSpectator() {
        val action = LoginFragmentDirections.actionFragmentLoginToOverviewFragment()
        findNavController().navigate(action)
    }


    private fun goToBlogger() {
        val action = LoginFragmentDirections.actionFragmentLoginToFragmentCameraMap()
        findNavController().navigate(action)
    }

    companion object {
        private const val TAG = "AuthFragment"
        private const val RC_SIGN_IN = 9001
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
        { granted: Map<String, Boolean> ->
            Timber.i("permission $granted")

            if (granted.containsValue(false)) showAcceptAllPermissions()
            else goToBlogger()
        }

    private fun showAcceptAllPermissions() {
        var allPermissionGranted = true
        PERMISSIONS_REQUIRED.forEach { permission ->
            val showRationale =
                shouldShowRequestPermissionRationale(permission)
            if (!showRationale) allPermissionGranted = false
        }
        if (!allPermissionGranted) {
            goToSettings()
        }
        showSnackBar("You should accept all permissions")
    }

    private fun goToSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            .setData(Uri.fromParts("package", requireActivity().packageName, null))
        startActivity(intent)
    }

    /**
     * Возвращает итог авторизации гугл аккаунта
     */
    private val signInLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);

        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            // Google Sign In was successful, authenticate with Firebase
            val account = task.getResult(ApiException::class.java)!!
            Timber.tag(TAG).d("firebaseAuthWithGoogle:%s", account.id)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            // Google Sign In failed, update UI appropriately
            showAuthFailed(e)
            Timber.e(e, "Google sign in failed")
        }
    }

    private fun showSnackBar(message: String?) {
        if (message != null) {
            Snackbar.make(
                requireActivity().findViewById(android.R.id.content),
                message,
                Snackbar.LENGTH_LONG
            ).show()
        }
    }


}