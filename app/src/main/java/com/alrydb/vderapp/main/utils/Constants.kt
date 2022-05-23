package com.alrydb.vderapp.main.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

// Denna klass innehåller ett antal konstanter som används som parametrar i varje API-anrop

object Constants{

    // API-NYCKEL
    const val APP_ID : String = "a62fb47f91aa25fcf51d05cf37e560f7"
    // API URL
    const val BASE_URL : String = "https://api.openweathermap.org/"
    // Språk för beskrivning av väder och namn på städer
    const val LANG: String = "se"

    const val METRIC_UNIT : String = "metric"

}