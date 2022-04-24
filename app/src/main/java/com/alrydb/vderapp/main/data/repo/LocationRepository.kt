package com.alrydb.vderapp.main.data.repo

import android.util.Log
import com.alrydb.vderapp.main.data.models.location.LocationResponse
import com.alrydb.vderapp.main.data.network.RetrofitInstance
import com.alrydb.vderapp.main.utils.Constants
import retrofit2.Call

class LocationRepository {

    fun getSearchedLocation(name : String): Call<LocationResponse> {

        Log.i("response", "LOCATION CALLED FROM REPO")
        return RetrofitInstance.weatherApi.getSearchedLocation(name,1,Constants.APP_ID)

    }

}