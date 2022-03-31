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
import com.google.android.gms.location.LocationRequest.create
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherInfoViewModel(private val weatherRepository: WeatherRepository): AndroidViewModel(Application()) {



    private lateinit var mfusedLocationClient: FusedLocationProviderClient
    // declare a global variable of FusedLocationProviderClient
   /* private lateinit var fusedLocationClient: FusedLocationProviderClient*/

    private  var lat : Double = 0.0
    private  var lon : Double = 0.0






    val myResponse : MutableLiveData<WeatherResponse> = MutableLiveData()
    //lateinit var longlat : ArrayList<Double>



  /*  @SuppressLint("MissingPermission")
    fun getLastKnownLocation(context: Context) {
        // in onCreate() initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    lat = location.latitude
                    lon = location.longitude
                    getLocationWeatherDetails()
                }

            }
    }
*/





        fun isLocationEnabled(context: Context): Boolean{


        // Få tillgång till användarens plats
        val locationManager: LocationManager
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)


    }


    @SuppressLint("MissingPermission")
    fun requestLocationData(context: Context){


        mfusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        val mLocationRequest = create().apply {
            interval = 10000
            fastestInterval = 5000

            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }


        mfusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.getMainLooper()
        )

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