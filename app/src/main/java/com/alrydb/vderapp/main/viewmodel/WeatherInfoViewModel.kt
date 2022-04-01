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
import androidx.lifecycle.viewModelScope
import com.alrydb.vderapp.main.data.models.WeatherResponse
import com.alrydb.vderapp.main.data.repo.WeatherRepository
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationRequest.create
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherInfoViewModel(private val weatherRepository: WeatherRepository): AndroidViewModel(Application()) {



    private lateinit var mfusedLocationClient: FusedLocationProviderClient

    private  var lat : Double = 0.0 // latitud
    private  var lon : Double = 0.0 // longitud

    val myResponse : MutableLiveData<WeatherResponse> = MutableLiveData()

        fun isLocationEnabled(context: Context): Boolean{


        // Få tillgång till användarens plats
        val locationManager: LocationManager
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)


    }


    // Process som körs i bakgrunden som hämtar mobilens plats var n:e sekund (n avgörs av värdet på interval)
    @SuppressLint("MissingPermission")
    fun requestLocationData(context: Context){


        mfusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        val mLocationRequest = create().apply {
            interval = 5000
            fastestInterval = 10000

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
        val currentLocation =  mfusedLocationClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token).addOnSuccessListener{ task ->
            lat = task.latitude
            lon = task.longitude
            getLocationWeatherDetails()
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

        }



    }



fun getLocationWeatherDetails(){

    val response = weatherRepository.getWeather(lat, lon)
    response.enqueue(object : Callback<WeatherResponse>{
        override fun onResponse(
            call: Call<WeatherResponse>,
            response: Response<WeatherResponse>
        ) {
            if(response!!.isSuccessful)
            {
                val weatherList : WeatherResponse? = response.body() // All data
                // myResponse.postValue(weatherList)

                //myResponse.postValue(weatherList)
                myResponse.value = weatherList
                //myResponse.postValue(weatherList)
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



}