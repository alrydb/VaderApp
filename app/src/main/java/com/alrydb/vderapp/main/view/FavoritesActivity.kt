package com.alrydb.vderapp.main.view

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.alrydb.vderapp.R
import com.alrydb.vderapp.databinding.ActivityFavoritesBinding
import com.alrydb.vderapp.main.data.TinyDB

class FavoritesActivity : AppCompatActivity() {

    private lateinit var binding : ActivityFavoritesBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityFavoritesBinding.inflate(layoutInflater)


        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        var tinyDB = TinyDB(applicationContext)

        var adapterList = tinyDB.getListString("favorites")

        binding.favoritesRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.favoritesRv.adapter = FavoritesAdapter(adapterList, this)


        // Bottom navigation
        binding.bottomNav.setOnItemSelectedListener{

            Log.i("bottomnav" , it.itemId.toString())

            when (it.title){

                "Prognos" -> {val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)}

                "Favoriter" -> {
                    Log.i("bottomnav" , "favoriter")}

                "Inställningar" -> {val intent = Intent(this, SettingsActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                }

            }

            true

        }
    }
}