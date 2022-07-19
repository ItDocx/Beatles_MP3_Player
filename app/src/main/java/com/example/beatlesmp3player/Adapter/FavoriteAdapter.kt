package com.example.beatlesmp3player.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.beatlesmp3player.MainActivity
import com.example.beatlesmp3player.Models.SongsLIst
import com.example.beatlesmp3player.Models.formatDuration
import com.example.beatlesmp3player.PlayerActivity
import com.example.beatlesmp3player.R
import com.example.beatlesmp3player.databinding.FavouriteItemsBinding

class FavoriteAdapter(private var mList: List<SongsLIst>, private val context: Context) : RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {

    // Holds the views for adding it to image and text
    class ViewHolder(bindin: FavouriteItemsBinding) : RecyclerView.ViewHolder(bindin.root) {

        val favimage = bindin.favSongImage
        val favsongName = bindin.favSongName
        val root = bindin.root
    }


    private fun sendData(ref: String, pos: Int){
        val intent = Intent(context, PlayerActivity::class.java)
        intent.putExtra("index",pos)
        intent.putExtra("class",ref)
        ContextCompat.startActivity(context,intent,null)    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(FavouriteItemsBinding.inflate(LayoutInflater.from(context),parent,false))

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
     holder.favsongName.text = mList[position].title
        Glide.with(context).load(mList[position].artUri).apply(RequestOptions().
        placeholder(R.drawable.potser).centerCrop()).into(holder.favimage)

        holder.root.setOnClickListener{
            val intent = Intent(context,PlayerActivity::class.java)
            intent.putExtra("index",position)
            intent.putExtra("class","FavouriteAdapter")
            ContextCompat.startActivity(context,intent,null)
        }

    }

    override fun getItemCount(): Int {
        return mList.size
    }
}