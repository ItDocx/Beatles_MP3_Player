package com.brainsMedia.beatlesmp3player

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class ActivitySplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_BeatlesMP3PlayerCustom)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

   var image = findViewById<ImageView>(R.id.SplashIcon)

        image.alpha = 0f

        image.animate().setDuration(1000).alpha(1f).withEndAction{
            var intent = Intent(this,MainActivity::class.java)
            startActivity(intent)

            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
            finish()
        }


    }
}