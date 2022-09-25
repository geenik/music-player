package com.example.musicplayer

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.databinding.FragmentNowplayingBinding


class Nowplaying : Fragment() {
   companion object{
       lateinit var binding:FragmentNowplayingBinding
   }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       val view=inflater.inflate(R.layout.fragment_nowplaying, container, false)
       binding= FragmentNowplayingBinding.bind(view)
        binding.root.visibility=View.INVISIBLE
        binding.playpausebtnnp.setOnClickListener {
            if(player_activity.isplaying)pausemusic()
            else playmusic()
        }
        binding.root.setOnClickListener {
            val intent= Intent(requireContext(),player_activity::class.java)
            intent.putExtra("index",player_activity.songposition)
            intent.putExtra("class","Nowplaying")
            ContextCompat.startActivity(requireContext(),intent,null)
        }
        binding.nextbtnnp.setOnClickListener {
            changesong(true)
            player_activity.musicservice!!.createmediaplayer()
            Glide.with(this).load(player_activity.musiclist[player_activity.songposition].arturi)
                .apply(RequestOptions().placeholder(R.drawable.music_icon_splash)).into(player_activity.bind.songimage)
            player_activity.bind.songname.text = player_activity.musiclist[player_activity.songposition].title
            playmusic()
            Glide.with(this).load(player_activity.musiclist[player_activity.songposition].arturi)
                .apply(RequestOptions().placeholder(R.drawable.music_icon_splash)).into(
                    binding.songimgnp)
            binding.songnamenp.text=player_activity.musiclist[player_activity.songposition].title
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        if(player_activity.musicservice!=null){
            binding.songnamenp.isSelected=true
            binding.root.visibility=View.VISIBLE
            Glide.with(this).load(player_activity.musiclist[player_activity.songposition].arturi)
                .apply(RequestOptions().placeholder(R.drawable.music_icon_splash)).into(
                    binding.songimgnp)
            binding.songnamenp.text=player_activity.musiclist[player_activity.songposition].title
            if(player_activity.isplaying) binding.playpausebtnnp.setIconResource(R.drawable.ic_baseline_pause_24)
            else binding.playpausebtnnp.setIconResource(R.drawable.ic_baseline_play_arrow_24)
        }
    }
    private fun playmusic(){
        player_activity.musicservice?.mediaplayer?.start()
        binding.playpausebtnnp.setIconResource(R.drawable.ic_baseline_pause_24)
        player_activity.musicservice?.shownotification(R.drawable.ic_baseline_pause_24)
        player_activity.bind.playpausebtn.setIconResource(R.drawable.ic_baseline_pause_24)
        player_activity.isplaying=true
    }
    private fun pausemusic(){
        player_activity.musicservice?.mediaplayer?.pause()
        binding.playpausebtnnp.setIconResource(R.drawable.ic_baseline_play_arrow_24)
        player_activity.musicservice?.shownotification(R.drawable.ic_baseline_play_arrow_24)
        player_activity.bind.playpausebtn.setIconResource(R.drawable.ic_baseline_play_arrow_24)
        player_activity.isplaying=false
    }

}