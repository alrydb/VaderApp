package com.alrydb.vderapp.main.data.models.weather

import java.io.Serializable

data class Forecast(


    val list: List<WeatherResponse>,
    val city : City

) : Serializable

