package com.example.beatlesmp3player.Adapter


import android.annotation.SuppressLint
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
import com.example.beatlesmp3player.*
import com.example.beatlesmp3player.Adapter.SongsAdapter.ViewHolder
import com.example.beatlesmp3player.Fragments.NowPlaying
import com.example.beatlesmp3player.Models.SongsLIst
import com.example.beatlesmp3player.Models.formatDuration
import com.example.beatlesmp3player.databinding.SongsListItemsBinding
import java.lang.reflect.Array

class SongsAdapter(private var mList: List<SongsLIst>,private val context:Context, private val detailsPlaylist: Boolean = false ,
private val selectionActivity: Boolean = false) : RecyclerView.Adapter<SongsAdapter.ViewHolder>()
{





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


        when {
            detailsPlaylist ->{

                holder.root.setOnClickListener{
                    sendData(ref= "PlaylistDetailsAdapter", pos =position)
                }

            }

            selectionActivity->{
                holder.root.setOnClickListener{
                    if (addSogs(mList[position])){
                        holder.root.setBackgroundColor(ContextCompat.getColor(context,R.color.darkorange))
                    }
                    else holder.root.setBackgroundColor(ContextCompat.getColor(context,R.color.white))

                }

            }

       else-> {
           holder.root.setOnClickListener {
               when {
                   MainActivity.searching -> sendData(ref = "MusicAdapterSearch", pos = position)
                   mList[position].id == PlayerActivity.nowPlayingId ->
                       sendData(ref = "NowPlaying", pos = PlayerActivity.songPosition)

                   else -> sendData(ref = "MusicAdapter", pos = position)
               }
           }

       }}

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateSongsList(searchList: ArrayList<SongsLIst>){
        mList = ArrayList()
        (mList as ArrayList<SongsLIst>).addAll(searchList)
        notifyDataSetChanged()

    }

    // Holds the views for adding it to image and text
    class ViewHolder(bindin: SongsListItemsBinding) : RecyclerView.ViewHolder(bindin.root) {

        val imageView = bindin.songIcon
        val song_album = bindin.songAlbum
        val song_name = bindin.songName
        val song_duration = bindin.songDuration
        val root = bindin.root
    }


    private fun sendData(ref: String, pos: Int){

        val intent = Intent(context,PlayerActivity::class.java)
        intent.putExtra("index",pos)
        intent.putExtra("class",ref)
        ContextCompat.startActivity(context,intent,null)    }

    private fun addSogs(songs: SongsLIst):Boolean{
        PlayListActivity.songsPlaylist.ref[PlaylistDetails.currentPlaylistPos].playList.forEachIndexed { index, songsLIst ->
            if(songs.id == songs.id){
                PlayListActivity.songsPlaylist.ref[PlaylistDetails.currentPlaylistPos].playList.removeAt(index)
            return false
            }
        }
        PlayListActivity.songsPlaylist.ref[PlaylistDetails.currentPlaylistPos].playList.add(songs)
        return true
    }

    fun refreshSongs(){
        mList = ArrayList()
        mList = PlayListActivity.songsPlaylist.ref[PlaylistDetails.currentPlaylistPos].playList
        notifyDataSetChanged()
    }
}