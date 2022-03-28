package com.alrydb.vderapp.main.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

// Singleton

object Constants{

    // WeatherMap API-nyckel
    const val APP_ID : String = "572c5e9a56daaad8dd3502bf88c4b5c7"
    const val BASE_URL : String = "https://api.openweathermap.org/data/"

    const val METRIC_UNIT : String = "metric"


    fun isNetWorkAvailable(context: Context) : Boolean{
        val connectivityManager : ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            // Returnera false om vi inte kan koppla upp oss till internet
            val network = connectivityManager.activeNetwork ?: return false

            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            // 'when' fungerar som en switch i andra språk
            return when {
                //Returnera true om vi får internetuppkoppling genom, wifi, mobilnät eller ethernet
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }
        else{
            // Gamla sättet att koppla appen till internet

            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnectedOrConnecting


        }


    }



}