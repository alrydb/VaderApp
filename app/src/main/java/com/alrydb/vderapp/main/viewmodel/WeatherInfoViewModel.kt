package com.alrydb.vderapp.main.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.location.Location
import android.location.LocationManager

//import android.location.LocationRequest

import android.os.Looper
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.alrydb.vderapp.main.data.models.weather.WeatherResponse
import com.alrydb.vderapp.main.data.models.forecast.DailyForecastResponse
import com.alrydb.vderapp.main.data.models.forecast.HourlyForecastResponse
import com.alrydb.vderapp.main.data.models.location.LocationResponse
import com.alrydb.vderapp.main.data.repo.DailyForecastRepository
import com.alrydb.vderapp.main.data.repo.HourlyForecastRepository
import com.alrydb.vderapp.main.data.repo.LocationRepository
import com.alrydb.vderapp.main.data.repo.WeatherRepository
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationRequest.create
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.delay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherInfoViewModel(private val weatherRepository: WeatherRepository, private val dailyForecastRepository: DailyForecastRepository, private val hourlyForecastRepository: HourlyForecastRepository, private val locationRepository: LocationRepository): AndroidViewModel(Application()) {


    private var mfusedLocationClient: FusedLocationProviderClient? = null
    private var mLocationRequest : LocationRequest? = null
   /* private var mLocationCallback : LocationCallback? = null*/


    // Sätter latitud och longitud till att vara Örebro som standard
    private var lat: Double = 59.2747
    private var lon: Double = 15.2151

    private lateinit var locationResponse : LocationResponse

    var finishRefresh: Boolean = false
    val currentWeatherList: MutableLiveData<WeatherResponse> = MutableLiveData()
    val dailyForecastList: MutableLiveData<DailyForecastResponse> = MutableLiveData()
    val hourlyForecastList: MutableLiveData<HourlyForecastResponse> = MutableLiveData()

    fun isLocationEnabled(context: Context): Boolean {


        // Få tillgång till användarens plats
        val locationManager: LocationManager
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)


    }


    // Process som hämtar mobilens plats var n:e millisekund (n avgörs av värdet på interval)
    @SuppressLint("MissingPermission")
    fun requestLocationData(context: Context) {


        mfusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        mLocationRequest = create().apply {
            interval = 60000
            fastestInterval = 30000

            priority = PRIORITY_HIGH_ACCURACY
        }


        mfusedLocationClient!!.requestLocationUpdates(
            mLocationRequest!!, mLocationCallback,
            Looper.getMainLooper()
        )

    }


    // Funktion som manuellt uppdaterar mobilens plats, anropas när användaren "refreshar" appen
    @SuppressLint("MissingPermission")
    fun refreshLocationData(context: Context) {

        mfusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        val cancellationTokenSource = CancellationTokenSource()
        mfusedLocationClient!!.getCurrentLocation(
            PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        ).addOnSuccessListener { task ->
            lat = task.latitude
            lon = task.longitude

        }


    }


    fun refreshSearchedLocation() {

            lat = locationResponse[0].lat
            lon = locationResponse[0].lon

            getLocationWeatherDetails()
            getLocationForecastDetails()
            getLocationHourlyForecastDetails()

            stopLocationUpdates()

    }


    private fun stopLocationUpdates() {
        mfusedLocationClient?.removeLocationUpdates(mLocationCallback)

        mfusedLocationClient = null
        mLocationRequest = null

    }







    private var mLocationCallback = object : LocationCallback() {

        override fun onLocationResult(locationresult: LocationResult) {

            val mLastLocation = locationresult.lastLocation
            lat = mLastLocation.latitude
            Log.i("Current Latitude", "$lat")
            Log.i("Location time", "${locationresult.lastLocation.time}")
            lon = mLastLocation.longitude
            Log.i("Current Longitude", "$lon")

            getLocationWeatherDetails()
            getLocationForecastDetails()
            getLocationHourlyForecastDetails()
            //getSearchedLocationDetails()

        }


    }


    fun refreshHourlyForecast()
    {
        getLocationHourlyForecastDetails()
    }

    fun refreshDailyForecast()
    {
        getLocationForecastDetails()
    }

    fun refreshCurrentWeather()
    {
        getLocationWeatherDetails()
    }


    private fun getLocationWeatherDetails() {

        val response = weatherRepository.getWeather(lat, lon)

        //Skickar vårt HTTP GET request asynkront
        response.enqueue(object : Callback<WeatherResponse> {

            //Anrop med svar
            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {

                if (response!!.isSuccessful) {
                    // All data från vårt gson objekt, dvs vår deserialiserade json data
                    val weatherList: WeatherResponse? = response.body()

                    // Tilldela värdet på weatherlist, dvs vår json data, till vår MutableLivedata 'currentWeatherlist' som vår view sedan observerar
                    currentWeatherList.postValue(weatherList)


                    Log.i("Response result", "$weatherList")

                } else {
                    val rc = response.code()
                    when (rc) {
                        400 -> {
                            Log.e("Error 400", "bad connection")
                        }
                        404 -> {
                            Log.e("Error 404", "not found")
                        }
                        else -> {
                            Log.e("Error", "Generic error")
                        }
                    }
                }
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.e("Error", t!!.message.toString())
                finishRefresh = true
            }

        })

    }


   private fun getLocationForecastDetails() {

        val response = dailyForecastRepository.getDailyForecast(lat, lon)
        response.enqueue(object : Callback<DailyForecastResponse> {
            override fun onResponse(
                call: Call<DailyForecastResponse>,
                response: Response<DailyForecastResponse>
            ) {

                val forecastList: DailyForecastResponse? = response.body()
                dailyForecastList.value = forecastList

                Log.i("Response result", "$forecastList")
                finishRefresh = true
            }

            override fun onFailure(call: Call<DailyForecastResponse>, t: Throwable) {
                Log.e("DailyForecast error", t!!.message.toString())
            }


        })

    }


    private fun getLocationHourlyForecastDetails() {

        val response = hourlyForecastRepository.getHourlyForecast(lat, lon)
        response.enqueue(object : Callback<HourlyForecastResponse> {
            override fun onResponse(
                call: Call<HourlyForecastResponse>,
                response: Response<HourlyForecastResponse>
            ) {

                val forecastList: HourlyForecastResponse? = response.body()
                hourlyForecastList.postValue(forecastList)

                Log.i("Response result HOURLY", "$forecastList")
                Log.i("response result refresh", finishRefresh.toString())
                finishRefresh = true
            }

            override fun onFailure(call: Call<HourlyForecastResponse>, t: Throwable) {
                Log.e("HourlyForecast error", t!!.message.toString())
            }

        })

        Log.i("response result refresh", finishRefresh.toString())
    }


     fun getSearchedLocationDetails(location : String?) {

        val response = locationRepository.getSearchedLocation(location ?: "London")
        response.enqueue(object : Callback<LocationResponse> {
            override fun onResponse(
                call: Call<LocationResponse>,
                response: Response<LocationResponse>
            ) {
                if (response.isSuccessful) {

                    val locationList: LocationResponse? = response.body()
                    if (locationList != null) {
                        if (locationList.size > 0) {
                            locationResponse = locationList

                            /* Log.i("search", "${locationList?.get(0)?.name}")*/
                            Log.i("response result refresh", finishRefresh.toString())

                            finishRefresh = true
                            refreshSearchedLocation()
                        } else {
                            Log.i("location error", "location not found")
                        }
                    }






                } else {
                    val rc = response.code()
                    when (rc) {
                        400 -> {
                            Log.e("Error 400", "bad connection")
                        }
                        404 -> {
                            Log.e("Error 404", "not found")
                        }
                        else -> {
                            Log.e("Error", "Generic error")
                        }
                    }
                }

            }

            override fun onFailure(call: Call<LocationResponse>, t: Throwable) {
                Log.e("locationresponse error", t!!.message.toString())
            }

        })

        Log.i("response result refresh", finishRefresh.toString())
    }

}