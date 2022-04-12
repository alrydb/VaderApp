package com.alrydb.vderapp.main.view

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.alrydb.vderapp.R
import com.alrydb.vderapp.databinding.ActivityMainBinding
import com.alrydb.vderapp.main.viewmodel.ViewModelFactory
import com.alrydb.vderapp.main.data.repo.DailyForecastRepository
import com.alrydb.vderapp.main.data.repo.WeatherRepository
import com.alrydb.vderapp.main.utils.Constants
import com.alrydb.vderapp.main.utils.NetworkController

import com.alrydb.vderapp.main.viewmodel.WeatherInfoViewModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: WeatherInfoViewModel
    private lateinit var binding : ActivityMainBinding

    lateinit var adapter : DailyForecastAdapter



    // Skapa menyn
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)

        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Få referenser till alla ui-komponenter med hjälp av viewbinding
        binding = ActivityMainBinding.inflate(layoutInflater)

        //Rendera UI-element
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarNav)
        binding.toolbarNav.inflateMenu(R.menu.options_menu)


        //skapa viewmodel
        val viewModelFactory = ViewModelFactory(dailyForecastRepository = DailyForecastRepository(), weatherRepository = WeatherRepository())

        viewModel = ViewModelProvider(this, viewModelFactory).get(WeatherInfoViewModel::class.java)


        //val weatherRepository = WeatherRepository()
        //val dailyForecastRepository = DailyForecastRepository()


        // Om appen har tillgång till mobilen plats samt om mobilen är uppkopplad till internet så hämtas och presenteras väderdata
        if (networkEnabled() && locationEnabled())
        {
            showCurrentWeather()
            showDailyForecast()

        }

        // När användaren refreshar appen
        binding.refreshLayout.setOnRefreshListener(){

           if (networkEnabled() && locationEnabled())
           {
               binding.refreshLayout.isRefreshing = false
               viewModel.refreshLocationData(this@MainActivity)
               showCurrentWeather()
               showDailyForecast()
           }
            else if (!locationEnabled())
           {
               binding.refreshLayout.isRefreshing = false
               Toast.makeText(this, "Plats är inte aktiverad", Toast.LENGTH_SHORT).show()

            }
            else if (!networkEnabled())
           {
               binding.refreshLayout.isRefreshing = false
               Toast.makeText(this, "Kunde inte koppla upp till internet, väderdata kan inte hämtas", Toast.LENGTH_SHORT).show()
            }


        }


    }


    private fun networkEnabled() : Boolean
    {
        var networkEnabled = false


        if (NetworkController.isNetWorkAvailable(this))
        {
            networkEnabled = true
        }
        else
        {
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                Toast.makeText(
                    this,
                    "Kunde inte koppla upp till internet, väderdata kan inte hämtas",
                    Toast.LENGTH_SHORT
                ).show()
            }, 5000)

        }
        return networkEnabled

    }


    private fun locationEnabled() : Boolean
    {
        var locationEnabled = false
        // Kollar om platstjänsten är aktiverad
        if(!viewModel.isLocationEnabled(this)){

            Toast.makeText(this, "Platstjänsten är inte aktiverad", Toast.LENGTH_SHORT).show()

            //Om platstjänsten inte är aktiverad så omdirigeras användaren
            // Till mobilens inställningar för platstjänst
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)

        }else{
            Dexter.withContext(this).withPermissions(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
                .withListener(object : MultiplePermissionsListener{
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?){
                        // Om behörigheter tillåts av användaren
                        if (report!!.areAllPermissionsGranted()){

                            viewModel.requestLocationData(this@MainActivity)
                            locationEnabled = true

                            showCurrentWeather()
                            showDailyForecast()

                        }
                        // Om behörigheter nekas av användaren
                        if (report.isAnyPermissionPermanentlyDenied){

                            Toast.makeText(this@MainActivity, "Behörighet till mobilens platstjänst har nekats", Toast.LENGTH_SHORT).show()

                        }                        }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        showRationalDialogForPermissions()
                    }
                }).onSameThread().check()


        }
        return locationEnabled
    }


    private fun showRationalDialogForPermissions(){
        AlertDialog.Builder(this)
            .setMessage("Behörighet till platstjänsten är inte aktiverad")
            .setPositiveButton("INSTÄLLNINGAR"){
                _,_ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }
                catch(e: ActivityNotFoundException){
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel"){
                dialog, _ ->
                dialog.dismiss()
            }.show()
    }


    private fun showCurrentWeather()
    {
        // Observera viewmodel
        viewModel.currentWeatherList.removeObservers(this)
        viewModel.currentWeatherList.observe(this, Observer { weatherResponse ->
            Log.i("response", weatherResponse.id.toString())
            Log.i("response", weatherResponse.visibility.toString())
            Log.i("response", weatherResponse.weather[0].icon)


            // Visa väderdata för nuvarande plats och tid
            binding.cityName.text = weatherResponse.name
            binding.countryName.text = weatherResponse.sys.country
            binding.currentTemp.text = weatherResponse.main.temp.toString().substringBefore(".") + "°C"
            binding.currentDescription.text = weatherResponse.weather[0].description.replaceFirstChar {
                weatherResponse.weather[0].description[0].uppercase()
            }
            // Hämta ikon
            val uri = "https://openweathermap.org/img/w/" + weatherResponse.weather[0].icon + ".png"
            Picasso.get().load(uri).into(binding.iconWeather)

        })
    }


    private fun showDailyForecast()
    {
        // Visa 7 dagars-prognos för nuvarande plats
        viewModel.dailyForecastList.observe(this, Observer { dailyForecastResponse ->
                binding?.forecastRv?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

                // Skicka data som hämtas från api:n till adaptern
                binding?.forecastRv?.adapter = DailyForecastAdapter(dailyForecastResponse)

        })

    }



}