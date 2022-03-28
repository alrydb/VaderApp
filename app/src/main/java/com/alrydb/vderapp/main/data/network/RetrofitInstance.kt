package com.alrydb.vderapp.main.data.network

import com.alrydb.vderapp.main.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val retrofit by lazy{
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    val api : WeatherService by lazy{
        retrofit.create(WeatherService::class.java)
    }


}