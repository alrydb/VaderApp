package com.alrydb.vderapp.main.data.repo

import android.util.Log
import com.alrydb.vderapp.main.data.models.weather.WeatherResponse
import com.alrydb.vderapp.main.data.network.RetrofitInstance
import com.alrydb.vderapp.main.utils.Constants
import retrofit2.Call

class WeatherRepository() {



      fun getWeather(lat : Double, lon : Double): Call<WeatherResponse> {

          Log.i("response", "GETWEATHER CALLEWD FROM REPO")
          // API-anrop, skickar ett request till webservern som retunerar ett svar som är av typen WeatherResponse
        return RetrofitInstance.weatherApi.getWeather(lat, lon, Constants.METRIC_UNIT, Constants.APP_ID, Constants.LANG)

    }



}
