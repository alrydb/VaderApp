package com.alrydb.vderapp.main.data.models.forecast

import com.alrydb.vderapp.main.data.models.weather.Weather
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class HourlyForecast(


    val dt: Int,
    val temp: Double,
    @SerializedName("feels_like")
    val feelsLike: Double,
    val pressure: Int,
    val humidity: Double,
    @SerializedName("dew_point")
    val dew_point : Double,
    val uvi : Double,
    val clouds: Double,
    val visibility : Int,
    @SerializedName("wind_speed")
    val windSpeed : Double,
    @SerializedName("wind_deg")
    val windDeg : Int,
    @SerializedName("wind_gust")
    val windGust : Double,
    val weather: List<Weather>,
    val pop : Double,
    val rain : Rain?


) : Serializable
