package com.brainsMedia.beatlesmp3player.Fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.brainsMedia.beatlesmp3player.Models.setSongsPosition
import com.brainsMedia.beatlesmp3player.PlayerActivity
import com.brainsMedia.beatlesmp3player.R
import com.brainsMedia.beatlesmp3player.databinding.FragmentNowPlayingBinding


class NowPlaying : Fragment() {

    companion object{
       @SuppressLint("StaticFieldLeak")
       lateinit var binding: FragmentNowPlayingBinding
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.fragment_now_playing, container, false)
        binding = FragmentNowPlayingBinding.bind(view)
        binding.songNameTxtNP.isSelected = true
        binding.root.visibility = View.GONE

        binding.playBtnNP.setOnClickListener{
            if (PlayerActivity.isPlaying) songPause() else songPlay()
        }

        binding.nextBtnNP.setOnClickListener{

            setSongsPosition(increment = true)
            PlayerActivity.songsServices!!.initializeMediaPlayer()

            // Setting Song Image
            Glide.with(this).load(PlayerActivity.songsListPA[PlayerActivity.songPosition].artUri).
            apply(RequestOptions().placeholder(R.drawable.splash2).centerCrop()).into(NowPlaying.binding.songImageNP)

            //Setting Song Name Text
            binding.songNameTxtNP.text = PlayerActivity.songsListPA[PlayerActivity.songPosition].title
            PlayerActivity.songsServices!!.showNotification(R.drawable.ic_pause)
            songPlay()

        }

        binding.root.setOnClickListener{

            val intent = Intent(requireContext(),PlayerActivity::class.java)
            intent.putExtra("index",PlayerActivity.songPosition)
            intent.putExtra("class","NowPlaying")
            ContextCompat.startActivity(requireContext(),intent,null)

        }



        return view

    }

    override fun onResume() {
        super.onResume()

        if (PlayerActivity.songsServices != null){
            binding.root.visibility = View.VISIBLE
            // Setting Song Image
            Glide.with(this).load(PlayerActivity.songsListPA[PlayerActivity.songPosition].artUri).
            apply(RequestOptions().placeholder(R.drawable.splash2).centerCrop()).into(binding.songImageNP)

            //Setting Song Name Text
            binding.songNameTxtNP.text = PlayerActivity.songsListPA[PlayerActivity.songPosition].title

            //Setting Play Pause Button

            if (PlayerActivity.isPlaying) binding.playBtnNP.setIconResource(R.drawable.ic_pause)
            else binding.playBtnNP.setIconResource(R.drawable.ic_play_arrow)
        }
    }

    private fun songPlay(){
        PlayerActivity.songsServices!!.mediaPlayer!!.start()
        binding.playBtnNP.setIconResource(R.drawable.ic_pause)
        PlayerActivity.songsServices!!.showNotification(R.drawable.ic_pause)
        PlayerActivity.binding.playBtn.setIconResource(R.drawable.ic_pause)
        PlayerActivity.isPlaying = true
    }

    private  fun songPause(){

        PlayerActivity.songsServices!!.mediaPlayer!!.pause()
        binding.playBtnNP.setIconResource(R.drawable.ic_play_arrow)
        PlayerActivity.songsServices!!.showNotification(R.drawable.ic_play_arrow)
        PlayerActivity.binding.playBtn.setIconResource(R.drawable.ic_play_arrow)
        PlayerActivity.isPlaying = false

    }

}