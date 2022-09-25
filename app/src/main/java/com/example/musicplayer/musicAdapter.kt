package com.example.musicplayer

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.databinding.MusicViewBinding

class musicAdapter(private val context: Context, private var musiclist: ArrayList<Music>) :
    RecyclerView.Adapter<musicAdapter.Myholder>() {
    class Myholder(binding: MusicViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.songname
        val album = binding.songalbum
        val image = binding.imageMV
        val duration = binding.songduration
        val root=binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): musicAdapter.Myholder {
        return Myholder(MusicViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: musicAdapter.Myholder, position: Int) {
        holder.title.text = musiclist[position].title
        holder.album.text = musiclist[position].album
        holder.duration.text = formatduration(musiclist[position].duration)
        Glide.with(context).load(musiclist[position].arturi)
            .apply(RequestOptions().placeholder(R.drawable.music_icon_splash)).into(holder.image)
        holder.root.setOnClickListener {
          if(MainActivity.search)sendintent("MusicAdapterSearch",position)
          else  if(musiclist[position].id == player_activity.nowplaying) sendintent("Nowplaying",player_activity.songposition)
            else sendintent("musicAdapter",position)
        }
    }

    override fun getItemCount(): Int {
        return musiclist.size
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updatelist(arrayList: ArrayList<Music>){
        musiclist= ArrayList()
        musiclist.addAll(arrayList)
        notifyDataSetChanged()
    }
    private fun sendintent(ref:String,pos:Int){
        val intent= Intent(context,player_activity::class.java)
        intent.putExtra("index",pos)
        intent.putExtra("class",ref)
        ContextCompat.startActivity(context,intent,null)
    }
}