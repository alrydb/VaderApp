package com.alrydb.vderapp.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.alrydb.vderapp.main.data.repo.WeatherRepository
import com.alrydb.vderapp.main.viewmodel.WeatherInfoViewModel

class ViewModelFactory (private val repository: WeatherRepository)
    : ViewModelProvider.Factory {


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WeatherInfoViewModel(repository) as T
    }
}