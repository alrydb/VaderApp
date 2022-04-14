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
    @SerializedName("moonPhase")
    val moon_phase: Double,
    val temp: Temp,
    val feelsLike: FeelsLike,
    val pressure: Int,
    val humidity: Int,
    @SerializedName("dewPoint")
    val dew_point : Double,
    @SerializedName("windSpeed")
    val wind_speed : Double,
    @SerializedName("windDeg")
    val wind_deg : Int,
    @SerializedName("windGust")
    val wind_gust : Double,
    val weather: List<Weather>,
    val clouds: Int,
    val pop : Double,
    val rain : Double,
    val uvi : Double


) : Serializable
