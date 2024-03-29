package com.brainsMedia.beatlesmp3player

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.brainsMedia.beatlesmp3player.Adapter.SongsAdapter
import com.brainsMedia.beatlesmp3player.Dialoges.AboutUsDialoge
import com.brainsMedia.beatlesmp3player.Dialoges.RateUsDialoge
import com.brainsMedia.beatlesmp3player.Models.SongsLIst
import com.brainsMedia.beatlesmp3player.Models.SongsPlaylist
import com.brainsMedia.beatlesmp3player.Models.exitApplication
import com.brainsMedia.beatlesmp3player.databinding.ActivityMainBinding

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var songAdapter: SongsAdapter

    // Lis for songs List
    companion object{
        lateinit var MusicList: ArrayList<SongsLIst>
        lateinit var searchMusicList: ArrayList<SongsLIst>
        var searching: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Permission Function
       // requestStoragePermission()

        //View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)




        // Fix Runtime Permission Bug

        if(requestStoragePermission()) {
            // Layout Initialization
            onInitializeLayout()

            // for Retrieving  Favourite Songs Data using SharedPreferences with the help of JSON

            FavouriteActivity.favList = ArrayList()
            val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE)
            val jsonString = editor.getString("favourites",null)
            val typeToken = object: TypeToken<ArrayList<SongsLIst>>(){}.type
            if (jsonString != null){
                val data: ArrayList<SongsLIst> = GsonBuilder().create().fromJson(jsonString,typeToken)
                FavouriteActivity.favList.addAll(data)

            }
            PlayListActivity.songsPlaylist = SongsPlaylist()
            val jsonStringPlaylist = editor.getString("SongsPlayList",null)
            if (jsonStringPlaylist != null){
                val dataPlaylist: SongsPlaylist = GsonBuilder().create().fromJson(jsonStringPlaylist,SongsPlaylist::class.java)
                PlayListActivity.songsPlaylist = dataPlaylist

            }


        }

        binding.ShuffleBtn.setOnClickListener {
            val intent = Intent(this,PlayerActivity::class.java)
            intent.putExtra("index",0)
            intent.putExtra("class","MainActivity")
            startActivity(intent)

        }

        binding.FavouriteBtn.setOnClickListener{
                startActivity(Intent(this,FavouriteActivity::class.java))
        }


        binding.PlaylistBtn.setOnClickListener{

            startActivity(Intent(this,PlayListActivity::class.java))

        }




        // NavigationView Listener
        binding.navView.setNavigationItemSelectedListener{
            when(it.itemId){

                R.id.feedback_btn -> {
                    settingRateUsDialoge()
                    binding.mainDrawer!!.closeDrawer(GravityCompat.START)
                }
                R.id.settings_btn -> Toast.makeText(baseContext,"Settings",Toast.LENGTH_SHORT).show()
                R.id.aboutUs_btn -> {
                    setAboutUsDialoge()
                    binding.mainDrawer!!.closeDrawer(GravityCompat.START)
                }
                R.id.exit_btn -> exitProcess(1)
            }
            true
        }




    }

    // Check Storage Permission
    private fun requestStoragePermission(): Boolean{
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
            PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),13)
            return false
        }
        return true
    }

    // Getting Permission From User
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 13){

            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show()
                MusicList = getAllAudio()

            }
            else {
                ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),13)
            }

        }
    }






    //Layout Initialization

    @SuppressLint("SetTextI18n")
    private fun onInitializeLayout(){

        // Initializing Recycler View
        // this creates a vertical layout Manager



            binding.recyclerSongs.layoutManager = LinearLayoutManager(this)

           MusicList = getAllAudio()
            // This will pass the ArrayList to our Adapter
            songAdapter = SongsAdapter(MusicList, this@MainActivity)

            // Setting the Adapter with the recyclerview
            binding.recyclerSongs.adapter = songAdapter
            // Initializing Total Songs
            binding.titleTotalSongs.setText("Total Songs: "+songAdapter.itemCount)

     //       songAdapter.notifyDataSetChanged()
       //     binding.refresher.isRefreshing = false

        toggle = ActionBarDrawerToggle(this,binding.root,R.string.open,R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }


    // Fetching Audio from External Storage
    @SuppressLint("Range")
    private fun getAllAudio():ArrayList<SongsLIst>{

        val tempList = ArrayList<SongsLIst>()

        val Selection = MediaStore.Audio.Media.IS_MUSIC+ " !=0 "
        val projection = arrayOf(MediaStore.Audio.Media._ID,MediaStore.Audio.Media.TITLE,MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_ID)
        val cursor = this.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,Selection,
            null,MediaStore.Audio.Media.DATE_ADDED+" Desc",null)

        if (cursor!=null && cursor.moveToFirst()) {

            do {
                val titleC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                val idC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                val albumC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                val artistC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                val durationC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                val albumIDC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toString()
                val uri = Uri.parse("content://media/external/audio/Albumart")
                val artUriC = Uri.withAppendedPath(uri, albumIDC).toString()

                val music = SongsLIst(
                    title = titleC, id = idC, album = albumC,
                    artist = artistC, path = pathC, duration = durationC, artUri = artUriC)

                val file = File(music.path)
                if (file.exists()) {
                    tempList.add(music)
                }

            } while (cursor.moveToNext())
            cursor.close()
        }
        return tempList
    }
    override fun onDestroy() {
        super.onDestroy()
        if(!PlayerActivity.isPlaying && PlayerActivity.songsServices != null) {
            exitApplication()
        }
    }

    override fun onResume() {
        super.onResume()

        // for Storing Favourite Songs Data using SharedPreferences with the help of JSON
        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
        val jsonString = GsonBuilder().create().toJson(FavouriteActivity.favList)
        editor.putString("favourites",jsonString)

        val jsonStringPlaylist = GsonBuilder().create().toJson(PlayListActivity.songsPlaylist)
        editor.putString("SongsPlaylist",jsonStringPlaylist)
        editor.apply()

    }

    @SuppressLint("ResourceType")
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.search_songs,menu)
        var searchView = menu?.findItem(R.id.search_songs)?.actionView as androidx.appcompat.widget.SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true
            override fun onQueryTextChange(newText: String?): Boolean {
                searchMusicList = ArrayList()
                if (newText != null){
                    val userinput = newText.lowercase()
                    for (song in MusicList){
                        if (song.title.lowercase().contains(userinput)){
                            searchMusicList.add(song)
                            searching = true

                            songAdapter.updateSongsList(searchList = searchMusicList)
                        }

                    }

                }
                return true
            }

        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item))
            return true

        return super.onOptionsItemSelected(item)
    }


    fun settingRateUsDialoge() {
        val rate = RateUsDialoge(this@MainActivity)
        rate.getWindow()?.setBackgroundDrawable(
            ColorDrawable(
                resources
                    .getColor(android.R.color.white)
            )
        )
        rate.setCancelable(false)
        rate.show()
    }

    fun setAboutUsDialoge() {
        val dashBoardDialog = AboutUsDialoge(this@MainActivity)
        dashBoardDialog.setCancelable(false)
        dashBoardDialog.show()
    }



}