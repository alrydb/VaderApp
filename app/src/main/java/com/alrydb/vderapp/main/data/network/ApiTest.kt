package com.alrydb.vderapp.main.data.network

import com.alrydb.vderapp.main.data.models.WeatherResponse
import retrofit2.http.GET

interface ApiTest {

    @GET("2.5/weather")
    suspend fun getWeatherResponse() : WeatherResponse


}