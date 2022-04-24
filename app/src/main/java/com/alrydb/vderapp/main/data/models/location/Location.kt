package com.alrydb.vderapp.main.data.models.location

import java.io.Serializable

data class Location (
    val country: String,
    val lat: Double,
    val local_names: LocalNames,
    val lon: Double,
    val name: String

        ) : Serializable
