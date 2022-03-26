package com.alrydb.vderapp.main.view

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.alrydb.vderapp.R
import com.alrydb.vderapp.databinding.ActivityMainBinding
import com.alrydb.vderapp.main.viewmodel.WeatherInfoViewModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: WeatherInfoViewModel
    private lateinit var binding : ActivityMainBinding


    // Skapa menyn
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)

        return true
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        //Rendera UI-element
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarNav)
        binding.toolbarNav.inflateMenu(R.menu.options_menu)



        //skapa viewmodel
        viewModel = WeatherInfoViewModel()



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




}