package com.example.musicplayer

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.databinding.FavoriteviewBinding

class favoriteadapter(private val context: Context, private var musiclist: ArrayList<Music>) :
    RecyclerView.Adapter<favoriteadapter.Myholder>() {
    class Myholder(binding: FavoriteviewBinding) : RecyclerView.ViewHolder(binding.root) {
      val image=binding.imageView
        val name=binding.name
        val root=binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): favoriteadapter.Myholder {
        return Myholder(FavoriteviewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: favoriteadapter.Myholder, position: Int) {
       holder.name.text=musiclist[position].title
        Glide.with(context).load(musiclist[position].arturi)
            .apply(RequestOptions().placeholder(R.drawable.music_icon_splash)).into(holder.image)
        holder.root.setOnClickListener {
            val intent=Intent(context,player_activity::class.java)
            intent.putExtra("index",position)
            intent.putExtra("class","FavoriteAdapter")
            ContextCompat.startActivity(context,intent,null)
        }
    }

    override fun getItemCount(): Int {
        return musiclist.size
    }

}