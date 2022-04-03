package com.alrydb.vderapp.main.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alrydb.vderapp.databinding.ForecastItemBinding
import com.alrydb.vderapp.main.data.models.DailyForecast
import com.alrydb.vderapp.main.data.models.WeatherResponse


class DailyForecastAdapter(val dailyForecast : DailyForecast) : RecyclerView.Adapter<DailyForecastAdapter.MainViewHolder>() {

    inner class MainViewHolder(val itemBinding: ForecastItemBinding)
        :RecyclerView.ViewHolder(itemBinding.root){
        fun bindItem(weatherResponse: WeatherResponse)
        {

           // for (weatherResponse in dailyForecast.list)
           // {
                    itemBinding.tvDate.text = weatherResponse.main.temp_max.toString()
                    itemBinding.tvTvTemp.text = weatherResponse.main.temp_min.toString()


            //}

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(ForecastItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val forecast = dailyForecast.list[position]
        holder.bindItem(forecast)
    }

    override fun getItemCount(): Int {
        return dailyForecast.list.size
    }


}