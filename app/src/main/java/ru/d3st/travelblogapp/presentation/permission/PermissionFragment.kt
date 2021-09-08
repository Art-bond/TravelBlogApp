package ru.d3st.travelblogapp.presentation.permission

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.d3st.travelblogapp.databinding.FragmentPermissionBinding
import ru.d3st.travelblogapp.presentation.login.LoginFragment
import ru.d3st.travelblogapp.utils.PERMISSIONS_REQUIRED
import timber.log.Timber


class PermissionFragment : Fragment() {

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { granted: Map<String, Boolean> ->
            Timber.i("permission $granted")

            if (granted.containsValue(false))
                showAcceptAllPermissions()
            else navigateToBlogger()
        }


    private lateinit var binding: FragmentPermissionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        showAcceptAllPermissions()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)

        binding = FragmentPermissionBinding.inflate(layoutInflater)

        binding.grantPermissionButton.setOnClickListener {
            requestPermissionLauncher.launch(
                PERMISSIONS_REQUIRED.toTypedArray()
            )
        }

        return binding.root
    }


/*    private fun shouldShowRequestPermissionRationale(permissions: List<String>): Boolean {
        permissions.forEach { permission ->
            if (shouldShowRequestPermissionRationale(permission)) return true
        }
        return false
    }*/


    private fun showAcceptAllPermissions() {
        var allPermissionGranted = true
        PERMISSIONS_REQUIRED.forEach { permission ->
            val showRationale =
                shouldShowRequestPermissionRationale(permission)
            Timber.i("permission $permission is $showRationale")
            if (!showRationale) allPermissionGranted = false
        }
        if (!allPermissionGranted){
            goToSettings()
        }
        Toast.makeText(requireContext(), "You should accept all permissions", Toast.LENGTH_SHORT)
            .show()
    }

    private fun showNoAccess() {
        binding = FragmentPermissionBinding.inflate(layoutInflater)
        binding.root
    }

    private fun goToSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            .setData(Uri.fromParts("package", requireActivity().packageName, null))
        startActivity(intent)
    }

    private fun navigateToBlogger() {
        val action = PermissionFragmentDirections.actionPermissionFragmentToFragmentCameraMap()
        findNavController().navigate(action)
    }


}