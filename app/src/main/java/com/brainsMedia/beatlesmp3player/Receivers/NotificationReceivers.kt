package com.brainsMedia.beatlesmp3player.Receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.brainsMedia.beatlesmp3player.Fragments.NowPlaying
import com.brainsMedia.beatlesmp3player.Models.exitApplication
import com.brainsMedia.beatlesmp3player.Models.favChecker
import com.brainsMedia.beatlesmp3player.Models.setSongsPosition
import com.brainsMedia.beatlesmp3player.PlayerActivity
import com.brainsMedia.beatlesmp3player.R
import com.brainsMedia.beatlesmp3player.Services.SongsApplication
import kotlin.coroutines.coroutineContext

class NotificationReceivers: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action){
            SongsApplication.PREVIOUS-> prevNext_btn(increment = false , context = context!!)
            SongsApplication.PLAY-> if(PlayerActivity.isPlaying) pause_btn_function() else play_btn_function()
            SongsApplication.NEXT-> prevNext_btn(increment = true, context = context!!)
            SongsApplication.EXIT-> {
                exitApplication()

            }
        }
    }


   private fun play_btn_function(){

        PlayerActivity.isPlaying = true
        PlayerActivity.songsServices!!.mediaPlayer!!.start()
        PlayerActivity.songsServices!!.showNotification(R.drawable.ic_pause)
        PlayerActivity.binding.playBtn.setIconResource(R.drawable.ic_pause)
        NowPlaying.binding.playBtnNP.setIconResource(R.drawable.ic_pause)
    }

    private fun pause_btn_function(){

        PlayerActivity.isPlaying = false
        PlayerActivity.songsServices!!.mediaPlayer!!.pause()
        PlayerActivity.songsServices!!.showNotification(R.drawable.ic_play_arrow)
        PlayerActivity.binding.playBtn.setIconResource(R.drawable.ic_play_arrow)
        NowPlaying.binding.playBtnNP.setIconResource(R.drawable.ic_play_arrow)
    }

    private fun prevNext_btn(increment: Boolean, context: Context){

        setSongsPosition(increment = increment)
        PlayerActivity.songsServices!!.initializeMediaPlayer()

        Glide.with(context).load(PlayerActivity.songsListPA[PlayerActivity.songPosition].artUri).
        apply(RequestOptions().placeholder(R.drawable.splash2).centerCrop()).into(PlayerActivity.binding.posterPlayer)

        PlayerActivity.binding.songTitle.text = PlayerActivity.songsListPA[PlayerActivity.songPosition].title

        // Setting Song Image
        Glide.with(context).load(PlayerActivity.songsListPA[PlayerActivity.songPosition].artUri).
        apply(RequestOptions().placeholder(R.drawable.splash2).centerCrop()).into(NowPlaying.binding.songImageNP)

        //Setting Song Name Text
        NowPlaying.binding.songNameTxtNP.text = PlayerActivity.songsListPA[PlayerActivity.songPosition].title
        play_btn_function()
        PlayerActivity.fIndex = favChecker( PlayerActivity.songsListPA[PlayerActivity.songPosition].id)




        if (PlayerActivity.isfav){
            PlayerActivity.binding.searchBtn.setImageResource(R.drawable.ic_favorite)
        }
        else PlayerActivity.binding.searchBtn.setImageResource(R.drawable.ic_favorite_empty)


    }

}