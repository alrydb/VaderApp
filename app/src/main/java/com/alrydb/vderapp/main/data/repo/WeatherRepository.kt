package com.alrydb.vderapp.main.data.repo

import com.alrydb.vderapp.main.data.models.WeatherResponse
import com.alrydb.vderapp.main.data.network.RetrofitInstance
import com.alrydb.vderapp.main.utils.Constants
import retrofit2.Call

class WeatherRepository() {


    //private var allWeatherResponse : MutableLiveData<ArrayList<WeatherResponse>> = MutableLiveData()
    //private var weatherResponseList : ArrayList<WeatherResponse> = ArrayList()


    // parametrar lon : Double, lat :Double, units: String?, appid : String?


      fun getWeather(): Call<WeatherResponse> {

        return RetrofitInstance.api.getWeather(35.0, 139.0, Constants.METRIC_UNIT, Constants.APP_ID)

    }

}
