package com.example.beatlesmp3player.Adapter


import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.LayoutInflater.from
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.beatlesmp3player.Adapter.SongsAdapter.ViewHolder
import com.example.beatlesmp3player.MainActivity
import com.example.beatlesmp3player.Models.SongsLIst
import com.example.beatlesmp3player.Models.formatDuration
import com.example.beatlesmp3player.PlayerActivity
import com.example.beatlesmp3player.R
import com.example.beatlesmp3player.databinding.SongsListItemsBinding

class SongsAdapter(private val mList: List<SongsLIst>,private val context:Context ) : RecyclerView.Adapter<SongsAdapter.ViewHolder>() {





    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item

        return ViewHolder(SongsListItemsBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        // sets the image to the imageview from our itemHolder class
        holder.song_name.text = mList[position].title
        holder.song_album.text = mList[position].album
        holder.song_duration.text = formatDuration(mList[position].duration)
        // Parsing Image With Glide
        Glide.with(context).load(mList[position].artUri).apply(RequestOptions().
            placeholder(R.drawable.potser).centerCrop()).into(holder.imageView)


        holder.root.setOnClickListener{

            val intent = Intent(context,PlayerActivity::class.java)
            intent.putExtra("index",position)
            intent.putExtra("class","MusicAdapter")
            ContextCompat.startActivity(context,intent,null)
        }



    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(bindin: SongsListItemsBinding) : RecyclerView.ViewHolder(bindin.root) {

        val imageView = bindin.songIcon
        val song_album = bindin.songAlbum
        val song_name = bindin.songName
        val song_duration = bindin.songDuration
        val root = bindin.root
    }
}