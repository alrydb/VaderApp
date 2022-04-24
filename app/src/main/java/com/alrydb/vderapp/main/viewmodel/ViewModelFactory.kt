package com.alrydb.vderapp.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alrydb.vderapp.main.data.repo.DailyForecastRepository
import com.alrydb.vderapp.main.data.repo.HourlyForecastRepository
import com.alrydb.vderapp.main.data.repo.LocationRepository
import com.alrydb.vderapp.main.data.repo.WeatherRepository

class ViewModelFactory (private val weatherRepository: WeatherRepository, private val dailyForecastRepository: DailyForecastRepository, private val hourlyForecastRepository: HourlyForecastRepository, private val locationRepository: LocationRepository )
    : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WeatherInfoViewModel(weatherRepository, dailyForecastRepository, hourlyForecastRepository, locationRepository) as T
    }
}