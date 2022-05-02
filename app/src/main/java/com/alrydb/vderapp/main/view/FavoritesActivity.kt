package com.alrydb.vderapp.main.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.alrydb.vderapp.R
import com.alrydb.vderapp.databinding.ActivityFavoritesBinding

class FavoritesActivity : AppCompatActivity() {

    private lateinit var binding : ActivityFavoritesBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityFavoritesBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)


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