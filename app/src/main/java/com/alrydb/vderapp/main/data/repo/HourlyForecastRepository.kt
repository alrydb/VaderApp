package com.alrydb.vderapp.main.data.repo

import android.util.Log
import com.alrydb.vderapp.main.data.models.forecast.DailyForecastResponse
import com.alrydb.vderapp.main.data.models.forecast.HourlyForecastResponse
import com.alrydb.vderapp.main.data.network.RetrofitInstance
import com.alrydb.vderapp.main.utils.Constants
import retrofit2.Call

class HourlyForecastRepository {

    fun getHourlyForecast(lat : Double, lon : Double): Call<HourlyForecastResponse> {

        Log.i("response", "HOURLY CALLED FROM REPO")
        return RetrofitInstance.weatherApi.getHourlyForecast(lat, lon,"current,daily,minutely,alerts", Constants.METRIC_UNIT, Constants.APP_ID, Constants.LANG)

    }



}