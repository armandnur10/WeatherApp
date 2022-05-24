package com.example.weatherapp.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.weatherapp.BuildConfig
import com.example.weatherapp.data.ForecastResponse
import com.example.weatherapp.data.WeatherResponse
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.utils.HelperFunction.formattedDegree
import com.example.weatherapp.utils.LOCATION_PERMISSION_REQ_CODE
import com.example.weatherapp.utils.iconSizeWeather2x
import com.example.weatherapp.utils.iconSizeWeather4x
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlin.math.log

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding as ActivityMainBinding

    private var _viewModel: MainViewModel? = null
    private val viewModel get() = _viewModel as MainViewModel

    private var _weatherAdapter: WeatherAdapter? = null
    private val weatherAdapter get() = _weatherAdapter as WeatherAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetControler = ViewCompat.getWindowInsetsController(window.decorView)
        windowInsetControler?.isAppearanceLightNavigationBars = true

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        _viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        searchCity()

        viewModel.weatherByCity().observe(this) {
            setupView(it, null)
        }

        viewModel.getForecastByCity().observe(this) {
            setupView(null, it)
        }

        _weatherAdapter = WeatherAdapter()

        getWeatherCurrentLocation()

    }

    private fun getWeatherCurrentLocation() {
        val fusedLocationCilent: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                LOCATION_PERMISSION_REQ_CODE
            )
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationCilent.lastLocation
            .addOnSuccessListener {
                try {
                    val lat = it.latitude
                    val lon = it.longitude
                    viewModel.weatherByCurrentLocation(lat, lon)
                    viewModel.forecastByCurrentLocation(lat, lon)
                } catch (e: Throwable) {
                    Log.i("MainActivity", "last location : it")
                    Log.e("MainActivity", "Couldn't get latilude and longitude")
                }

            }
            .addOnFailureListener {
                Log.e("MainActivity", "FusedLocationError: Failed getting current location")
            }
        viewModel.getWeatherByCurrentLocation().observe(this) {
            setupView(it, null)
        }
        viewModel.getForecastByCurrentLocation().observe(this) {
            setupView(null, it)
        }
    }

    private fun setupView(weather: WeatherResponse?, forecast: ForecastResponse?) {
        weather?.let {
            binding.apply {
                tvCity.text = weather.name
                tvDegree.text = formattedDegree(weather.main?.temp)

                val icon = it.weather?.get(0)?.icon
                val iconUrl = BuildConfig.ICON_URL + icon + iconSizeWeather2x
                Glide.with(applicationContext).load(iconUrl).into(imgIcWeather)

                rvForecastWeather.apply {
                    layoutManager = LinearLayoutManager(
                        applicationContext,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                    adapter = weatherAdapter
                }
            }
        }
    }

    private fun searchCity() {
        binding.edtSearch.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let {
                        viewModel.searchByCity((it))
                        viewModel.forecastByCity(it)
                    }
                    try {
                        val inputMethodManager =
                            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
                    } catch (e: Throwable) {
                        Log.e("MainActivity", "hideSoftWindow: $e")
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }
            }
        )
    }
}