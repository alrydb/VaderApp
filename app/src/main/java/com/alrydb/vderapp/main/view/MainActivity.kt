package com.alrydb.vderapp.main.view

import android.app.Activity
import android.app.SearchManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alrydb.vderapp.R
import com.alrydb.vderapp.databinding.ActivityMainBinding
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
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.alrydb.vderapp.main.data.TinyDB
import com.alrydb.vderapp.main.data.repo.LocationRepository


class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: WeatherInfoViewModel
    private lateinit var binding : ActivityMainBinding

    private lateinit var searchView: androidx.appcompat.widget.SearchView
    var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var showHourlyAdapter : Boolean = true

    private lateinit var favorites : SharedPreferences
    private lateinit var tinyDB : TinyDB




    var menuHidden : Boolean = false



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)


        // Konfigurera s??k https://developer.android.com/guide/topics/search/search-dialog#UsingSearchWidget
        searchView = (menu.findItem(R.id.search).actionView as androidx.appcompat.widget.SearchView)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            setIconifiedByDefault(false)
        }

        if (menuHidden) {
            for (i in 0 until menu.size()) menu.getItem(i).isVisible = false
        }





        searchView.addOnAttachStateChangeListener(object : OnAttachStateChangeListener {
            override fun onViewDetachedFromWindow(arg0: View) {
                // Searchview st??ngs
                binding.cityName.setVisibility(View.VISIBLE)
                binding.countryName.setVisibility(View.VISIBLE)
                binding.buttonFavorites.setVisibility(View.VISIBLE)

                binding.forecastGroup.isVisible = true
                binding.currentGroup.isVisible = true


            }

            override fun onViewAttachedToWindow(arg0: View) {
                // Searchview ??ppnas
                binding.cityName.setVisibility(View.INVISIBLE)
                binding.countryName.setVisibility(View.INVISIBLE)
                binding.buttonFavorites.setVisibility(View.INVISIBLE)

                binding.forecastGroup.isVisible = false
                binding.currentGroup.isVisible = false

            }
        })


        // Kod som hanterar vad som h??nder n??r man s??ker p?? en stad/ort
        searchView.setOnQueryTextListener(object: androidx.appcompat.widget.SearchView.OnQueryTextListener{

            override fun onQueryTextSubmit(query: String?): Boolean {

                if (networkEnabled()) {
                    viewModel.getSearchedLocationDetails(query, this@MainActivity)
                }

                (menu.findItem(R.id.search)).collapseActionView()
                binding.forecastTab.selectTab(binding.forecastTab.getTabAt(0))


                showHourlyForecast()
                removeFragment()

                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                return true
            }

        }
        )

        return true
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        //Tvinga appen att k??ras med ljust tema ??ven om m??rkt tema ??r aktiverat
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        super.onCreate(savedInstanceState)


        // F?? referenser till alla ui-komponenter med hj??lp av viewbinding
        binding = ActivityMainBinding.inflate(layoutInflater)


        tinyDB = TinyDB(applicationContext)
        favorites = getSharedPreferences("favorites", Context.MODE_PRIVATE)


        //Rendera UI-element
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarNav)
        binding.toolbarNav.inflateMenu(R.menu.options_menu)

        swipeRefreshLayout = binding.refreshLayout

        // S??tter default vald tab till 24 timmars prognos
        binding.forecastTab.selectTab(binding.forecastTab.getTabAt(0))


        val viewModelFactory = ViewModelFactory(
            dailyForecastRepository = DailyForecastRepository(),
            weatherRepository = WeatherRepository(),
            hourlyForecastRepository = HourlyForecastRepository(),
            locationRepository = LocationRepository()
        )
        viewModel = ViewModelProvider(this, viewModelFactory).get(WeatherInfoViewModel::class.java)


        binding.bottomNav.setOnItemSelectedListener{

            when (it.title){

                "Prognos" -> {}

                "Favoriter" -> {val intent = Intent(this, FavoritesActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)}

                "Inst??llningar" -> {val intent = Intent(this, SettingsActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                }

            }

            true

        }



        if (networkEnabled() && locationEnabled()) {
            refreshContent()
        }

        // Om appen har tillg??ng till internet men inte plats s?? visas en v??derprognos ??ver ??rebro som standard
        if (networkEnabled() && !locationEnabled()){

            viewModel.refreshCurrentWeather()
            viewModel.refreshHourlyForecast()
            viewModel.refreshDailyForecast()
            showCurrentWeather()
            showHourlyForecast()

        }


        // N??r anv??ndaren refreshar appen
        swipeRefreshLayout!!.setOnRefreshListener() {

            removeFragment()

            if (networkEnabled() && locationEnabled()) {

                refreshContent()

            } else if (!locationEnabled()) {

                Toast.makeText(this, "Plats ??r inte aktiverad, kan inte h??mta v??derdata f??r aktuell plats", Toast.LENGTH_SHORT).show()
                swipeRefreshLayout!!.isRefreshing = false


            } else if (!networkEnabled()) {

                Toast.makeText(
                    this,
                    "Kunde inte koppla upp till internet, v??derdata kan inte h??mtas",
                    Toast.LENGTH_SHORT
                ).show()

            }

        }



            // Kod som hanterar vad som ska visas under vilken tabb
            binding.forecastTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (tab) {
                        binding.forecastTab.getTabAt(0) -> {
                            showHourlyAdapter = true
                            showHourlyForecast()

                            Log.i("tab", "tab 1 selected")

                        }
                        binding.forecastTab.getTabAt(1) -> {
                            showHourlyAdapter = false
                            showDailyForecast()

                            Log.i("tab", "tab 2 selected")


                        }
                    }


                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    when (tab) {
                        binding.forecastTab.getTabAt(0) -> {
                            Log.i("tab", "tab 1 unselected")

                        }

                        binding.forecastTab.getTabAt(1) -> {
                            Log.i("tab", "tab 2 unselected")

                        }

                    }

                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    when (tab) {
                        binding.forecastTab.getTabAt(0) -> {
                            Log.i("tab", "tab 1 reselected")

                        }

                        binding.forecastTab.getTabAt(1) -> {
                            Log.i("tab", "tab 2 reselected")

                        }

                    }

                }


            })




        binding.buttonFavorites.setOnClickListener(){

            if (binding.cityName.text.isNotEmpty()) {

                // H??mta favoriter
                var favorites = tinyDB.getListString("favorites")


                if (!favorites.contains(binding.cityName.text))
                {
                    val favorite = binding.cityName.text

                    // L??gg till ny favorit
                    // Tar bort 'kommun' fr??n vissa resultat
                    favorites.add(favorite.toString().substringBefore("Municipality"))

                    // Uppdatera och spara favoriter
                    tinyDB.putListString("favorites", favorites)

                    binding.buttonFavorites.setImageResource(R.drawable.ic_favorite)
                    Toast.makeText(this, "Sparad som favorit!", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    Toast.makeText(this, "Denna plats ??r redan sparad som favorit", Toast.LENGTH_SHORT).show()
                }

            }


        }

        // Denna kod k??rs n??r anv??ndaren har klickat p?? en favorit fr??n listan med favoriter
        if (intent.extras?.get("goToFavorite").toString().toBoolean())
        {
            val location = intent.extras?.get("selectedFavorite").toString()

            viewModel.getSearchedLocationDetails(location, this@MainActivity)

        }







    }



    override fun onBackPressed() {

        binding.currentGroup.isInvisible = false
        binding.menuGroup.isInvisible = false
        binding.forecastGroup.isInvisible = false
        binding.toolbarNav.isVisible = true

        super.onBackPressed()
    }


    private fun checkIfFavorited(result : String)
    {

        if(tinyDB.getListString("favorites").contains(result.substringBefore("Municipality")))
        {
            binding.buttonFavorites.setImageResource(R.drawable.ic_favorite)
        }
        else
        {
            binding.buttonFavorites.setImageResource(R.drawable.ic_favorite_border)
        }
    }


   private fun removeFragment()
    {
        binding.currentGroup.isInvisible = false

        val fragmentManager = supportFragmentManager
        fragmentManager.beginTransaction().apply {
            fragmentManager.findFragmentById(R.id.fragment_hourly)?.let { remove(it).commitNow() }
            fragmentManager.findFragmentById(R.id.fragment_daily)?.let { remove(it).commitNow() }
        }
    }




    private fun refreshContent()
    {

        var tab = binding.forecastTab.selectedTabPosition

        viewModel.refreshLocationData(this@MainActivity)
        showCurrentWeather()

        // Om 24 timmars prognos ??r markerad
        if (tab == 0)
        {
            viewModel.refreshHourlyForecast()
            showHourlyForecast()

        }
        // Om 7 dagars prognos ??r markerad
        else if (tab == 1)
        {

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
                    "Kunde inte koppla upp till internet, v??derdata kan inte h??mtas",
                    Toast.LENGTH_SHORT
                ).show()
            }, 1000)

        }
        return networkEnabled

    }


    private fun locationEnabled() : Boolean
    {
        var locationEnabled = false

        if(!viewModel.isLocationEnabled(this)){

            Toast.makeText(this, "Platstj??nsten ??r inte aktiverad, aktivera platstj??nsten f??r att h??mta aktuell v??derdata", Toast.LENGTH_SHORT).show()

            //Om platstj??nsten inte ??r aktiverad s?? omdirigeras anv??ndaren
            // Till mobilens inst??llningar f??r platstj??nst
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)

        }else{
            Dexter.withContext(this).withPermissions(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
                .withListener(object : MultiplePermissionsListener{
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?){
                        // Om beh??righeter till??ts av anv??ndaren
                        if (report!!.areAllPermissionsGranted()){

                            viewModel.requestLocationData(this@MainActivity)
                            locationEnabled = true

                        }
                        // Om beh??righeter nekas av anv??ndaren
                        if (report.isAnyPermissionPermanentlyDenied){

                            Toast.makeText(this@MainActivity, "Beh??righet till mobilens platstj??nst har nekats", Toast.LENGTH_SHORT).show()

                        }
                    }

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
            .setMessage("Beh??righet till platstj??nsten ??r inte aktiverad")
            .setPositiveButton("INST??LLNINGAR"){
                _,_ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)

                    intent.data = uri

                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)

                    moveTaskToBack(true)
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1)


                }
                catch(e: ActivityNotFoundException){
                    e.printStackTrace()
                }
            }
            .setNegativeButton("AVBRYT"){
                dialog, _ ->
                dialog.dismiss()

                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)


            }.show()
    }



    private fun showCurrentWeather()
    {

        viewModel.currentWeatherList.removeObservers(this)
        viewModel.currentWeatherList.observe(this, Observer { weatherResponse ->
            Log.i("response", weatherResponse.id.toString())
            Log.i("response", weatherResponse.visibility.toString())
            Log.i("response", weatherResponse.weather[0].icon)


            checkIfFavorited(weatherResponse.name)

            binding.cityName.text = weatherResponse.name

            binding.countryName.text = weatherResponse.sys.country
            binding.currentWind.text = weatherResponse.wind.speed.toInt().toString() + " m/s"
            binding.currentTemp.text = weatherResponse.main.temp.toString().substringBefore(".") + "??C"
            binding.currentDescription.text = weatherResponse.weather[0].description.replaceFirstChar {
                weatherResponse.weather[0].description[0].uppercase()
            }

            val uri = "https://openweathermap.org/img/w/" + weatherResponse.weather[0].icon + ".png"
            Picasso.get().load(uri).into(binding.iconWeather)


        })

        if(viewModel.finishRefresh)
        {
            swipeRefreshLayout!!.isRefreshing = false


        }
    }






    private fun showDailyForecast()
    {
        binding.forecastRv.adapter = null

        viewModel.dailyForecastList.observe(this, Observer { dailyForecastResponse ->

            binding?.forecastRv?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            val timeZone = dailyForecastResponse.timezone

                // Skicka data som h??mtas fr??n api:n till adaptern
            if(!showHourlyAdapter)
            {
                binding?.forecastRv?.adapter = DailyForecastAdapter(dailyForecastResponse, this, timeZone)
            }

        })

       if(viewModel.finishRefresh)
        {
            swipeRefreshLayout!!.isRefreshing = false

        }

        Log.i("SHOW", "SHOW DAILY CALLED")
        Log.i("SHOW", binding.forecastRv.adapter.toString())


    }





    private fun showHourlyForecast()
    {

            viewModel.hourlyForecastList.observe(this, Observer { hourlyForecastResponse ->
            binding?.forecastRv?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

            var adapterlist : MutableList<HourlyForecast> = mutableListOf()
            val timeZone = hourlyForecastResponse.timezone

            for(i in hourlyForecastResponse.hourly)
            {
                adapterlist.add(i)
                // Api-svaret ger data f??r 48 timmar, vi vill endast visa 24 timmar
                if (i == hourlyForecastResponse.hourly[23])
                {
                    break
                }

            }

            // Skicka data som h??mtas fr??n api:n till adaptern
            if(showHourlyAdapter)
            {
                binding?.forecastRv?.adapter = HourlyForecastAdapter(adapterlist, this@MainActivity, timeZone )
            }

        })

        if(viewModel.finishRefresh)
        {
            swipeRefreshLayout!!.isRefreshing = false

        }

        Log.i("SHOW", "SHOW HOURLY CALLED")
        Log.i("SHOW", binding.forecastRv.adapter.toString())

    }





}