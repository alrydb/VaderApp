package com.alrydb.vderapp.main.viewmodel

import android.app.Application
import android.content.Context
import android.location.LocationManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel

class WeatherInfoViewModel: AndroidViewModel(Application()) {



    fun isLocationEnabled(context: Context): Boolean{

        // Få tillgång till användarens plats
        val locationManager: LocationManager
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)



    }

}