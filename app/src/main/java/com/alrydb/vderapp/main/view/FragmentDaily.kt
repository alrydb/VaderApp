package com.alrydb.vderapp.main.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.alrydb.vderapp.R
import com.alrydb.vderapp.databinding.FragmentDailyBinding
import com.squareup.picasso.Picasso


class FragmentDaily : Fragment() {

    private var fragmentBinding : FragmentDailyBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_daily, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentDailyBinding.bind(view)
        fragmentBinding = binding

        fragmentBinding!!.detailsClear.setOnClickListener(){
            activity!!.onBackPressed()
        }



    }

    // Funktion som ser till att väderdata för den valda prognosen är den som presenteras
    @SuppressLint("SetTextI18n")
    fun showWeatherDetails(dateTime: String, maxTemp: Double, minTemp: Double, description: String, rain : Double, wind : Double,
                           clouds : Double, humidity : Double, icon : String, sunrise : String, sunset :String, pop : Double)
    {

            fragmentBinding!!.detailsDate.text =  dateTime
            fragmentBinding!!.detailsTempMax.text =   " " + maxTemp.toString().substringBefore(".") + "°C" + " " + resources.getString(R.string.temp_max)
            fragmentBinding!!.detailsTempMin.text =  " " + minTemp.toString().substringBefore(".") + "°C" + " " + resources.getString(R.string.temp_min)
            fragmentBinding!!.detailsDescription.text = description.replaceFirstChar {
                description[0].uppercase()
           }

            fragmentBinding!!.detailsDailyPop.text = resources.getString(R.string.details_pop) + " " + (pop * 100).toInt().toString() + "%"
            fragmentBinding!!.detailsRain.text = resources.getString(R.string.details_rain) + " " + rain + " mm"
            fragmentBinding!!.detailsWind.text = resources.getString(R.string.details_wind) + " " + wind.toInt().toString() + " m/s"
            fragmentBinding!!.detailsClouds.text = resources.getString(R.string.details_clouds) + " " + clouds.toInt().toString() + "%"
            fragmentBinding!!.detailsHumidity.text = resources.getString(R.string.details_humidity) + " " + humidity.toInt().toString() + "%"

            fragmentBinding!!.detailsSunrise.text = resources.getString(R.string.details_sunrise) + " " + sunrise + " " + resources.getString(R.string.local_time)
            fragmentBinding!!.detailsSunset.text = resources.getString(R.string.details_sunset) + " " + sunset + " " + resources.getString(R.string.local_time)

            var cityTextView : TextView = activity!!.findViewById(R.id.city_name)
            fragmentBinding!!.detailsCity.text = cityTextView.text


            val uri = "https://openweathermap.org/img/w/" + icon + ".png"
            Picasso.get().load(uri).into(fragmentBinding!!.detailsIcon)

            Picasso.get().load("https://openweathermap.org/img/w/" + "02d" + ".png").into(fragmentBinding!!.detailsIconSunrise)
            Picasso.get().load("https://openweathermap.org/img/w/" + "02n" + ".png").into(fragmentBinding!!.detailsIconSunset)



        }



    override fun onDestroyView() {
        fragmentBinding = null
        super.onDestroyView()
    }



}