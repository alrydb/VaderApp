package com.alrydb.vderapp.main.data.repo

import android.util.Log
import com.alrydb.vderapp.main.data.models.WeatherResponse
import com.alrydb.vderapp.main.data.network.RetrofitInstance
import com.alrydb.vderapp.main.utils.Constants
import retrofit2.Call

class WeatherRepository() {


    //private var allWeatherResponse : MutableLiveData<ArrayList<WeatherResponse>> = MutableLiveData()
    //private var weatherResponseList : ArrayList<WeatherResponse> = ArrayList()


    // parametrar lon : Double, lat :Double, units: String?, appid : String?


      fun getWeather(lat : Double, lon : Double): Call<WeatherResponse> {

          Log.i("response", "GETWEATHER CALLEWD FROM REPO")
        return RetrofitInstance.weatherApi.getWeather(lat, lon, Constants.METRIC_UNIT, Constants.APP_ID, Constants.LANG)

    }

}
