package ru.d3st.travelblogapp.blogger

import android.annotation.SuppressLint
import android.content.res.Resources
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import ru.d3st.travelblogapp.R
import ru.d3st.travelblogapp.databinding.FragmentCameraMapFragmentBinding
import ru.d3st.travelblogapp.databinding.FragmentLoginBinding
import ru.d3st.travelblogapp.utils.currentLocation
import kotlin.concurrent.fixedRateTimer

class FragmentCameraMap : Fragment() {

    //tag для логов
    private val TAG_LOG = FragmentCameraMap::class.java.simpleName

    private val viewModel: FragmentCameraMapViewModel by viewModels()

    private var _binding: FragmentCameraMapFragmentBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    //определение координат пользователя
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraMapFragmentBinding.inflate(inflater, container, false)
        return binding.root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_blogger) as SupportMapFragment?

        mapFragment?.getMapAsync(mapCallBack)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("MissingPermission")
    private val mapCallBack = OnMapReadyCallback { googleMap ->

        viewLifecycleOwner.lifecycleScope.launch {
            //Начальные параметры
            val homeLatLng = fusedLocationClient.currentLocation()
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLatLng, 15f))
        }
        //установить пин
        //googleMap.addMarker(MarkerOptions().position(homeLatLng))

        //применить стиль из папки Raw
        setMapStyle(googleMap)

/*        cancelTimer()
        timer = fixedRateTimer(period = 60_000) { getMyLocation(googleMap, false) }*/

        binding.fabLocationBlogger.setOnClickListener { getMyLocation(googleMap, true) }
    }

    @SuppressLint("MissingPermission")
    private fun getMyLocation(googleMap: GoogleMap, fromUser: Boolean) {
        viewLifecycleOwner.lifecycleScope.launch {
            val latLng = fusedLocationClient.currentLocation()
            updateMapLocation(latLng, googleMap, fromUser)
            val user = Firebase.auth.currentUser
            if (fromUser.not() && user != null) {
                val locationData = mapOf(
                    "latitude" to latLng.latitude,
                    "longitude" to latLng.longitude,
                    "timestamp" to Timestamp.now(),
                    "record" to viewModel.statusRecording.value,
                )

                Firebase.firestore
                    .collection("users").document(user.uid)
                    .collection("locations").document()
                    .set(locationData)

                Firebase.firestore
                    .collection("users").document(user.uid)
                    .update("lastLocation", locationData)

                Firebase.firestore
                    .collection("users").document(user.uid)
                    .update("locations", FieldValue.increment(1))
            }
        }
    }

    private fun updateMapLocation(latLng: LatLng?, googleMap: GoogleMap, fromUser: Boolean) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        if (latLng != null && fromUser.not()) googleMap.addMarker(MarkerOptions().position(latLng))

/*        googleMap.moveCamera(CameraUpdateFactory.zoomTo(15.0f))
        location?.let {
        }*/
    }


    private fun setMapStyle(map: GoogleMap) {
        try {
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    R.raw.map_style
                )
            )
            if (!success) {
                //Timber.tag(TAG).i("Style apply failed")
            }
        } catch (e: Resources.NotFoundException) {
            //Timber.tag(TAG).e(e, "Can't find style. Error: ")
        }
    }

}