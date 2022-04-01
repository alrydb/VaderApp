package com.alrydb.vderapp.main.data.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Forecast(


    val list: List<WeatherResponse>,
    val city : City

) : Serializable

