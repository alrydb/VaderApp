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
import com.alrydb.vderapp.databinding.ActivityMainBinding
import com.alrydb.vderapp.databinding.FragmentHourlyBinding


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

        fragmentBinding!!.detailsTemp.text = "hello world"



    }

    @SuppressLint("SetTextI18n")
    fun showWeatherDetails(dateTime: String, temp: Double, feelsLike: Double, description: String, rain : Double, wind : Double, clouds : Double, humidity : Double)
    {
        if (activity?.isDestroyed == false && this.isAdded && view != null) {
            fragmentBinding!!.detailsDate.text =  dateTime
            fragmentBinding!!.detailsTemp.text = resources.getString(R.string.temp_current) + " " + temp.toString().substringBefore(".") + "°C"
            fragmentBinding!!.detailsFeelsLike.text = resources.getString(R.string.temp_feelslike) + " " + feelsLike.toString().substringBefore(".") + "°C"
            fragmentBinding!!.detailsDescription.text = description.replaceFirstChar {
                description[0].uppercase()
            }
            fragmentBinding!!.detailsRain.text = resources.getString(R.string.details_rain) + " " + rain.toString()
            fragmentBinding!!.detailsWind.text = resources.getString(R.string.details_wind) + " " + wind.toString()
            fragmentBinding!!.detailsClouds.text = resources.getString(R.string.details_clouds) + " " + clouds.toString()
            fragmentBinding!!.detailsHumidity.text = resources.getString(R.string.details_humidity) + " " + humidity.toString()

            Log.i("fragment", "it worked")
        }
        else
        {
            Log.i("fragment", "did not worjk")
            /*fragmentBinding!!.detailsTemp.text = temp*/
        }
    }

    override fun onDestroyView() {
        fragmentBinding = null
        super.onDestroyView()
    }



}