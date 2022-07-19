package com.example.beatlesmp3player

import android.annotation.SuppressLint
import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beatlesmp3player.Adapter.FavoriteAdapter
import com.example.beatlesmp3player.Adapter.SongsAdapter
import com.example.beatlesmp3player.Models.SongsLIst
import com.example.beatlesmp3player.databinding.ActivityFavouriteBinding

class FavouriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavouriteBinding
    private lateinit var favAdapter: FavoriteAdapter

    companion object{

        var favList: ArrayList<SongsLIst> = ArrayList()

    }

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_BeatlesMP3PlayerCustom)
        super.onCreate(savedInstanceState)
        binding = ActivityFavouriteBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val back = findViewById<ImageView>(R.id.menu_btn)
        val search = findViewById<ImageView>(R.id.search_btn)
        val fav_title = findViewById<TextView>(R.id.title_TV)

        back.setImageResource(R.drawable.ic_prev)
        search.setVisibility(View.GONE)
        fav_title.setText(R.string.Fav_title)

        back.setOnClickListener{
            finish()
        }

        binding.favShuffleBtn.setOnClickListener {
                val intent = Intent(this, PlayerActivity::class.java)
                intent.putExtra("index", 0)
                intent.putExtra("class", "FavouriteShuffle")
                startActivity(intent)
        }
        binding.favRecycler.layoutManager = GridLayoutManager(this,3)
        binding.favRecycler.setHasFixedSize(true)
        binding.favRecycler.setItemViewCacheSize(13)
        // This will pass the ArrayList to our Adapter
        favAdapter = FavoriteAdapter(favList, this)
        // Setting the Adapter with the recyclerview
        binding.favRecycler.adapter = favAdapter
        // Initializing Total Songs


    }
}