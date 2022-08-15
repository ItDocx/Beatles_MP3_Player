package com.brainsMedia.beatlesmp3player

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.brainsMedia.beatlesmp3player.Models.*
import com.brainsMedia.beatlesmp3player.Services.SongsServices
import com.brainsMedia.beatlesmp3player.databinding.ActivityPlayerBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.max

class PlayerActivity : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener {



   companion object {
        lateinit var songsListPA: ArrayList<SongsLIst>
        var songPosition: Int=0
       var isPlaying: Boolean = false
       var songsServices: SongsServices?= null

       @SuppressLint("StaticFieldLeak")
       lateinit var binding: ActivityPlayerBinding
       var repeat: Boolean = false
       var min_15: Boolean = false
       var min_30: Boolean = false
       var min_45: Boolean = false
       var nowPlayingId : String = ""
       var isfav : Boolean = false
       var fIndex: Int = -1

   }
   lateinit var back_btn_toolbar :ImageView
    lateinit var fav_btn_toolbar :ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_BeatlesMP3PlayerCustom)
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_player)
        //Applying Binding
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        back_btn_toolbar = findViewById(R.id.menu_btn)
        fav_btn_toolbar = findViewById(R.id.search_btn)

        back_btn_toolbar.setImageResource(R.drawable.ic_prev)
        fav_btn_toolbar.setImageResource(R.drawable.ic_favorite_empty)


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

        // SeekBar Listener

        binding.progressDuration.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                if (fromUser) songsServices!!.mediaPlayer!!.seekTo(progress)

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })

        //Repeat Button Listener

        binding.playerRepeatBtn.setOnClickListener{

            if (!repeat){
                repeat = true
                binding.playerRepeatBtn.setImageResource(R.drawable.ic_repeat_one)
            }
            else
            {
                repeat = false
                binding.playerRepeatBtn.setImageResource(R.drawable.ic_repeat_all)

            }

        }

        //Equalizer Button Listener
        binding.playerEqualizerBtn.setOnClickListener{


                try {
                    val EQIntent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                    EQIntent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, songsServices!!.mediaPlayer!!.audioSessionId)
                    EQIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME,baseContext.packageName)
                    EQIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
                    startActivityForResult(EQIntent,13)

                }catch (e:Exception){

                    Toast.makeText(this,"Equalizer Feature not Supported!!",Toast.LENGTH_LONG).show()
                } }

        // Timer Bottom Sheet Dialogue

        binding.playerStopwatchBtn.setOnClickListener{
            val timer = min_15 || min_30 || min_45
            if (!timer) showBottomSheetDialogue()

            else {

                val builder = MaterialAlertDialogBuilder(this)
                builder.setTitle(" Stop Timer ")
                    .setMessage(" Do you want to Stop Timer? ")
                    .setPositiveButton("Yes"){_, _ ->

                        min_15 = false
                        min_30 = false
                        min_45 = false
                    }.setNegativeButton("No"){dialog,_ ->
                        dialog.dismiss()
                    }

                val customDialoge = builder.create()
                customDialoge.show()
                customDialoge.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
                customDialoge.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)



            }
        }

        // Share Button Listener

        binding.playerShareBtn.setOnClickListener{

            Toast.makeText(this,"Please select preffered sharing option",Toast.LENGTH_SHORT).show()
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "audio/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(songsListPA[songPosition].path))
            startActivity(Intent.createChooser(shareIntent,"Sharing File....!"))
        }


        back_btn_toolbar.setOnClickListener{ finish() }

        fav_btn_toolbar.setOnClickListener{
            if (isfav){
                isfav = false
                fav_btn_toolbar.setImageResource(R.drawable.ic_favorite_empty)
                FavouriteActivity.favList.removeAt(fIndex)

            }
            else {

                isfav = true
                fav_btn_toolbar.setImageResource(R.drawable.ic_favorite)
                FavouriteActivity.favList.add(songsListPA[songPosition])
            }

            }


    }

    // setLayout Player Activity
    fun setLayout(){

        fIndex = favChecker(songsListPA[songPosition].id)
        Glide.with(this).load(songsListPA[songPosition].artUri).
        apply(RequestOptions().placeholder(R.drawable.splash2).centerCrop()).into(binding.posterPlayer)

        binding.songTitle.text = songsListPA[songPosition].title
        binding.songTitle.isSelected = true
        if (repeat) binding.playerRepeatBtn.setColorFilter(ContextCompat.getColor(this,R.color.darkorange))
        if (min_15|| min_30|| min_45) binding.playerStopwatchBtn.setColorFilter(R.color.darkorange)
        if (isfav) fav_btn_toolbar.setImageResource(R.drawable.ic_favorite)
        else fav_btn_toolbar.setImageResource(R.drawable.ic_favorite_empty)

    }




    //Initialize Music Player Layout

    private fun initializeLayout(){

        songPosition = intent.getIntExtra("index",0)
        when(intent.getStringExtra("class"))
        {
            "FavouriteShuffle" -> {
                val intent = Intent(this,SongsServices::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                songsListPA = ArrayList()
                songsListPA.addAll(FavouriteActivity.favList)
                songsListPA.shuffle()

                setLayout()
            }

            "FavouriteAdapter" ->{
                val intent = Intent(this,SongsServices::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                songsListPA = ArrayList()
                songsListPA.addAll(FavouriteActivity.favList)
                setLayout()

            }


            "NowPlaying" ->{
                setLayout()
                binding.durationTxt1.text = formatDuration(songsServices!!.mediaPlayer!!.currentPosition.toLong())
                binding.durationTxt2.text = formatDuration(songsServices!!.mediaPlayer!!.duration.toLong())
                binding.progressDuration.progress = songsServices!!.mediaPlayer!!.currentPosition
                binding.progressDuration.max = songsServices!!.mediaPlayer!!.duration

                if (isPlaying) binding.playBtn.setIconResource(R.drawable.ic_pause)
                else binding.playBtn.setIconResource(R.drawable.ic_play_arrow)


            }

            "MusicAdapterSearch"->{
                val intent = Intent(this,SongsServices::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                songsListPA = ArrayList()
                songsListPA.addAll(MainActivity.searchMusicList)

                // Adding Functions
                setLayout()
            }

            "MusicAdapter"->{
                val intent = Intent(this,SongsServices::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                songsListPA = ArrayList()
                songsListPA.addAll(MainActivity.MusicList)

                // Adding Functions
                setLayout()

            }
            "MainActivity"->{
                val intent = Intent(this,SongsServices::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                songsListPA = ArrayList()
                songsListPA.addAll(MainActivity.MusicList)
                songsListPA.shuffle()

                setLayout()


            }

            "PlaylistDetailsAdapter"->{
                val intent = Intent(this,SongsServices::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                songsListPA = ArrayList()
                songsListPA.addAll(PlayListActivity.songsPlaylist.ref[PlaylistDetails.currentPlaylistPos].playList)
                setLayout()
            }

            "detailsShuffle"->{

                val intent = Intent(this,SongsServices::class.java)
                bindService(intent,this, BIND_AUTO_CREATE)
                startService(intent)
                songsListPA = ArrayList()
                songsListPA.addAll(PlayListActivity.songsPlaylist.ref[PlaylistDetails.currentPlaylistPos].playList)
                songsListPA.shuffle()

                setLayout()

            }

        }

    }


    // Initializing MediaPlayer
    private fun initializeMediaPlayer() {
        try {
            if (songsServices!!.mediaPlayer==null) songsServices!!.mediaPlayer = MediaPlayer()
            songsServices!!.mediaPlayer!!.reset()
            songsServices!!.mediaPlayer!!.setDataSource(PlayerActivity.songsListPA[PlayerActivity.songPosition].path)
            songsServices!!.mediaPlayer!!.prepare()
            songsServices!!.mediaPlayer!!.start()
            isPlaying = true


            // Setting Image Icon
            binding.playBtn.setIconResource(R.drawable.ic_pause)

            songsServices!!.showNotification(R.drawable.ic_pause)

            // Duration TV
            binding.durationTxt1.text = formatDuration(songsServices!!.mediaPlayer!!.currentPosition.toLong())
            binding.durationTxt2.text = formatDuration(songsServices!!.mediaPlayer!!.duration.toLong())

            // Adding duration in SeekBar
            binding.progressDuration.progress = 0
            binding.progressDuration.max = songsServices!!.mediaPlayer!!.duration

            songsServices!!.mediaPlayer!!.setOnCompletionListener(this)
            //Setting ID
            nowPlayingId = songsListPA[songPosition].id


        }catch (e: Exception){
            return
        } }

    // Play & Pause Functionality

    private fun playSongs(){
        binding.playBtn.setIconResource(R.drawable.ic_pause)
        isPlaying = true
        songsServices!!.showNotification(R.drawable.ic_pause)
        songsServices!!.mediaPlayer!!.start()


    }

    private fun pauseSongs(){

        binding.playBtn.setIconResource(R.drawable.ic_play_arrow)
        songsServices!!.showNotification(R.drawable.ic_play_arrow)
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

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as SongsServices.MyBinder
        songsServices = binder.currentSongService()
        initializeMediaPlayer()
        songsServices!!.seekBarSetup()


    }

    override fun onServiceDisconnected(name: ComponentName?) {
        songsServices = null
    }

    override fun onCompletion(mp: MediaPlayer?) {
        setSongsPosition(increment = true)
        initializeMediaPlayer()
        try {
            setLayout()
        }catch (e:Exception){return} }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode ==13 || resultCode == RESULT_OK)
            return
    }

    private fun showBottomSheetDialogue(){

        val dialogue = BottomSheetDialog(this@PlayerActivity)
        dialogue.setContentView(R.layout.bottom_sheet_dialogue)
        dialogue.show()

        // Adding 15 Minutes Toast on The minutes
        dialogue.findViewById<LinearLayout>(R.id.layout_min15)?.setOnClickListener{
            Toast.makeText(this,"Music Will stop after 15 minutes",Toast.LENGTH_SHORT).show()
            binding.playerStopwatchBtn.setColorFilter(ContextCompat.getColor(this,R.color.gray))
            min_15 = true
            Thread{Thread.sleep((15 * 60000).toLong())
            if (min_15) exitApplication()}.start()

            dialogue.dismiss()
        }

        // Adding 30 Minutes Toast on The minutes
        dialogue.findViewById<LinearLayout>(R.id.layout_min30)?.setOnClickListener{
            Toast.makeText(this,"Music Will stop after 30 minutes",Toast.LENGTH_SHORT).show()
            binding.playerStopwatchBtn.setColorFilter(ContextCompat.getColor(this,R.color.gray))
            min_30 = true
            Thread{Thread.sleep((30 * 60000).toLong())
                if (min_30) exitApplication()}.start()
            dialogue.dismiss()
        }

        // Adding 45 Minutes Toast on The minutes
        dialogue.findViewById<LinearLayout>(R.id.layout_min45)?.setOnClickListener{
            Toast.makeText(this,"Music Will stop after 45 minutes",Toast.LENGTH_SHORT).show()
            binding.playerStopwatchBtn.setColorFilter(ContextCompat.getColor(this,R.color.gray))
            min_45 = true
            Thread{Thread.sleep((45 * 60000).toLong())
                if (min_45) exitApplication()}.start()
            dialogue.dismiss()
        }





    }


}