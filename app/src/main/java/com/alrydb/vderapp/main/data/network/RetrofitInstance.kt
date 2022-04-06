package com.alrydb.vderapp.main.data.network

import com.alrydb.vderapp.main.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val retrofit by lazy{
        Retrofit.Builder()
                // Url för den api som används
            .baseUrl(Constants.BASE_URL)
                // Gson används för att deserializea json data som vi får från vår api
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    val weatherApi : WeatherService by lazy{
        retrofit.create(WeatherService::class.java)
    }


}