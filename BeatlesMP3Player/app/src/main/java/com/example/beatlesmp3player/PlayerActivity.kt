package com.example.beatlesmp3player

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView

class PlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        var title_toolbar = findViewById<TextView>(R.id.title_TV)
        var back_btn_toolbar = findViewById<ImageView>(R.id.menu_btn)
        var fav_btn_toolbar = findViewById<ImageView>(R.id.search_btn)

        back_btn_toolbar.setImageResource(R.drawable.ic_prev)
        fav_btn_toolbar.setImageResource(R.drawable.ic_favorite_empty)

        back_btn_toolbar.setOnClickListener{

            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }


    }
}