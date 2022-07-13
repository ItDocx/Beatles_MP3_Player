package com.example.beatlesmp3player

import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView

class FavouriteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourite)

        val back = findViewById<ImageView>(R.id.menu_btn)
        val search = findViewById<ImageView>(R.id.search_btn)
        val fav_title = findViewById<TextView>(R.id.title_TV)

        back.setImageResource(R.drawable.ic_prev)
        search.setVisibility(View.GONE)
        fav_title.setText(R.string.Fav_title)


    }
}