package com.example.musicplayer

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayer.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {
    lateinit var bind: ActivityMainBinding
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var musicAdapter: musicAdapter
    companion object{
        lateinit var musiclistMA:ArrayList<Music>
        lateinit var musicsearchlist:ArrayList<Music>
        var search:Boolean=false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPinkNav)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)

        //drawer
        toggle = ActionBarDrawerToggle(this, bind.root, R.string.open, R.string.close)
        bind.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if(requestruntimepermission())
        initializelayout()
        favorite_activity.favsongs= ArrayList()
        val editor=getSharedPreferences("FAVORITES", MODE_PRIVATE)
        val jsonstring = editor.getString("favoritesongs",null)
        val typetoken=object :TypeToken<ArrayList<Music>>(){}.type
         if(jsonstring!=null){
            val data: ArrayList<Music> = GsonBuilder().create().fromJson(jsonstring, typetoken)
             favorite_activity.favsongs.addAll(data)
         }
        bind.nav.setNavigationItemSelectedListener {
            when (it.itemId) {

                R.id.about -> startActivity(Intent(this,about_activity::class.java))
                R.id.exit -> {
                    val builder=MaterialAlertDialogBuilder(this)
                    builder.setTitle("Exit")
                        .setMessage("Do you want to close the app ?")
                        .setPositiveButton("yes"){_, _ ->
                            stopmusic()
                            exitProcess(1)
                        }
                        .setNegativeButton("No"){dialog,_ ->
                           dialog.dismiss()
                        }
                    val customdialog=builder.create()
                    customdialog.show()
                    customdialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
                    customdialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
                }
            }
            true
        }

        //button
        bind.shufflebtn.setOnClickListener {
            val intent = Intent(this, player_activity::class.java)
            intent.putExtra("index",0)
            intent.putExtra("class","Mainactivity")
            startActivity(intent)
        }
        bind.favoritebtn.setOnClickListener {
            val intent = Intent(this, favorite_activity::class.java)
            startActivity(intent)
        }

    }

    //storage permission
    private fun requestruntimepermission():Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                13
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 13) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                initializelayout()
            } else {
                requestruntimepermission()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("SetTextI18n")
    private fun initializelayout() {
        search=false
        musiclistMA=getallaudio()
        bind.musicRV.setHasFixedSize(true)
        bind.musicRV.setItemViewCacheSize(13)
        bind.musicRV.layoutManager = LinearLayoutManager(this)
        musicAdapter = musicAdapter(this, musiclistMA)
        bind.musicRV.adapter = musicAdapter
        bind.totalsongs.text = "Total Songs : " + musicAdapter.itemCount
    }

    @SuppressLint("Range")
    private fun getallaudio(): ArrayList<Music> {
        val templist = ArrayList<Music>()
        val selection = MediaStore.Audio.Media.DURATION+">=30000"
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID
        )
        val cursor=this.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,selection,null,null,null)

        if (cursor != null) {
            if(cursor.moveToFirst()){
                do {

                    val titlec = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val idc = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))

                    val albumc = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))

                    val pathcc = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))

                    val artistc = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val durationc =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val albumid=cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))

                    val url=Uri.parse("content://media/external/audio/albumart")
                    val arturi=Uri.withAppendedPath(url,albumid).toString()
                    val music = Music(idc, titlec, albumc, artistc, durationc, pathcc,arturi)
                    if (File(music.path).exists()) {
                        templist.add(music)
                    }
                }while (cursor.moveToNext())
                cursor.close()
            }
        }
        return templist
    }

    override fun onDestroy() {
        super.onDestroy()
        if(!player_activity.isplaying && player_activity.musicservice!=null){
            player_activity.musicservice!!.stopForeground(true)
            player_activity.musicservice=null
            player_activity.musicservice?.audioManager?.abandonAudioFocus(player_activity.musicservice)
            player_activity.musicservice!!.mediaplayer!!.release()
            exitProcess(1)
        }
    }

    override fun onResume() {
        super.onResume()
        //for storing favorite in sharedprefences
        val editor = getSharedPreferences("FAVORITES", MODE_PRIVATE).edit()
        val jsonstring = GsonBuilder().create().toJson(favorite_activity.favsongs)
        editor.putString("favoritesongs", jsonstring)
        editor.apply()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu,menu)
        val searchview=menu?.findItem(R.id.searchmenu)?.actionView as SearchView
        searchview.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?)=true
            override fun onQueryTextChange(newText: String?): Boolean {
                musicsearchlist= ArrayList()
                if(newText!=null){
                    var usertext=newText.lowercase()
                    for(song in musiclistMA){
                        if(song.title.lowercase().contains(usertext))
                            musicsearchlist.add(song)
                    }
                    search=true
                    musicAdapter.updatelist(musicsearchlist)
                }
               return true
            }
        }
        )
        return super.onCreateOptionsMenu(menu)
    }
}