package com.example.beatlesmp3player.Services

import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.media.MediaSessionManager
import com.example.beatlesmp3player.PlayerActivity
import com.example.beatlesmp3player.R

class SongsServices: Service() {

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
    fun showNotification(){
        val notification = NotificationCompat.Builder(baseContext,SongsApplication.channel_ID)
            .setContentTitle(PlayerActivity.songsListPA[PlayerActivity.songPosition].title)
            .setContentText(PlayerActivity.songsListPA[PlayerActivity.songPosition].artist)
            .setSmallIcon(R.drawable.ic_playlist)
            .setLargeIcon(BitmapFactory.decodeResource(resources,R.drawable.splash1))
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.ic_prev,"Previous",null)
            .addAction(R.drawable.ic_play_arrow,"Play",null)
            .addAction(R.drawable.ic_forward,"Next",null)
            .addAction(R.drawable.ic_exit,"Exit",null)
            .build()


        startForeground(13,notification)
    }

}