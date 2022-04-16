package com.alrydb.vderapp.main.data.models.forecast

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class HourlyForecastResponse (

    val lat : Double,
    val lon : Double,
    val timezone : String,
    @SerializedName("timezoneOffset")
    val timezone_offset : Int,
    val hourly : List<HourlyForecast>
) : Serializable