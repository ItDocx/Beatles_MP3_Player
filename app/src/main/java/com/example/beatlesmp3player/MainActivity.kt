package com.example.beatlesmp3player

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var testbtn = findViewById<Button>(R.id.Shuffle_btn)

        testbtn.setOnClickListener {

            val intent = Intent(this, PlayerActivity::class.java)
            startActivity(intent)
        }
    }
}