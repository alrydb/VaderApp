package com.alrydb.vderapp.main.data.models.weather

import java.io.Serializable

data class City
    (
    val id : String,
    val name: String,
    val coord: Coord,
    val country : String,

    ) : Serializable