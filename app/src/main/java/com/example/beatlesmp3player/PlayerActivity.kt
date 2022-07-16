package com.example.beatlesmp3player

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.media.Image
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.beatlesmp3player.Models.SongsLIst
import com.example.beatlesmp3player.Services.SongsServices
import com.example.beatlesmp3player.databinding.ActivityPlayerBinding
import java.lang.Exception
import java.util.Collections.addAll

class PlayerActivity : AppCompatActivity(), ServiceConnection {

    private lateinit var binding: ActivityPlayerBinding

   companion object {
        lateinit var songsListPA: ArrayList<SongsLIst>
        var songPosition: Int=0
       var isPlaying: Boolean = false
       var songsServices: SongsServices?= null

   }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_player)
        //Applying Binding
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // For Starting Music Service
        val intent = Intent(this,SongsServices::class.java)
        bindService(intent,this, BIND_AUTO_CREATE)
        startService(intent)

        // Setting Toolbar Icon & Title Function
        settingToolBar()
        //Initializing Layout Function
        initializeLayout()
        // Play Button Click Listener
        binding.playBtn.setOnClickListener{

            if (isPlaying == false){
                playSongs()

            }
            else {

                pauseSongs()}
        }

        // Prev & Next Button Click Listener
        binding.prevBtn.setOnClickListener{
            prev_nextbtn(increment = false)
        }

        binding.nextBtn.setOnClickListener{
            prev_nextbtn(increment = true)
        }


    }
    // Function Setting Toolbar Icons & Functionality
    private fun settingToolBar(){

        var back_btn_toolbar = findViewById<ImageView>(R.id.menu_btn)
        var fav_btn_toolbar = findViewById<ImageView>(R.id.search_btn)

        back_btn_toolbar.setImageResource(R.drawable.ic_prev)
        fav_btn_toolbar.setImageResource(R.drawable.ic_favorite_empty)

        back_btn_toolbar.setOnClickListener{

            startActivity(Intent(this,MainActivity::class.java))
            finish() }

        fav_btn_toolbar.setOnClickListener{
            fav_btn_toolbar.setImageResource(R.drawable.ic_favorite)
            Toast.makeText(this,"Added to Favourites",Toast.LENGTH_SHORT).show()
        } }


    // setLayout Player Activity
    private fun setLayout(){
        Glide.with(this).load(songsListPA[songPosition].artUri).
        apply(RequestOptions().placeholder(R.drawable.splash2).centerCrop()).into(binding.posterPlayer)

        binding.songTitle.text = songsListPA[songPosition].title

    }


// Initializing MediaPlayer
    private fun initializeMediaPlayer() {
    try {
        if (songsServices!!.mediaPlayer==null) songsServices!!.mediaPlayer = MediaPlayer()
        songsServices!!.mediaPlayer!!.reset()
        songsServices!!.mediaPlayer!!.setDataSource(songsListPA[songPosition].path)
        songsServices!!.mediaPlayer!!.prepare()
        songsServices!!.mediaPlayer!!.start()
        isPlaying = true


        // Setting Image Icon
        binding.playBtn.setIconResource(R.drawable.ic_pause)

    }catch (e:Exception){
        return
    } }

    //Initialize Music Player Layout

    private fun initializeLayout(){

        songPosition = intent.getIntExtra("index",0)
        when(intent.getStringExtra("class"))
        {
            "MusicAdapter"->{

                songsListPA = ArrayList()
                songsListPA.addAll(MainActivity.MusicList)

                // Adding Functions
                setLayout()

            }
            "MainActivity"->{
                songsListPA = ArrayList()
                songsListPA.addAll(MainActivity.MusicList)
                songsListPA.shuffle()

                setLayout()


            }
        }

    }

    // Play & Pause Functionality

    private fun playSongs(){
        binding.playBtn.setIconResource(R.drawable.ic_pause)
        isPlaying = true
        songsServices!!.mediaPlayer!!.start()
    }

    private fun pauseSongs(){

        binding.playBtn.setIconResource(R.drawable.ic_play_arrow)
        isPlaying = false
        songsServices!!.mediaPlayer!!.pause()
    }

    // Applying Next & Previous Functionality

    private fun prev_nextbtn(increment: Boolean){

        if(increment){

            setSongsPosition(increment = true)
            setLayout()
            initializeMediaPlayer()
        }
        else
        {
            setSongsPosition(increment = false)
            setLayout()
            initializeMediaPlayer()
        }

    }

    //Set Songs Position
    private fun setSongsPosition(increment: Boolean){

        if (increment){
            if (songsListPA.size -1 == songPosition)
                songPosition =0
            else ++songPosition
        }
        else{
            if (0== songPosition)
                songPosition = songsListPA.size -1
            else --songPosition

        }


    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as SongsServices.MyBinder
        songsServices = binder.currentSongService()
        initializeMediaPlayer()
        songsServices!!.showNotification()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        songsServices = null
    }


}