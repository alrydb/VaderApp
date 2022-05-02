package com.alrydb.vderapp.main.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.alrydb.vderapp.R
import com.alrydb.vderapp.databinding.ActivityMainBinding
import com.alrydb.vderapp.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {


    private lateinit var binding : ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivitySettingsBinding.inflate(layoutInflater)
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
                    val intent = Intent(this, FavoritesActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)}

                "Inställningar" -> {
                }

            }

            true

        }
    }
}