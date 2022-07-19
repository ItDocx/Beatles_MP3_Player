package com.example.beatlesmp3player.Models

import android.content.Intent
import android.icu.text.CaseMap
import android.media.MediaMetadata
import android.media.MediaMetadataRetriever
import android.widget.Toolbar
import com.example.beatlesmp3player.FavouriteActivity
import com.example.beatlesmp3player.PlayerActivity
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

data class SongsLIst(val id:String, val title: String, val album:String, val artist:String,
                     val duration: Long, val path:String, val artUri: String)

fun formatDuration(duration: Long):String{

    val minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
    val seconds = TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS) -
            minutes*TimeUnit.SECONDS.convert(1,TimeUnit.MINUTES)
    return String.format("%02d:%02d",minutes,seconds)
}

fun getIMaginNotification(path: String): ByteArray? {

    val retreiver = MediaMetadataRetriever()
    retreiver.setDataSource(path)
    return retreiver.embeddedPicture
}

//Set Songs Position
fun setSongsPosition(increment: Boolean){

    if (!PlayerActivity.repeat){
        if (increment){
            if (PlayerActivity.songsListPA.size -1 == PlayerActivity.songPosition)
                PlayerActivity.songPosition =0
            else ++PlayerActivity.songPosition
        }
        else{
            if (0== PlayerActivity.songPosition)
                PlayerActivity.songPosition = PlayerActivity.songsListPA.size -1
            else --PlayerActivity.songPosition
        }
    }
     }


fun exitApplication(){
    if (PlayerActivity.songsServices != null){
        PlayerActivity.songsServices!!.mediaPlayer!!.release()
        PlayerActivity.songsServices!!.stopForeground(true)
        PlayerActivity.songsServices = null
    }
    exitProcess(1)

}

fun favChecker(id: String): Int{
    PlayerActivity.isfav = false

    FavouriteActivity.favList.forEachIndexed{index, music ->
        if (id==music.id){
            PlayerActivity.isfav = true

            return index
        }
    }
    return  -1
}

