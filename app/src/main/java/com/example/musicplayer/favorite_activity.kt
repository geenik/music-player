package com.example.musicplayer

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.musicplayer.databinding.ActivityFavoriteBinding

class favorite_activity : AppCompatActivity() {
    lateinit var bind:ActivityFavoriteBinding
    lateinit var favoriteadapter: favoriteadapter
    companion object{
        var favsongs:ArrayList<Music> =ArrayList()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPink)
        bind= ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(bind.root)
        favsongs= checkplaylist(favsongs)
        bind.backbtn.setOnClickListener { finish() }
        bind.favrv.setHasFixedSize(true)
        bind.favrv.setItemViewCacheSize(13)
        bind.favrv.layoutManager = GridLayoutManager(this,4)
        favoriteadapter = favoriteadapter(this, favsongs)
        bind.favrv.adapter = favoriteadapter
        if(favsongs.size==0)bind.shufflebtn.visibility=View.INVISIBLE
        bind.shufflebtn.setOnClickListener {
            val intent = Intent(this, player_activity::class.java)
            intent.putExtra("index",0)
            intent.putExtra("class","Favoriteshuffle")
            startActivity(intent)
        }
    }
}