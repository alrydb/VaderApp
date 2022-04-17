package com.alrydb.vderapp.main.data.models.forecast


import com.alrydb.vderapp.main.data.models.weather.Weather
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DailyForecast(

    val dt: Int,
    val sunrise:Int,
    val sunset : Int,
    val moonrise : Int,
    val moonset:Int,
    @SerializedName("moon_phase")
    val moonPhase: Double,
    val temp: Temp,
    val feelsLike: FeelsLike,
    val pressure: Int,
    val humidity: Int,
    @SerializedName("dew_point")
    val dewPoint : Double,
    @SerializedName("wind_speed")
    val windSpeed : Double,
    @SerializedName("wind_deg")
    val windDeg : Int,
    @SerializedName("wind_gust")
    val windGust : Double,
    val weather: List<Weather>,
    val clouds: Int,
    val pop : Double,
    val rain : Double,
    val uvi : Double


) : Serializable
