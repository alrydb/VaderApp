package com.alrydb.vderapp.main.data.models.forecast

import java.io.Serializable

data class Temp(
    val day: Double,
    val min: Double,
    val max: Double,
    val night: Double,
    val eve: Double,
    val morn: Double

) : Serializable
