package com.alrydb.vderapp.main.view

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.alrydb.vderapp.R
import com.alrydb.vderapp.databinding.ForecastItemBinding
import com.alrydb.vderapp.main.data.models.forecast.DailyForecast
import com.alrydb.vderapp.main.data.models.forecast.DailyForecastResponse
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*


class DailyForecastAdapter(val dailyForecastResponse : DailyForecastResponse, val context: Context, val timeZone: String) : RecyclerView.Adapter<DailyForecastAdapter.MainViewHolder>() {

    private lateinit var fragmentDaily: FragmentDaily

    inner class MainViewHolder(val itemBinding: ForecastItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        // Vad varje rad i recyclerviewen ska innehålla
        fun bindItem(dailyForecast: DailyForecast) {
            val uri = "https://openweathermap.org/img/w/" + dailyForecast.weather[0].icon + ".png"
            Picasso.get().load(uri).into(itemBinding.ivWeatherIcon)

            val calendar: Calendar = Calendar.getInstance()
            Log.i("time", "${dailyForecast.dt}")
            calendar.setTimeInMillis((dailyForecast.dt * 1000L))

            val weekdayDate = SimpleDateFormat("E")
            val weekdayDateName = weekdayDate.format(calendar.time)
            val monthDate = SimpleDateFormat("MMMM")
            val monthName = monthDate.format(calendar.time)
            val dayDate = SimpleDateFormat("d")
            val dayName = dayDate.format(calendar.time)

            itemBinding.tvDay.text = weekdayDateName + " "
            itemBinding.tvDate.text = dayName + " " + monthName
            itemBinding.tvTempMax.text =
                dailyForecast.temp.max.toString().substringBefore(".") + "°C"
            itemBinding.tvTempMin.text =
                dailyForecast.temp.min.toString().substringBefore(".") + "°C"

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            ForecastItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val forecast = dailyForecastResponse.daily[position]
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
            holder.itemBinding.root.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.white
                )
            )
        }

        holder.itemView.setOnClickListener() {

            fragmentDaily = FragmentDaily()
            val fragmentManager = (context as AppCompatActivity).supportFragmentManager
            fragmentManager.beginTransaction().apply {
                replace(R.id.fragment_daily, fragmentDaily)


                val groupCurrentWeather: androidx.constraintlayout.widget.Group =
                    context.findViewById(R.id.current_group)
                val groupMenu: androidx.constraintlayout.widget.Group =
                    context.findViewById(R.id.menu_group)
                val groupForecast: androidx.constraintlayout.widget.Group =
                    context.findViewById(R.id.forecast_group)
                val menu: androidx.appcompat.widget.Toolbar = context.findViewById(R.id.toolbar_nav)


                groupCurrentWeather.isInvisible = true
                groupMenu.isInvisible = true
                groupForecast.isGone = true
                menu.isInvisible = true


                addToBackStack(null)
                commit()


            }

            fragmentManager.executePendingTransactions()


            val tz = TimeZone.getTimeZone(timeZone)

            Log.i("timezone", dailyForecastResponse.timezone)
            val calendarTime: Calendar = Calendar.getInstance()
            calendarTime.setTimeInMillis((forecast.dt  * 1000L))
            val dayDate = SimpleDateFormat("d")
            val dayName = dayDate.format(calendarTime.time)
            val monthDate = SimpleDateFormat("MMMM")
            val monthName = monthDate.format(calendarTime.time)


            val calendarSunrise: Calendar = Calendar.getInstance()
            /*calendarSunrise.setTimeInMillis(((forecast.sunrise + dailyForecastResponse.timezoneOffset) * 1000L) -7200000)*/
            calendarSunrise.setTimeInMillis((forecast.sunrise) * 1000L)

           /* calendarSunrise.setTimeZone(timeZone)*/
            val sunriseFormat = SimpleDateFormat("HH:mm")
            sunriseFormat.setTimeZone(tz)
            val sunriseTime = sunriseFormat.format(calendarSunrise.time)


            val calendarSunset: Calendar = Calendar.getInstance()
            /*calendarSunset.setTimeZone(timeZone)*/
            /*calendarSunset.setTimeInMillis(((forecast.sunset + dailyForecastResponse.timezoneOffset) * 1000L ) - 7200000)*/
            calendarSunset.setTimeInMillis((forecast.sunset) * 1000L)

            val sunsetFormat = SimpleDateFormat("HH:mm")
            sunsetFormat.setTimeZone(tz)
            val sunsetTime = sunsetFormat.format(calendarSunset.time)





            fragmentDaily.showWeatherDetails( dayName + " " + monthName , forecast.temp.max,
                forecast.temp.min, forecast.weather[0].description, forecast.rain, forecast.windSpeed, forecast.clouds.toDouble(), forecast.humidity.toDouble(), forecast.weather[0].icon, sunriseTime, sunsetTime
            )

        }


    }

    override fun getItemCount(): Int {
        return dailyForecastResponse.daily.size
    }
}