package com.brainsMedia.beatlesmp3player

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.brainsMedia.beatlesmp3player.Adapter.SongsAdapter
import com.brainsMedia.beatlesmp3player.Models.SongsPlaylist
import com.brainsMedia.beatlesmp3player.Models.checkPlaylist
import com.brainsMedia.beatlesmp3player.databinding.ActivityPlaylistDetailsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder

class PlaylistDetails : AppCompatActivity() {

    lateinit var binding: ActivityPlaylistDetailsBinding
    lateinit var detailsAdapter : SongsAdapter
    companion object{
        var currentPlaylistPos: Int = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_BeatlesMP3PlayerCustom)
        binding = ActivityPlaylistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val search_btn = findViewById<ImageView>(R.id.search_btn)

        search_btn.visibility = View.GONE


        currentPlaylistPos = intent.extras?.get("index") as Int

        PlayListActivity.songsPlaylist.ref[currentPlaylistPos].playList =
            checkPlaylist(Playlist = PlayListActivity.
            songsPlaylist.ref[currentPlaylistPos].playList)

        binding.detailsRecycler.setItemViewCacheSize(10)
        binding.detailsRecycler.setHasFixedSize(true)
        binding.detailsRecycler.layoutManager = LinearLayoutManager(this)
        detailsAdapter = SongsAdapter(
            PlayListActivity.songsPlaylist.ref[currentPlaylistPos].playList,
            this,
            detailsPlaylist = true
        )
        binding.detailsRecycler.adapter = detailsAdapter


        binding.detailsshuffleBtn.setOnClickListener {
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "detailsShuffle")
            startActivity(intent)
        }

        binding.detailsAddBtn.setOnClickListener {

            startActivity(Intent(this, SelectionActivity::class.java))

        }
        binding.detailsRemoveBtn.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(this)
                .setTitle("Remove").setMessage("Do want to remove all songs from playlist?")
                .setPositiveButton("Yes") { dialog, _ ->

                    PlayListActivity.songsPlaylist.ref[currentPlaylistPos].playList.clear()
                    dialog.dismiss()
                }.setNegativeButton("NO") { dialog, _ ->
                    detailsAdapter.refreshSongs()


                    dialog.dismiss()
                }
            val customdialog = builder.create()
            customdialog.show()
            customdialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.GREEN)
            customdialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        val back_btn = findViewById<ImageView>(R.id.menu_btn)
        back_btn.setImageResource(R.drawable.ic_prev)
        val detailsname = findViewById<TextView>(R.id.title_TV)
        back_btn.setOnClickListener{
            finish()
        }

        detailsname.text = PlayListActivity.songsPlaylist.ref[currentPlaylistPos].name

        binding.detailsTotal.text = "Total ${detailsAdapter.itemCount} Songs\n\n\n"+
                "Created on: ${PlayListActivity.songsPlaylist.ref[currentPlaylistPos].createdOn}\n\n\n"+
                "-- ${PlayListActivity.songsPlaylist.ref[currentPlaylistPos].createdBy}"

        if (detailsAdapter.itemCount >0){

            Glide.with(this).load(PlayListActivity.songsPlaylist.
            ref[currentPlaylistPos].playList[0].artUri)
                .apply(RequestOptions().placeholder(R.drawable.splash2)).into(binding.detailPoster)

            binding.detailsshuffleBtn.visibility = View.VISIBLE
        }
        detailsAdapter.notifyDataSetChanged()

            val editorplayList = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
            val jsonStringPlaylist = GsonBuilder().create().toJson(PlayListActivity.songsPlaylist)
            editorplayList.putString("SongsPlaylist",jsonStringPlaylist)
            editorplayList.apply()


    }
}