package com.alrydb.vderapp.main.data.repo

import android.util.Log
import com.alrydb.vderapp.main.data.models.DailyForecast
import com.alrydb.vderapp.main.data.network.RetrofitInstance
import com.alrydb.vderapp.main.utils.Constants
import retrofit2.Call

class DailyForecastRepository {

    fun getDailyForecast(lat : Double, lon : Double): Call<DailyForecast> {

        Log.i("response", "GETFORECAST CALLED FROM REPO")
        return RetrofitInstance.weatherApi.getDailyForecast(lat, lon, Constants.METRIC_UNIT, Constants.APP_ID, Constants.LANG)

    }


}