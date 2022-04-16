package com.alrydb.vderapp.main.data.models.forecast

import com.alrydb.vderapp.main.data.models.weather.Weather
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class HourlyForecast(


    val dt: Int,
    val temp: Double,
    val feelsLike: Double,
    val pressure: Int,
    val humidity: Int,
    @SerializedName("dewPoint")
    val dew_point : Double,
    val uvi : Double,
    val clouds: Int,
    val visibility : Int,
    @SerializedName("windSpeed")
    val wind_speed : Double,
    @SerializedName("windDeg")
    val wind_deg : Int,
    @SerializedName("windGust")
    val wind_gust : Double,
    val weather: List<Weather>,
    val pop : Double,


) : Serializable
