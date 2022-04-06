package com.alrydb.vderapp.main.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.location.LocationManager

//import android.location.LocationRequest

import android.os.Looper
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.alrydb.vderapp.main.data.models.weather.WeatherResponse
import com.alrydb.vderapp.main.data.models.forecast.DailyForecastResponse
import com.alrydb.vderapp.main.data.repo.DailyForecastRepository
import com.alrydb.vderapp.main.data.repo.WeatherRepository
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationRequest.create
import com.google.android.gms.tasks.CancellationTokenSource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherInfoViewModel(private val weatherRepository: WeatherRepository, private val dailyForecastRepository: DailyForecastRepository): AndroidViewModel(Application()) {



    private lateinit var mfusedLocationClient: FusedLocationProviderClient

    private  var lat : Double = 0.0 // latitud
    private  var lon : Double = 0.0 // longitud

    val currentWeatherList : MutableLiveData<WeatherResponse> = MutableLiveData()
    val dailyForecastList : MutableLiveData<DailyForecastResponse> = MutableLiveData()

        fun isLocationEnabled(context: Context): Boolean{


        // Få tillgång till användarens plats
        val locationManager: LocationManager
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)


    }


    // Process som körs i bakgrunden som hämtar mobilens plats var n:e millisekund (n avgörs av värdet på interval)
    @SuppressLint("MissingPermission")
    fun requestLocationData(context: Context){


        mfusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        val mLocationRequest = create().apply {
            interval = 60000
            fastestInterval = 30000

            priority = PRIORITY_HIGH_ACCURACY
        }


        mfusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.getMainLooper()
        )



    }


    // Funktion som manuellt uppdaterar mobilens plats, anropas när användaren "refreshar" appen
    @SuppressLint("MissingPermission")
    fun refreshLocationData(context: Context){

        mfusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        val cancellationTokenSource = CancellationTokenSource()
        mfusedLocationClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token).addOnSuccessListener{ task ->
            lat = task.latitude
            lon = task.longitude

            getLocationWeatherDetails()
            getLocationForecastDetails()
        }


    }



    private val mLocationCallback = object : LocationCallback() {

        override fun onLocationResult(locationresult: LocationResult) {

            val mLastLocation = locationresult.lastLocation
            lat = mLastLocation.latitude
            Log.i("Current Latitude", "$lat")
            Log.i("Location time", "${locationresult.lastLocation.time}")
            lon = mLastLocation.longitude
            Log.i("Current Longitude", "$lon")

            getLocationWeatherDetails()
            getLocationForecastDetails()

        }



    }



fun getLocationWeatherDetails(){

    val response = weatherRepository.getWeather(lat, lon)

    response.enqueue(object : Callback<WeatherResponse>{

        //Lyckat api-anrop
        override fun onResponse(
            call: Call<WeatherResponse>,
            response: Response<WeatherResponse>
        ) {
            if(response!!.isSuccessful)
            {
                // All data från vårt gson objekt, dvs vår deserialiserade json data
                val weatherList : WeatherResponse? = response.body()

                // Tilldela värdet på weatherlist, dvs vår json data, till vår MutableLivedata 'currentWeatherlist' som vår view sedan får tillgång till
                currentWeatherList.value = weatherList
                Log.i("Response result", "$weatherList")

            }
            else
            {
                val rc = response.code()
                when(rc){
                    400 ->{
                        Log.e("Error 400", "bad connection")
                    }
                    404 ->{
                        Log.e("Error 404", "not found")
                    }
                    else ->{
                        Log.e("Error", "Generic error")
                    }
                }
            }
        }

        override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
            Log.e("Error", t!!.message.toString())
        }

    })

}



    fun getLocationForecastDetails(){

        val response = dailyForecastRepository.getDailyForecast(lat, lon)
        response.enqueue(object : Callback <DailyForecastResponse>{
            override fun onResponse(call: Call<DailyForecastResponse>, response: Response<DailyForecastResponse>) {

                val forecastList : DailyForecastResponse? = response.body()
                dailyForecastList.value = forecastList

                Log.i("Response result", "$forecastList")
            }

            override fun onFailure(call: Call<DailyForecastResponse>, t: Throwable) {
                Log.e("DailyForecast error", t!!.message.toString())
            }


        })

    }



}