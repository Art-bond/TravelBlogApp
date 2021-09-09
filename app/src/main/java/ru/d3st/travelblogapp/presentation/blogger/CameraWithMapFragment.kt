package ru.d3st.travelblogapp.presentation.blogger

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.view.*
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.core.VideoCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.youtube.YouTube
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.d3st.travelblogapp.R
import ru.d3st.travelblogapp.databinding.FragmentCameraMapBinding
import ru.d3st.travelblogapp.utils.currentLatLng
import ru.d3st.travelblogapp.utils.currentLocation
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


//tag для логов
private val LOG_TAG = CameraWithMapFragment::class.java.simpleName
private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"


@AndroidEntryPoint
class CameraWithMapFragment : Fragment() {


    //определение координат пользователя
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    //для возможности передачать данные о местоположении
    private lateinit var locationCallback: LocationCallback


    // подключаем возможность видеозаписи
    private lateinit var videoCapture: VideoCapture
    private lateinit var cameraExecutor: ExecutorService


    private lateinit var outputDirectory: File

    private lateinit var binding: FragmentCameraMapBinding

    private lateinit var googleAccountCredential: GoogleAccountCredential
    private lateinit var youTube: YouTube

    private val viewModel: CameraWithMapViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentUser = Firebase.auth.currentUser
        //проверям залогинен ли пользователь
        if (currentUser == null) {
            navigateToStartScreen()
        }else{
            showSnackBar("You are logged in as ${currentUser.displayName}")
        }

        initYoutubeSettings(currentUser)

        outputDirectory = getOutputDirectory()

        cameraExecutor = Executors.newSingleThreadExecutor()

        setHasOptionsMenu(true)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.log_out)?.setOnMenuItemClickListener {
            Firebase.auth.signOut()
            navigateToStartScreen()
            return@setOnMenuItemClickListener true
        }
        super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.log_out, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCameraMapBinding.inflate(inflater, container, false)

        initCamera()

        // Настраиваем захват видер
        videoCapture = VideoCapture.Builder().apply {
        }.build()


        //обработчик нажатия
        binding.cameraCaptureButton.setOnClickListener {
            if (viewModel.statusRecording.value == false) {
                binding.cameraCaptureButton.setBackgroundColor(Color.RED)
                startRecording()
                startLocationUpdates()
                viewModel.startRecord()
            } else if (viewModel.statusRecording.value == true) {
                binding.cameraCaptureButton.setBackgroundColor(Color.GRAY)
                stopLocationUpdates()
                stopRecording()
                viewModel.stopRecord()
            }
        }
        //привязываем состояние записи
/*        viewModel.statusRecording.observe(viewLifecycleOwner,{
            if (it){
                startRecording()
                startLocationUpdates()
            } else{
                stopLocationUpdates()
                stopRecording()
            }
        })*/

        viewModel.errorMessage.observe(viewLifecycleOwner,{
            showSnackBar(it)
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_blogger) as SupportMapFragment?

        mapFragment?.getMapAsync(callback)
    }


    override fun onDestroy() {
        super.onDestroy()
/*        stopLocationUpdates()
        stopRecording()*/
        cameraExecutor.shutdown()
    }

    /**
     * Подключаем YouTube Сервис
     */
    private fun initYoutubeSettings(currentUser: FirebaseUser?) {
        googleAccountCredential = GoogleAccountCredential.usingOAuth2(
            requireContext(),
            listOf("https://www.googleapis.com/auth/youtube")
        ).setBackOff(ExponentialBackOff()).setSelectedAccountName(currentUser?.email)

        val httpTransport = NetHttpTransport()
        val jsonFactory = JacksonFactory.getDefaultInstance()

        //Вносим настройки в Utube Сервис
        youTube = YouTube.Builder(
            httpTransport,
            jsonFactory,
            googleAccountCredential
        ).setApplicationName("TravelBlog").build()
    }

    /**
     * Подключаем камеру
     */
    private fun initCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            videoCapture = VideoCapture.Builder().build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, videoCapture
                )

            } catch (exc: Exception) {
                Timber.e(exc, "Use case binding failed")
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    @SuppressLint("RestrictedApi", "MissingPermission")
    private fun startRecording() {

        // Create time-stamped output file to hold the image
        val fileName =
            SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".mp4"
        val videoFile = File(outputDirectory, fileName)

        val startTS = Timestamp.now()

        videoCapture.startRecording(
            VideoCapture.OutputFileOptions.Builder(videoFile).build(),
            cameraExecutor,
            object : VideoCapture.OnVideoSavedCallback {
                override fun onVideoSaved(outputFileResults: VideoCapture.OutputFileResults) {
                    Timber.tag(LOG_TAG).d("Video saved succeeded: %s", Uri.fromFile(videoFile))
                    val endTS = Timestamp.now()
                    viewModel.uploadVideo(youTube, videoFile, startTS, endTS)
                }

                override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {
                    Timber.e(message)
                }
            })

    }

    @SuppressLint("RestrictedApi")
    private fun stopRecording() {
        videoCapture.stopRecording()
    }

    private fun getOutputDirectory(): File {
        val mediaDir = requireContext().externalCacheDirs.firstOrNull()?.also { it.mkdirs() }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else requireContext().filesDir
    }


    /**
     * Переходим на начальных экран
     */
    private fun navigateToStartScreen() {
        val action = CameraWithMapFragmentDirections.actionFragmentCameraMapToFragmentLogin()
        findNavController().navigate(action)
    }


    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->

        viewLifecycleOwner.lifecycleScope.launch {
            //Начальные параметры
            val homeLatLng: LatLng = fusedLocationClient.currentLatLng()

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLatLng, 15f))
        }

        //применить стиль из папки Raw
        setMapStyle(googleMap)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    updateLocation(location, googleMap)
                }
            }
        }
        //нажатие на кнопку найти местоположения пользователя
        //в случае если потеряли себя на карте
        binding.fabLocationBlogger.setOnClickListener { getMyLocation(googleMap) }
    }


    @SuppressLint("MissingPermission")
    private fun getMyLocation(googleMap: GoogleMap) {
        viewLifecycleOwner.lifecycleScope.launch {
            val latLng = fusedLocationClient.currentLatLng()
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        }
    }

    /**
     * Отображаем изменение положения пользователя на карте
     * @param location положение в коордианатах
     * @param googleMap экземпляр карт куда будут вносится изменения
     */
    fun updateLocation(location: Location, googleMap: GoogleMap) {
        Timber.i("New location in ${location.time} is $location")
        //Конвертируем из и location в latLng
        val latLng = LatLng(location.latitude, location.longitude)
        //движение камеры
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        //установка маркера
        googleMap.addMarker(
            MarkerOptions()
            .position(latLng))

        //сохраняем в базу данных
        viewModel.addLocation(location)
    }

    /**
     * Запуск слежения за местоположением
     */
    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create()
        locationRequest.interval = 30000L //каждые 30 сек

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    /**
     * Отмена слежения
     */
    @SuppressLint("MissingPermission")
    private fun stopLocationUpdates() {
        //заносим последнее местоположение в базу
        viewLifecycleOwner.lifecycleScope.launch {
            val location = fusedLocationClient.currentLocation()
            viewModel.addLocation(location)
        }
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }




    /**
     * Настраиваем стиль карты
     */
    private fun setMapStyle(map: GoogleMap) {
        try {
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(),
                    R.raw.map_style
                )
            )
            if (!success) {
                Timber.tag(LOG_TAG).i("Style apply failed")
            }
        } catch (e: Resources.NotFoundException) {
            Timber.tag(LOG_TAG).e(e, "Can't find style. Error: ")
            showSnackBar(e.message)
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