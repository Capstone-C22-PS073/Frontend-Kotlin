package com.capstonec22ps073.toursight.view.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.capstonec22ps073.toursight.R
import com.capstonec22ps073.toursight.data.AuthDataPreferences
import com.capstonec22ps073.toursight.databinding.ActivityMainBinding
import com.capstonec22ps073.toursight.repository.AuthRepository
import com.capstonec22ps073.toursight.repository.CulturalObjectRepository
import com.capstonec22ps073.toursight.view.camera.CameraActivity
import com.capstonec22ps073.toursight.view.login.LoginActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var viewModel: MainViewModel
    var token = ""

    private var location: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setupNavigationController()
        getLastLocation()

        val pref = AuthDataPreferences.getInstance(dataStore)
        viewModel = ViewModelProvider(this, MainViewModelFactory(AuthRepository(pref), CulturalObjectRepository())).get(
            MainViewModel::class.java
        )

        viewModel.getUserToken().observe(this) { token ->
            if (token == "") {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                this.token = token
            }
        }

//        viewModel.location.observe(this) { location ->
//            if (location != null) {
//
//            }
//        }

        binding.bottomNavigationView.background = null

        binding.fabCamera.setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }
    }

    private fun setupNavigationController() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getLastLocation()
                }
                else -> {}
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getLastLocation() {
        if (
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    this.location = location
                    val city = getGeneralCity(getAddress(location.latitude, location.longitude))
                    viewModel.getCulturalObjectBasedOnUserLocation(this.token, city)
                    Log.d(TAG, "getLastLocation: ${location.latitude}, ${location.longitude}")
                    Log.d(TAG, "getAddress: ${getAddress(location.latitude, location.longitude)}")
                    Log.d(TAG, "getGeneralCity: ${getGeneralCity(getAddress(location.latitude, location.longitude))}")
                } else {
                    viewModel.getALlCulturalObjects(this.token)
                    Toast.makeText(
                        this,
                        "Location is not found. Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun getAddress(lat: Double, lng: Double): String {
        val geocoder = Geocoder(this)
        val list = geocoder.getFromLocation(lat, lng, 1)
        return list[0].subAdminArea
    }

    private fun getGeneralCity(city: String): String {
        val citySplit = city.split(" ")

        return if (citySplit.size == 3) {
            citySplit[1]
        } else {
            if (citySplit[0].lowercase() == "kota" || citySplit[0].lowercase() == "kabupaten") {
                citySplit[1]
            } else {
                citySplit[0]
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}