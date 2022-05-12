package com.alrydb.vderapp.main.data.network

import com.alrydb.vderapp.main.data.models.weather.Forecast
import com.alrydb.vderapp.main.data.models.weather.WeatherResponse
import com.alrydb.vderapp.main.data.models.forecast.DailyForecastResponse
import com.alrydb.vderapp.main.data.models.forecast.HourlyForecastResponse
import com.alrydb.vderapp.main.data.models.location.LocationResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    // http GET request med tillagda parametrar
    // Exempel på hur url:en kommer se ut https://api.openweathermap.org/data/2.5/weather?lat=35&lon=139&units=metric&appid=572c5e9a56daaad8dd3502bf88c4b5c7&lang=se

    // Nuvarande väder
    @GET("data/2.5/weather")
       fun getWeather(
        @Query("lat") lat : Double,
        @Query("lon") lon : Double,
        @Query("units") units: String?,
        @Query("appid") appid : String?,
        @Query("lang") lang : String?,
    ) : Call<WeatherResponse> // Returnerar ett gson  objekt då det är den "converter" vi angett i vår Retrofitbuilder

    // 7 dagars prognos
    @GET("data/2.5/onecall")
    fun getForecast(
        @Query("lat") lat : Double,
        @Query("lon") lon : Double,
        @Query("exclude") exclude : String?,
        @Query("units") units: String?,
        @Query("appid") appid : String?,
        @Query("lang") lang : String?,

    ) : Call<DailyForecastResponse>

    // 24 timmars prognos
    @GET("data/2.5/onecall")
    fun getHourlyForecast(
        @Query("lat") lat : Double,
        @Query("lon") lon : Double,
        @Query("exclude") exclude : String?,
        @Query("units") units: String?,
        @Query("appid") appid : String?,
        @Query("lang") lang : String?,

        ) : Call<HourlyForecastResponse>


    // Plats
    @GET("geo/1.0/direct")
    fun getSearchedLocation(
        @Query("q") q : String?,
        @Query("limit") limit : Int,
        @Query("appid") appid : String?,

        ) : Call<LocationResponse>

}