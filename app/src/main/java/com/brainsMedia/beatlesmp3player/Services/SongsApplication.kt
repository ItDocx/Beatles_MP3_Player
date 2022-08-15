package com.brainsMedia.beatlesmp3player.Services

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class SongsApplication: Application() {

    companion object{
        const val channel_ID = "Channel1"
        const val PLAY = "Play"
        const val NEXT = "Next"
        const val PREVIOUS = "Previous"
        const val EXIT = "Exit"
    }

    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(channel_ID,"Now Playing Song",NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.description = "This is Channel for showing songs......!!"
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

}