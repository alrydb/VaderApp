package com.alrydb.vderapp.main.view

import android.app.SearchManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuItemCompat
import androidx.core.view.forEach
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alrydb.vderapp.R
import com.alrydb.vderapp.databinding.ActivityMainBinding
import com.alrydb.vderapp.main.data.models.forecast.DailyForecast
import com.alrydb.vderapp.main.data.models.forecast.HourlyForecast
import com.alrydb.vderapp.main.viewmodel.ViewModelFactory
import com.alrydb.vderapp.main.data.repo.DailyForecastRepository
import com.alrydb.vderapp.main.data.repo.HourlyForecastRepository
import com.alrydb.vderapp.main.data.repo.WeatherRepository
import com.alrydb.vderapp.main.utils.NetworkController

import com.alrydb.vderapp.main.viewmodel.WeatherInfoViewModel
import com.google.android.material.tabs.TabLayout
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.squareup.picasso.Picasso
import android.view.View.OnAttachStateChangeListener
import com.alrydb.vderapp.main.data.repo.LocationRepository


class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: WeatherInfoViewModel
    private lateinit var binding : ActivityMainBinding
    private lateinit var adapterList : MutableList<Any>
    private lateinit var searchView: androidx.appcompat.widget.SearchView
    var swipeRefreshLayout: SwipeRefreshLayout? = null

    private var twentyFourHoursSelected : Boolean = true
    private var sevenDaysSelected: Boolean = false


    lateinit var adapter : DailyForecastAdapter



    // Skapa menyn
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)


        // Konfigurera sök https://developer.android.com/guide/topics/search/search-dialog#UsingSearchWidget
        searchView = (menu.findItem(R.id.search).actionView as androidx.appcompat.widget.SearchView)

        // Get the SearchView and set the searchable configuration
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.apply {
            // Assumes current activity is the searchable activity
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            setIconifiedByDefault(false) // Do not iconify the widget; expand it by default
        }



        // Kod som hanterar vad som händer när sökvyn öppnas och stängs
        searchView.addOnAttachStateChangeListener(object : OnAttachStateChangeListener {
            override fun onViewDetachedFromWindow(arg0: View) {
                // Searchview stängs
                binding.cityName.setVisibility(View.VISIBLE)
                binding.countryName.setVisibility(View.VISIBLE)
                binding.imageView.setVisibility(View.VISIBLE)

            }

            override fun onViewAttachedToWindow(arg0: View) {
                // Searchview öppnas
                binding.cityName.setVisibility(View.INVISIBLE)
                binding.countryName.setVisibility(View.INVISIBLE)
                binding.imageView.setVisibility(View.INVISIBLE)

            }
        })


        searchView.setOnQueryTextListener(object: androidx.appcompat.widget.SearchView.OnQueryTextListener{

            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.i("search",query ?: "tom" )
                viewModel.getSearchedLocationDetails(query)

                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                Log.i("search",query ?: "tom" )
                return true
            }


        }
        )

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
        swipeRefreshLayout = binding.refreshLayout


        //skapa viewmodel
        val viewModelFactory = ViewModelFactory(
            dailyForecastRepository = DailyForecastRepository(),
            weatherRepository = WeatherRepository(),
            hourlyForecastRepository = HourlyForecastRepository(),
            locationRepository = LocationRepository()

        )

        viewModel = ViewModelProvider(this, viewModelFactory).get(WeatherInfoViewModel::class.java)


        //val weatherRepository = WeatherRepository()
        //val dailyForecastRepository = DailyForecastRepository()


        // Om appen har tillgång till mobilen plats samt om mobilen är uppkopplad till internet så hämtas och presenteras väderdata
        if (networkEnabled() && locationEnabled()) {
            refreshContent()
        }

        // När användaren refreshar appen

        swipeRefreshLayout!!.setOnRefreshListener() {


            if (networkEnabled() && locationEnabled()) {
                refreshContent()
            } else if (!locationEnabled()) {

                Toast.makeText(this, "Plats är inte aktiverad", Toast.LENGTH_SHORT).show()

            } else if (!networkEnabled()) {

                Toast.makeText(
                    this,
                    "Kunde inte koppla upp till internet, väderdata kan inte hämtas",
                    Toast.LENGTH_SHORT
                ).show()

            }

        }






            binding.forecastTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (tab) {
                        binding.forecastTab.getTabAt(0) -> {
                            showHourlyForecast()
                            Log.i("tab", "tab 1 selected")
                            twentyFourHoursSelected = true


                        }
                        binding.forecastTab.getTabAt(1) -> {
                            showDailyForecast()
                            Log.i("tab", "tab 2 selected")

                            sevenDaysSelected = true

                        }
                    }


                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    when (tab) {
                        binding.forecastTab.getTabAt(0) -> {
                            Log.i("tab", "tab 1 unselected")
                            twentyFourHoursSelected = false


                        }

                        binding.forecastTab.getTabAt(1) -> {
                            Log.i("tab", "tab 2 unselected")

                            sevenDaysSelected = false

                        }

                    }

                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    when (tab) {
                        binding.forecastTab.getTabAt(0) -> {
                            Log.i("tab", "tab 1 reselected")
                            twentyFourHoursSelected = true

                        }

                        binding.forecastTab.getTabAt(1) -> {
                            Log.i("tab", "tab 2 reselected")
                            sevenDaysSelected = true
                        }

                    }

                }


            })




    }



 /*   override fun onBackPressed() {
        if (!searchView.hasFocus()) {
            binding.cityName.setVisibility(View.VISIBLE)
            binding.countryName.setVisibility(View.VISIBLE)
            binding.imageView.setVisibility(View.VISIBLE)

        } else {
            super.onBackPressed();
        }
    }

*/



    private fun refreshContent()
    {



        var tab = binding.forecastTab.selectedTabPosition

        showCurrentWeather()
        Log.i("help",tab.toString())
        if (twentyFourHoursSelected)
        {
            viewModel.refreshLocationData(this@MainActivity)
            viewModel.refreshHourlyForecast()
            showHourlyForecast()



        }
        else if (sevenDaysSelected)
        {
            viewModel.refreshLocationData(this@MainActivity)
            viewModel.refreshDailyForecast()
            showDailyForecast()

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

                            /*showCurrentWeather()
                            showDailyForecast()*/

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

        if(viewModel.finishRefresh)
        {
            swipeRefreshLayout!!.isRefreshing = false


        }
    }



    private fun observeViewModel()
    {
        // Visa 7 dagars-prognos för nuvarande plats
        viewModel.dailyForecastList.observe(this, Observer { dailyForecastResponse ->

            var adapterlist : MutableList<DailyForecast> = mutableListOf()


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
       if(viewModel.finishRefresh)
        {
            swipeRefreshLayout!!.isRefreshing = false

        }

    }

    private fun showHourlyForecast()
    {
        // Visa 7 dagars-prognos för nuvarande plats
        viewModel.hourlyForecastList.observe(this, Observer { hourlyForecastResponse ->
            binding?.forecastRv?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

            var adapterlist : MutableList<HourlyForecast> = mutableListOf()

            for(i in hourlyForecastResponse.hourly)
            {
                adapterlist.add(i)
                // Api-svaret ger data för 48 timamr, vi vill endast visa 24 timmar
                if (i == hourlyForecastResponse.hourly[23])
                {
                    break
                }

            }

            // Skicka data som hämtas från api:n till adaptern
            binding?.forecastRv?.adapter = HourlyForecastAdapter(adapterlist)



        })
        if(viewModel.finishRefresh)
        {
            swipeRefreshLayout!!.isRefreshing = false

        }

    }



}