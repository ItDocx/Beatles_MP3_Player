package com.example.beatlesmp3player

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beatlesmp3player.Adapter.SongsAdapter
import com.example.beatlesmp3player.Models.SongsLIst
import com.example.beatlesmp3player.Models.exitApplication
import com.example.beatlesmp3player.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import java.io.File
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var songAdapter: SongsAdapter


    // Lis for songs List
    companion object{
        lateinit var MusicList: ArrayList<SongsLIst>
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_BeatlesMP3Player)
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

                R.id.feedback_btn -> Toast.makeText(baseContext,"Feedback",Toast.LENGTH_SHORT).show()
                R.id.settings_btn -> Toast.makeText(baseContext,"Settings",Toast.LENGTH_SHORT).show()
                R.id.aboutUs_btn -> Toast.makeText(baseContext,"About Us",Toast.LENGTH_SHORT).show()
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

     //Adding Swipe Refresh View

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
        } }

    @SuppressLint("ResourceType")
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.search_songs,menu)
        var searchView = menu?.findItem(R.id.search_songs)?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean = true
            override fun onQueryTextChange(newText: String?): Boolean {
                Toast.makeText(this@MainActivity,newText.toString(),Toast.LENGTH_SHORT).show()
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

}