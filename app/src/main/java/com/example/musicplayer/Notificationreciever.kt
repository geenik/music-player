package com.example.musicplayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlin.system.exitProcess

class Notificationreciever:BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        when(p1?.action){
            applicationclass.NEXT->prevnext(true,p0!!)
            applicationclass.PREVIOUS->prevnext(false,p0!!)
            applicationclass.PLAY->if(player_activity.isplaying)pausemusic()else playmusic()
            applicationclass.EXIT -> {
                stopmusic()
                exitProcess(1)
            }
        }
    }
    private fun playmusic(){
        player_activity.isplaying=true
        player_activity.musicservice!!.mediaplayer!!.start()
        player_activity.musicservice!!.shownotification(R.drawable.ic_baseline_pause_24)
        player_activity.bind.playpausebtn.setIconResource(R.drawable.ic_baseline_pause_24)
        Nowplaying.binding.playpausebtnnp.setIconResource(R.drawable.ic_baseline_pause_24)
    }
    private fun pausemusic(){
        player_activity.isplaying=false
        player_activity.musicservice!!.mediaplayer!!.pause()
        player_activity.musicservice!!.shownotification(R.drawable.ic_baseline_play_arrow_24)
        player_activity.bind.playpausebtn.setIconResource(R.drawable.ic_baseline_play_arrow_24)
        Nowplaying.binding.playpausebtnnp.setIconResource(R.drawable.ic_baseline_play_arrow_24)
    }
    private fun prevnext(decider:Boolean,context: Context){
        changesong(decider)
        player_activity.musicservice!!.createmediaplayer()
        Glide.with(context).load(player_activity.musiclist[player_activity.songposition].arturi)
            .apply(RequestOptions().placeholder(R.drawable.music_icon_splash)).into(player_activity.bind.songimage)
        player_activity.bind.songname.text = player_activity.musiclist[player_activity.songposition].title
        playmusic()
        Glide.with(context).load(player_activity.musiclist[player_activity.songposition].arturi)
            .apply(RequestOptions().placeholder(R.drawable.music_icon_splash)).into(
                Nowplaying.binding.songimgnp)
        Nowplaying.binding.songnamenp.text=player_activity.musiclist[player_activity.songposition].title
        player_activity.findex = favchecker(player_activity.musiclist[player_activity.songposition].id)
        if(player_activity.isfavorite) player_activity.bind.favoritebtn.setImageResource(R.drawable.favorite_icon)
        else player_activity.bind.favoritebtn.setImageResource(R.drawable.favorite_empty)
    }
}