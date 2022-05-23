package com.alrydb.vderapp.main.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.alrydb.vderapp.R
import com.alrydb.vderapp.databinding.FavoritesItemBinding
import com.alrydb.vderapp.databinding.HourlyForecastItemBinding
import com.alrydb.vderapp.main.data.models.forecast.HourlyForecast
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class FavoritesAdapter(val favorites : ArrayList<String>, val context: Context) : RecyclerView.Adapter<FavoritesAdapter.MainViewHolder>() {

   private lateinit var activityResultLauncher : ActivityResultLauncher<Intent>

    inner class MainViewHolder(val itemBinding: FavoritesItemBinding)
        : RecyclerView.ViewHolder(itemBinding.root){


        fun bindItem(favorite: String)
        {

            itemBinding.tvFavorite.text = favorite


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(FavoritesItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val favorite = favorites[position]

        holder.bindItem(favorite)

        // Varannan rad får ljusgrå bakgrund istället för vit
        if (position % 2 == 0) {
            holder.itemBinding.root.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.colorLightGray
                )
            )
        } else {
            holder.itemBinding.root.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
        }




        holder.itemView.setOnClickListener(){

            val extras = Bundle()
            extras.putString("selectedFavorite", favorite)
            extras.putBoolean("goToFavorite", true)
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtras(extras)

            context.startActivity(intent)


            }



    }

    override fun getItemCount(): Int {
        return favorites.size
    }



}