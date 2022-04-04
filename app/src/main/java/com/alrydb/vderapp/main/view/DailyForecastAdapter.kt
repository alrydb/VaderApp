package com.alrydb.vderapp.main.view

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alrydb.vderapp.databinding.ForecastItemBinding
import com.alrydb.vderapp.main.data.models.forecast.DailyForecast
import com.alrydb.vderapp.main.data.models.forecast.DailyForecastResponse
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*


class DailyForecastAdapter(val dailyForecastResponse : DailyForecastResponse) : RecyclerView.Adapter<DailyForecastAdapter.MainViewHolder>() {

    inner class MainViewHolder(val itemBinding: ForecastItemBinding)
        :RecyclerView.ViewHolder(itemBinding.root){
        fun bindItem(dailyForecast: DailyForecast)
        {

           // for (weatherResponse in dailyForecast.list)
           // {
                    val uri = "https://openweathermap.org/img/w/" + dailyForecast.weather[0].icon + ".png"
                    Picasso.get().load(uri).into(itemBinding.ivWeatherIcon)
            val calendar: Calendar = Calendar.getInstance()
            Log.i("time", "${dailyForecast.dt}")
            calendar.setTimeInMillis((dailyForecast.dt * 1000L))


               /* val year: Int = calendar.get(Calendar.YEAR)
                val month: Int = calendar.get(Calendar.MONTH)
                val day: Int = calendar.get(Calendar.DAY_OF_MONTH)*/

                //val simpleDateFormat = SimpleDateFormat("MM", Locale.ENGLISH)

                //val date = simpleDateFormat.parse(month.toString())
                val weekdayDate = SimpleDateFormat("E")
                val weekdayDateName = weekdayDate.format(calendar.time)
                val monthDate = SimpleDateFormat("MMMM")
                val monthName = monthDate.format(calendar.time)
                val dayDate = SimpleDateFormat("dd")
                val dayName = dayDate.format(calendar.time)
                    itemBinding.tvDay.text = weekdayDateName + " "
                    itemBinding.tvDate.text =  monthName + " " + dayName
                    itemBinding.tvTvTemp.text = dailyForecast.temp.min.toString()



            //}

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(ForecastItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val forecast = dailyForecastResponse.daily[position]
        holder.bindItem(forecast)
    }

    override fun getItemCount(): Int {
        return dailyForecastResponse.daily.size
    }


}