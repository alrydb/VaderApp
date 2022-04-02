package com.alrydb.vderapp.main.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alrydb.vderapp.databinding.ForecastItemBinding
import com.alrydb.vderapp.main.data.models.WeatherResponse


class DailyForecastAdapter(val forecastList : List<WeatherResponse>) : RecyclerView.Adapter<DailyForecastAdapter.MainViewHolder>() {

    inner class MainViewHolder(val itemBinding: ForecastItemBinding)
        :RecyclerView.ViewHolder(itemBinding.root){
        fun bindItem(weatherResponse: WeatherResponse)
        {
            for (weatherResponse in forecastList)
            {
                itemBinding.dateTv.text = weatherResponse.name
                itemBinding.iconForecast.text = weatherResponse.main.temp.toString()
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(ForecastItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val forecast = forecastList[position]
        holder.bindItem(forecast)
    }

    override fun getItemCount(): Int {
        return forecastList.size
    }


}