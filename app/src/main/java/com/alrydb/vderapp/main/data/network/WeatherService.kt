package com.alrydb.vderapp.main.data.network

import com.alrydb.vderapp.main.data.models.weather.Forecast
import com.alrydb.vderapp.main.data.models.weather.WeatherResponse
import com.alrydb.vderapp.main.data.models.forecast.DailyForecastResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

// https://api.openweathermap.org/data/2.5/weather?lat=35&lon=139&appid=572c5e9a56daaad8dd3502bf88c4b5c7

    @GET("2.5/weather")
       fun getWeather(
        @Query("lat") lat : Double,
        @Query("lon") lon : Double,
        @Query("units") units: String?,
        @Query("appid") appid : String?,
        @Query("lang") lang : String?,
    ) : Call<WeatherResponse> // Returnera ett WeatherResponse GSON-objekt


    @GET("2.5/onecall")
    fun getForecast(
        @Query("lat") lat : Double,
        @Query("lon") lon : Double,
        @Query("exclude") exclude : String?,
        @Query("units") units: String?,
        @Query("appid") appid : String?,
        @Query("lang") lang : String?,

    ) : Call<DailyForecastResponse> // Returnera ett DailyForecastResponse GSON-objekt

}