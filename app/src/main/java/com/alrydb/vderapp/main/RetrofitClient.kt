package com.alrydb.vderapp.main


import com.alrydb.vderapp.main.data.network.WeatherService
import com.alrydb.vderapp.main.utils.Constants

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {


    private val retrofit : Retrofit = Retrofit.Builder().baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()



    fun getAPI (): WeatherService{
        return retrofit.create(WeatherService::class.java)
    }

}