package com.alrydb.vderapp.main.view

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.alrydb.vderapp.R
import com.alrydb.vderapp.databinding.FragmentHourlyBinding
import com.alrydb.vderapp.databinding.HourlyForecastItemBinding
import com.alrydb.vderapp.main.data.models.forecast.HourlyForecast
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*


class HourlyForecastAdapter(val hourlyForecastResponse : List<HourlyForecast>, val context: Context, val timeZone: String) : RecyclerView.Adapter<HourlyForecastAdapter.MainViewHolder>() {

    private lateinit var fragmentHourly: FragmentHourly


    inner class MainViewHolder(val itemBinding: HourlyForecastItemBinding)
        :RecyclerView.ViewHolder(itemBinding.root){

        // Vad varje rad i recyclerviewen ska innehålla
        fun bindItem(hourlyForecast: HourlyForecast)
        {
            val uri = "https://openweathermap.org/img/w/" + hourlyForecast.weather[0].icon + ".png"
            Picasso.get().load(uri).into(itemBinding.ivWeatherIconHourly)

            val calendar: Calendar = Calendar.getInstance()

            // 'dt' ger tid för prognosen i "epoch time"
            calendar.setTimeInMillis((hourlyForecast.dt * 1000L))

            val tz = TimeZone.getTimeZone(timeZone)
            val hour = SimpleDateFormat("HH")
            hour.setTimeZone(tz)
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

        // Hanterar klickevent för varje prognos
        holder.itemView.setOnClickListener(){


            Log.i("clicked", forecast.temp.toString())

            fragmentHourly = FragmentHourly()
            val fragmentManager = (context as AppCompatActivity).supportFragmentManager
            fragmentManager.beginTransaction().apply {
                replace(R.id.fragment_hourly, fragmentHourly)


                val groupCurrentWeather :  androidx.constraintlayout.widget.Group = context.findViewById(R.id.current_group)
                val groupMenu :  androidx.constraintlayout.widget.Group = context.findViewById(R.id.menu_group)
                val groupForecast :  androidx.constraintlayout.widget.Group = context.findViewById(R.id.forecast_group)
                val menu : androidx.appcompat.widget.Toolbar =  context.findViewById(R.id.toolbar_nav)

                groupCurrentWeather.isInvisible = true
                groupMenu.isInvisible = true
                groupForecast.isGone = true
                menu.isInvisible = true

                addToBackStack(null)
                commit()

            }


            fragmentManager.executePendingTransactions()

            val calendar: Calendar = Calendar.getInstance()
            calendar.setTimeInMillis((forecast.dt * 1000L))
            val tz = TimeZone.getTimeZone(timeZone)
            val hour = SimpleDateFormat("HH")
            hour.setTimeZone(tz)
            val hourName = hour.format(calendar.time)
            val dayDate = SimpleDateFormat("d")
            val dayName = dayDate.format(calendar.time)
            val monthDate = SimpleDateFormat("MMMM")
            val monthName = monthDate.format(calendar.time)



            fragmentHourly.showWeatherDetails( dayName + " " + monthName + " " + hourName + ": 00" , forecast.temp,
                 forecast.feelsLike, forecast.weather[0].description, forecast.rain?.rain ?: 0.0, forecast.windSpeed, forecast.clouds,
                forecast.humidity, forecast.weather[0].icon, forecast.pop
            )


        }


    }

    override fun getItemCount(): Int {
        return hourlyForecastResponse.size
    }



}