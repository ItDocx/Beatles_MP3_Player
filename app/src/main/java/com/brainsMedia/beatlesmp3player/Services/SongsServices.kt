package com.brainsMedia.beatlesmp3player.Services

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.brainsMedia.beatlesmp3player.MainActivity
import com.brainsMedia.beatlesmp3player.Models.formatDuration
import com.brainsMedia.beatlesmp3player.Models.getIMaginNotification
import com.brainsMedia.beatlesmp3player.PlayerActivity
import com.brainsMedia.beatlesmp3player.R
import com.brainsMedia.beatlesmp3player.Receivers.NotificationReceivers
import java.lang.Exception

class SongsServices: Service() {

    lateinit var runnable: Runnable
    private var myBinder = MyBinder()
            var mediaPlayer: MediaPlayer? = null
    private lateinit var mediaSession: MediaSessionCompat
    override fun onBind(intent: Intent?): IBinder? {

        mediaSession = MediaSessionCompat(baseContext,"My Music")
        return myBinder
    }

    inner class MyBinder: Binder(){
        fun currentSongService(): SongsServices {

            return this@SongsServices
        }
    }
    @SuppressLint("UnspecifiedImmutableFlag")
    fun showNotification(playPausebtn: Int){

        val intent = Intent(baseContext,MainActivity::class.java)
        val contentIntent = PendingIntent.
        getActivity(this,0,intent,0,)

        val prevIntent = Intent(baseContext,NotificationReceivers::class.java).setAction(SongsApplication.PREVIOUS)
        val prevPendingIntent = PendingIntent.getBroadcast(baseContext,0,prevIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        val nextIntent = Intent(baseContext,NotificationReceivers::class.java).setAction(SongsApplication.NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(baseContext,0,nextIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        val playIntent = Intent(baseContext,NotificationReceivers::class.java).setAction(SongsApplication.PLAY)
        val playPendingIntent = PendingIntent.getBroadcast(baseContext,0,playIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        val exitIntent = Intent(baseContext,NotificationReceivers::class.java).setAction(SongsApplication.EXIT)
        val exitPendingIntent = PendingIntent.getBroadcast(baseContext,0,exitIntent,PendingIntent.FLAG_UPDATE_CURRENT)

        val imageArt = getIMaginNotification(PlayerActivity.songsListPA[PlayerActivity.songPosition].path)
        val image = if (imageArt != null){
            BitmapFactory.decodeByteArray(imageArt,0,imageArt.size)
        }else{
            BitmapFactory.decodeResource(resources,R.drawable.potser)
        }



        val notification = NotificationCompat.Builder(baseContext,SongsApplication.channel_ID)
            .setContentIntent(contentIntent)
            .setContentTitle(PlayerActivity.songsListPA[PlayerActivity.songPosition].title)
            .setContentText(PlayerActivity.songsListPA[PlayerActivity.songPosition].artist)
            .setSmallIcon(R.drawable.ic_playlist)
            .setLargeIcon(image)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.ic_navigate_prev,"Previous",prevPendingIntent)
            .addAction(playPausebtn,"Play",playPendingIntent)
            .addAction(R.drawable.ic_navigate_next,"Next",nextPendingIntent)
            .addAction(R.drawable.ic_close,"Exit",exitPendingIntent)
            .build()


        startForeground(13,notification)
    }


    // Initializing MediaPlayer
    fun initializeMediaPlayer() {
        try {
            if (PlayerActivity.songsServices!!.mediaPlayer==null) PlayerActivity.songsServices!!.mediaPlayer = MediaPlayer()
            PlayerActivity.songsServices!!.mediaPlayer!!.reset()
            PlayerActivity.songsServices!!.mediaPlayer!!.setDataSource(PlayerActivity.songsListPA[PlayerActivity.songPosition].path)
            // Setting Image Icon
            PlayerActivity.binding.playBtn.setIconResource(R.drawable.ic_pause)

            PlayerActivity.songsServices!!.showNotification(R.drawable.ic_pause)
            // Duration TV
            PlayerActivity.binding.durationTxt1.text = formatDuration(mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.binding.durationTxt2.text = formatDuration(mediaPlayer!!.duration.toLong())
            // Adding duration in SeekBar
            PlayerActivity.binding.progressDuration.progress = 0
            PlayerActivity.binding.progressDuration.max = mediaPlayer!!.duration
            // Setting nowPlaying Id
            PlayerActivity.nowPlayingId = PlayerActivity.songsListPA[PlayerActivity.songPosition].id

        }catch (e: Exception){
            return
        } }

    fun seekBarSetup(){
        runnable = Runnable {
            PlayerActivity.binding.durationTxt1.text = formatDuration(mediaPlayer!!.currentPosition.toLong())
            PlayerActivity.binding.progressDuration.progress = mediaPlayer!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable,200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable,0)

    }

}