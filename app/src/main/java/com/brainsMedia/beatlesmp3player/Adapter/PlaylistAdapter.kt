package com.brainsMedia.beatlesmp3player.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.brainsMedia.beatlesmp3player.Models.Playlist
import com.brainsMedia.beatlesmp3player.PlayListActivity
import com.brainsMedia.beatlesmp3player.PlaylistDetails
import com.brainsMedia.beatlesmp3player.R
import com.brainsMedia.beatlesmp3player.databinding.PlaylistItemsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PlaylistAdapter (private var playList: ArrayList<Playlist>, private val context: Context): RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(PlaylistItemsBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.playlistName.text = playList[position].name
        holder.playlistName.isSelected = true

        holder.delete_btn.setOnClickListener{

            val builder = MaterialAlertDialogBuilder(context)
            builder.setTitle(playList[position].name).setMessage("Do you really want to delete the Playlist?")
                .setPositiveButton("Yes"){dialog, _->

                    // adding delete functionality
                    PlayListActivity.songsPlaylist.ref.removeAt(position)
                    refreshPlaylist()
                    dialog.dismiss()
                }.setNegativeButton("No"){dialog,_->

                    dialog.dismiss()
                }
            val customdialog = builder.create()
            customdialog.show()
            customdialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.GREEN)
            customdialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)

            holder.itemView.setOnClickListener{

                val intent = Intent(context, PlaylistDetails::class.java)
                intent.putExtra("index",position)
                ContextCompat.startActivity(context,intent,null)
            }

            if (PlayListActivity.songsPlaylist.ref[position].playList.size >0){

                Glide.with(context).load(PlayListActivity.songsPlaylist.
                ref[position].playList[0].artUri)
                    .apply(RequestOptions().placeholder(R.drawable.splash2)).into(holder.playlistImage)

            }

        }

    }

    override fun getItemCount(): Int {
        return playList.size
    }

    // View Holder Class
    class ViewHolder(binding: PlaylistItemsBinding):RecyclerView.ViewHolder(binding.root) {
        val playlistImage = binding.playlistSongImage
        val playlistName = binding.playlistSongName
        val delete_btn = binding.playListDelBtn

    }

    fun refreshPlaylist(){

        playList = ArrayList()
        playList.addAll(PlayListActivity.songsPlaylist.ref)
        notifyDataSetChanged()

    }

}