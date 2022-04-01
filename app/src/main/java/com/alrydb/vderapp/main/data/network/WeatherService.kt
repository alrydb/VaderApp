package com.alrydb.vderapp.main.data.network

import com.alrydb.vderapp.main.data.models.Forecast
import com.alrydb.vderapp.main.data.models.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {


    @GET("2.5/weather")
       fun getWeather(
        @Query("lat") lat : Double,
        @Query("lon") lon : Double,
        @Query("units") units: String?,
        @Query("appid") appid : String?,
        @Query("lang") lang : String?,
    ) : Call<WeatherResponse> // Returnera ett WeatherResponse GSON-objekt


    @GET("2.5/forecast")
    fun getForecast(
        @Query("lat") lat : Double,
        @Query("lon") lon : Double,
        @Query("units") units: String?,
        @Query("appid") appid : String?,
        @Query("lang") lang : String?,
    ) : Call<Forecast> // Returnera ett Forecast GSON-objekt
}