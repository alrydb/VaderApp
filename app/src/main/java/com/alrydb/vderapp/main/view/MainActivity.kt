package com.alrydb.vderapp.main.view

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import com.alrydb.vderapp.R
import com.alrydb.vderapp.main.viewmodel.WeatherInfoViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: WeatherInfoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //skapa viewmodel
        viewModel = WeatherInfoViewModel()




        if(!viewModel.isLocationEnabled(this)){

            Toast.makeText(this, "Platstjänsten är inte aktiverad", Toast.LENGTH_SHORT).show()

            //Om platstjänsten inte är aktiverad så omdirigeras användaren till
            // Till mobilens inställningar för platstjänst
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)


            }else{

            Toast.makeText(this, "Platstjänsten är aktiverad", Toast.LENGTH_SHORT).show()

             }

    }




}