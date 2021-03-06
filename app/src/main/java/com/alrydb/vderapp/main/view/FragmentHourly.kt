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
import com.alrydb.vderapp.databinding.FragmentHourlyBinding
import com.squareup.picasso.Picasso


class FragmentHourly : Fragment() {

    private var fragmentBinding : FragmentHourlyBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hourly, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentHourlyBinding.bind(view)
        fragmentBinding = binding

        fragmentBinding!!.detailsClear.setOnClickListener(){
            activity!!.onBackPressed()
        }



    }

    // Funktion som ser till att väderdata för den valda prognosen är den som presenteras
    @SuppressLint("SetTextI18n")
    fun showWeatherDetails(dateTime: String, temp: Double, feelsLike: Double, description: String, rain : Double, wind : Double, clouds : Double, humidity : Double, icon : String, pop : Double)
    {

            fragmentBinding!!.detailsDate.text =  dateTime
            fragmentBinding!!.detailsTemp.text =   " " + temp.toString().substringBefore(".") + "°C"
            fragmentBinding!!.detailsFeelsLike.text = resources.getString(R.string.temp_feelslike) + " " + feelsLike.toString().substringBefore(".") + "°C"
            fragmentBinding!!.detailsDescription.text = description.replaceFirstChar {
                description[0].uppercase()
            }

            fragmentBinding!!.detailsPop.text = resources.getString(R.string.details_pop) + " " + (pop * 100).toInt().toString() + "%"
            fragmentBinding!!.detailsRain.text = resources.getString(R.string.details_rain) + " " + rain.toString() + " mm"
            fragmentBinding!!.detailsWind.text = resources.getString(R.string.details_wind) + " " + wind.toInt().toString() + " m/s"
            fragmentBinding!!.detailsClouds.text = resources.getString(R.string.details_clouds) + " " + clouds.toInt().toString() + "%"
            fragmentBinding!!.detailsHumidity.text = resources.getString(R.string.details_humidity) + " " + humidity.toInt().toString() + "%"

            var cityTextView : TextView = activity!!.findViewById(R.id.city_name)
            fragmentBinding!!.detailsCity.text = cityTextView.text

            val uri = "https://openweathermap.org/img/w/" + icon + ".png"
            Picasso.get().load(uri).into(fragmentBinding!!.detailsIcon)

        }


    override fun onDestroyView() {
        fragmentBinding = null
        super.onDestroyView()
    }



}