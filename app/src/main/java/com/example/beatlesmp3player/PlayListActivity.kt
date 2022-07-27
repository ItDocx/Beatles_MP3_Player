package com.example.beatlesmp3player

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.example.beatlesmp3player.Adapter.PlaylistAdapter
import com.example.beatlesmp3player.Models.Playlist
import com.example.beatlesmp3player.Models.SongsPlaylist
import com.example.beatlesmp3player.databinding.ActivityPlayListBinding
import com.example.beatlesmp3player.databinding.CustomDialogPlaylistBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PlayListActivity : AppCompatActivity() {

 private lateinit var binding: ActivityPlayListBinding

 companion object {
     @SuppressLint("StaticFieldLeak")
     private lateinit var playlistAdapter : PlaylistAdapter
     var songsPlaylist : SongsPlaylist = SongsPlaylist()
 }
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_BeatlesMP3PlayerCustom)
        super.onCreate(savedInstanceState)

        binding = ActivityPlayListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Setting Toolbar
        TollbarLayout()
        binding.PlayListRV.setHasFixedSize(true)
        binding.PlayListRV.setItemViewCacheSize(13)
        binding.PlayListRV.layoutManager = GridLayoutManager(this,2)
        playlistAdapter = PlaylistAdapter(playList = songsPlaylist.ref,this)
        binding.PlayListRV.adapter = playlistAdapter

        // Add Playlist Button
        binding.PlaylistAddBtn.setOnClickListener{
            addPlayListDialog()
        }

    }

    private fun TollbarLayout(){

        val back = findViewById<ImageView>(R.id.menu_btn)
        val search = findViewById<ImageView>(R.id.search_btn)
        val plaListTitle = findViewById<TextView>(R.id.title_TV)

        back.setImageResource(R.drawable.ic_prev)

        search.setVisibility(View.GONE)
        plaListTitle.setText(R.string.Playlist_title)

        back.setOnClickListener{
            finish()
        }
    }

    private fun addPlayListDialog()
    {
        val customDialog = LayoutInflater.from(this@PlayListActivity).
        inflate(R.layout.custom_dialog_playlist,binding.root,false)
        val bind = CustomDialogPlaylistBinding.bind(customDialog)
        val builder = MaterialAlertDialogBuilder(this)
        builder.setView(customDialog)
            .setTitle("Playlist Details")
            .setPositiveButton("Add"){dialog,_->
                val playlist_name = bind.PlaylistNameET.text
                val createdBy = bind.PlaylistUserNameET.text

                if (bind.PlaylistNameET.length().equals(0))
                {
                    bind.PlaylistNameET.setError("Please Enter Playlist Name")

                }
                else if(bind.PlaylistUserNameET.length().equals(0))
                {
                    bind.PlaylistUserNameET.setError("Please Enter User name")
                }
                else {
                    if (playlist_name != null && createdBy != null)
                        if (playlist_name.isNotEmpty() && createdBy.isNotEmpty()) {

                            addPlayList(playlist_name.toString(), createdBy.toString())
                            dialog.dismiss()
                        }

                }

            }.show()


    }

    private fun addPlayList(playlistName: String, userName: String) {
        var playlistExists = false
        for (i in songsPlaylist.ref){
            if (playlistName.equals(i.name)){
                playlistExists = true
                break
            }
        }
        if (playlistExists) Toast.makeText(this,"Playlist Alrady Exists",Toast.LENGTH_SHORT).show()
        else{
            val tempList = Playlist()
            tempList.name = playlistName
            tempList.createdBy = userName
            tempList.playList = ArrayList()

            val calendar = Calendar.getInstance().time
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
            tempList.createdOn = sdf.format(calendar)
            songsPlaylist.ref.add(tempList)
            playlistAdapter.refreshPlaylist()
        }

    }

    override fun onResume() {
        super.onResume()
        playlistAdapter.notifyDataSetChanged()
    }
}

