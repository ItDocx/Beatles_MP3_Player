package com.brainsMedia.beatlesmp3player

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.brainsMedia.beatlesmp3player.Adapter.SongsAdapter
import com.brainsMedia.beatlesmp3player.databinding.ActivitySelectionBinding

class SelectionActivity : AppCompatActivity() {

    private lateinit var songAdapter: SongsAdapter
    private lateinit var binding: ActivitySelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val back = findViewById<ImageView>(R.id.menu_btn)
        val searc = findViewById<ImageView>(R.id.search_btn)
        val title = findViewById<TextView>(R.id.title_TV)

        title.setText("Select to add in Playlist")
        back.setImageResource(R.drawable.ic_prev)
        back.setOnClickListener{ finish() }

        binding.searcRecycler.setHasFixedSize(true)
        binding.searcRecycler.setItemViewCacheSize(13)
        binding.searcRecycler.layoutManager = LinearLayoutManager(this)
        songAdapter = SongsAdapter(MainActivity.MusicList,this, selectionActivity = true)
        binding.searcRecycler.adapter = songAdapter

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.search_songs,menu)
        var searchView = menu?.findItem(R.id.search_songs)?.actionView as androidx.appcompat.widget.SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true
            override fun onQueryTextChange(newText: String?): Boolean {
                MainActivity.searchMusicList = ArrayList()
                if (newText != null){
                    val userinput = newText.lowercase()
                    for (song in MainActivity.MusicList){
                        if (song.title.lowercase().contains(userinput)){
                            MainActivity.searchMusicList.add(song)
                            MainActivity.searching = true

                            songAdapter.updateSongsList(searchList = MainActivity.searchMusicList)
                        }

                    }

                }
                return true
            }

        })


        return super.onCreateOptionsMenu(menu)
    }



}