package com.alrydb.vderapp.main.data.models.weather

import java.io.Serializable

data class Main(
    val temp: Double,
    val pressure: Double,
    val humidity: Int,
    val temp_min: Double,
    val temp_max: Double,
) : Serializable