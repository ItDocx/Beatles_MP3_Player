package com.example.beatlesmp3player

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.beatlesmp3player.databinding.ActivityPlayListBinding

class PlayListActivity : AppCompatActivity() {

 private lateinit var binding: ActivityPlayListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val back = findViewById<ImageView>(R.id.menu_btn)
        val search = findViewById<ImageView>(R.id.search_btn)
        val fav_title = findViewById<TextView>(R.id.title_TV)

        back.setImageResource(R.drawable.ic_prev)
        search.setVisibility(View.GONE)
        fav_title.setText(R.string.Playlist_title)

        back.setOnClickListener{
            finish()
        }




    }
}