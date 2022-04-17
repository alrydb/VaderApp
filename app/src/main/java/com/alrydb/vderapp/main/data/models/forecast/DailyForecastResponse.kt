package com.alrydb.vderapp.main.data.models.forecast

import com.google.gson.annotations.SerializedName

data class DailyForecastResponse(

    val lat : Double,
    val lon : Double,
    val timezone : String,
    @SerializedName("timezone_offset")
    val timezoneOffset : Int,
    val daily : List<DailyForecast>

)
