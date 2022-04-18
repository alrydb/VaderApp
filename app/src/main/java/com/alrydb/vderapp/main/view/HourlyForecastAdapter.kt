package com.alrydb.vderapp.main.view

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.alrydb.vderapp.R
import com.alrydb.vderapp.databinding.ForecastItemBinding
import com.alrydb.vderapp.databinding.HourlyForecastItemBinding
import com.alrydb.vderapp.main.data.models.forecast.DailyForecast
import com.alrydb.vderapp.main.data.models.forecast.DailyForecastResponse
import com.alrydb.vderapp.main.data.models.forecast.HourlyForecast
import com.alrydb.vderapp.main.data.models.forecast.HourlyForecastResponse
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*


class HourlyForecastAdapter(val hourlyForecastResponse : List<HourlyForecast>) : RecyclerView.Adapter<HourlyForecastAdapter.MainViewHolder>() {

    inner class MainViewHolder(val itemBinding: HourlyForecastItemBinding)
        :RecyclerView.ViewHolder(itemBinding.root){

        // Vad varje rad i recyclerviewen ska innehålla
        fun bindItem(hourlyForecast: HourlyForecast)
        {
            val uri = "https://openweathermap.org/img/w/" + hourlyForecast.weather[0].icon + ".png"
            Picasso.get().load(uri).into(itemBinding.ivWeatherIconHourly)

            val calendar: Calendar = Calendar.getInstance()

            // 'dt' ger tid för prognosen i "epoch time"
            Log.i("time", "${hourlyForecast.dt}")
            calendar.setTimeInMillis((hourlyForecast.dt * 1000L))

            val hour = SimpleDateFormat("HH")
            val hourName = hour.format(calendar.time)

            itemBinding.tvHour.text = "Kl " + hourName.toString() + ": 00"
            itemBinding.tvTempCurrent.text = hourlyForecast.temp.toString().substringBefore(".") + "°C"
            itemBinding.tvTempFeelslike.text = hourlyForecast.feelsLike.toString().substringBefore(".") + "°C"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(HourlyForecastItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val forecast = hourlyForecastResponse[position]
        // Tilldelar varje rad i recyclerviewen rätt innehåll
        holder.bindItem(forecast)

        // Varannan rad får ljusgrå bakgrund istället för vit
        if (position % 2 == 0) {
            holder.itemBinding.root.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.colorLightGray
                )
            )
        } else {
            holder.itemBinding.root.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
        }



    }

    override fun getItemCount(): Int {
        return hourlyForecastResponse.size
    }


}