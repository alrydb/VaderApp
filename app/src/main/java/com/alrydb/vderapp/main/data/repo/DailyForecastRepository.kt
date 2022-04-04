package com.alrydb.vderapp.main.data.repo

import android.util.Log
import com.alrydb.vderapp.main.data.models.forecast.DailyForecast
import com.alrydb.vderapp.main.data.models.forecast.DailyForecastResponse
import com.alrydb.vderapp.main.data.network.RetrofitInstance
import com.alrydb.vderapp.main.utils.Constants
import retrofit2.Call

class DailyForecastRepository {

    fun getDailyForecast(lat : Double, lon : Double): Call<DailyForecastResponse> {

        Log.i("response", "GETFORECAST CALLED FROM REPO")
        return RetrofitInstance.weatherApi.getForecast(lat, lon,"current,hourly,minutely,alerts", Constants.METRIC_UNIT, Constants.APP_ID, Constants.LANG)

    }


}