package com.example.beatlesmp3player.Adapter


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.beatlesmp3player.Adapter.SongsAdapter.ViewHolder
import com.example.beatlesmp3player.MainActivity
import com.example.beatlesmp3player.Models.SongsLIst
import com.example.beatlesmp3player.PlayerActivity
import com.example.beatlesmp3player.R

class SongsAdapter(private val mList: List<SongsLIst>) : RecyclerView.Adapter<SongsAdapter.ViewHolder>() {

    private lateinit var context: Context



    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.songs_list_items, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        // sets the image to the imageview from our itemHolder class
        holder.textView.text = mList[position].title
        holder.song_album.text = mList[position].album
        holder.song_duration.text = mList[position].artist



    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = ItemView.findViewById(R.id.song_icon)
        val textView: TextView = ItemView.findViewById(R.id.song_name)
        val song_duration = itemView.findViewById<TextView>(R.id.song_duration)
        val song_album = itemView.findViewById<TextView>(R.id.song_album)

    }
}