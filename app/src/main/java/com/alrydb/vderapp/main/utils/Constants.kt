package com.alrydb.vderapp.main.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

// Denna klass innehåller ett antal konstanter som används som parametrar i varje API-anrop

object Constants{

    // API-NYCKEL
    const val APP_ID : String = "572c5e9a56daaad8dd3502bf88c4b5c7"
    // API URL
    const val BASE_URL : String = "https://api.openweathermap.org/"
    // Språk för beskrivning av väder och namn på städer
    const val LANG: String = "se"
    // Måttenhet
    const val METRIC_UNIT : String = "metric"

}