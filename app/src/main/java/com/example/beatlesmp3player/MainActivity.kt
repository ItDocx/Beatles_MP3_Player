package com.example.beatlesmp3player

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beatlesmp3player.Adapter.SongsAdapter
import com.example.beatlesmp3player.Models.SongsLIst

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var shuffle = findViewById<Button>(R.id.Shuffle_btn)
        var fav = findViewById<Button>(R.id.Favourite_btn)

        shuffle.setOnClickListener {

            val intent = Intent(this, PlayerActivity::class.java)
            startActivity(intent)
        }

        fav.setOnClickListener{

            val intent = Intent(this,FavouriteActivity::class.java)
            startActivity(intent)
        }



        // getting the recyclerview by its id
        val recyclerview = findViewById<RecyclerView>(R.id.recycler_songs)

        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(this)

        // ArrayList of class ItemsViewModel
        val data = ArrayList<SongsLIst>()

        // This loop will create 20 Views containing
        // the image with the count of view
        for (i in 1..20) {
            data.add(
                SongsLIst(R.drawable.splash2, "Diljit Dosanjh: CLASH (Official) Music Video | GOAT - YouTube")
            )
        }

        // This will pass the ArrayList to our Adapter
        val adapter = SongsAdapter(data)

        // Setting the Adapter with the recyclerview
        recyclerview.adapter = adapter






    }
}