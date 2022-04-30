package com.alrydb.vderapp.main.data.models.forecast

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Rain(

    @SerializedName("1h")
    val rain : Double

) : Serializable
