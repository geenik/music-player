package com.example.musicplayer

import android.media.MediaMetadataRetriever
import java.io.File
import java.util.concurrent.TimeUnit

data class Music(
    val id: String,
    val title: String,
    val album: String,
    val artist: String,
    val duration: Long = 0,
    val path: String,
    val arturi:String
)
fun formatduration(duration: Long):String{
    val minutes=TimeUnit.MINUTES.convert(duration,TimeUnit.MILLISECONDS)
    val duration2=duration% 60000;
    val seconds=TimeUnit.SECONDS.convert(duration2,TimeUnit.MILLISECONDS)
    return String.format("%02d:%02d",minutes,seconds)
}
fun getimageart(path: String):ByteArray?{
    val retriever=MediaMetadataRetriever()
    retriever.setDataSource(path)
    return retriever.embeddedPicture
}
 fun changesong(decider: Boolean) {
    if (decider) {
        if (player_activity.songposition == player_activity.musiclist.size - 1)
            player_activity.songposition = 0
        else {
            player_activity.songposition++
        }

    } else
        if (player_activity.songposition == 0)
            player_activity.songposition = player_activity.musiclist.size - 1
        else
            player_activity.songposition--

}
fun stopmusic(){
    if(player_activity.musicservice!=null){
        player_activity.musicservice!!.stopForeground(true)
        player_activity.musicservice = null
        player_activity.musicservice!!.mediaplayer!!.release()
    }
}
fun favchecker(id:String):Int{
    player_activity.isfavorite=false
    favorite_activity.favsongs.forEachIndexed{index,music->
        if(id==music.id){
            player_activity.isfavorite=true;
            return index
        }
    }
    return -1;
}
fun checkplaylist(playlist:ArrayList<Music>):ArrayList<Music>{
    playlist.forEachIndexed { index, music ->
        val file=File(music.path)
        if(!file.exists())playlist.removeAt(index)
    }
    return playlist
}