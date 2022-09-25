package com.example.musicplayer

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat

class Musicservice:Service(),AudioManager.OnAudioFocusChangeListener {
    private var mybinder=Mybinder()
    var mediaplayer:MediaPlayer?=null
    private lateinit var mediasession:MediaSessionCompat
    lateinit var audioManager: AudioManager
    override fun onBind(p0: Intent?): IBinder? {
        mediasession= MediaSessionCompat(baseContext,"mysession")
        return mybinder
    }
    inner class Mybinder:Binder(){
        fun currentservice():Musicservice{
            return this@Musicservice
        }
    }
    fun shownotification(playpausebtn:Int){
        val intent=Intent(baseContext,MainActivity::class.java)
        intent.putExtra("index",player_activity.songposition)
        intent.putExtra("class","Nowplaying")
        val contentIntent=PendingIntent.getActivity(this,0,intent,0)

        val previntent=Intent(baseContext,Notificationreciever::class.java).setAction(applicationclass.PREVIOUS)
        val prevpendingintent=PendingIntent.getBroadcast(baseContext,0,previntent,PendingIntent.FLAG_UPDATE_CURRENT)

        val nextintent=Intent(baseContext,Notificationreciever::class.java).setAction(applicationclass.NEXT)
        val nextpendingintent=PendingIntent.getBroadcast(baseContext,0,nextintent,PendingIntent.FLAG_UPDATE_CURRENT)

        val playintent=Intent(baseContext,Notificationreciever::class.java).setAction(applicationclass.PLAY)
        val playpendingintent=PendingIntent.getBroadcast(baseContext,0,playintent,PendingIntent.FLAG_UPDATE_CURRENT)

        val exitintent=Intent(baseContext,Notificationreciever::class.java).setAction(applicationclass.EXIT)
        val exitpendingintent=PendingIntent.getBroadcast(baseContext,0,exitintent,PendingIntent.FLAG_UPDATE_CURRENT)

        val imgart= getimageart(player_activity.musiclist[player_activity.songposition].path)
        val image= if(imgart!=null){
            BitmapFactory.decodeByteArray(imgart,0,imgart.size)
        }else
        {
            BitmapFactory.decodeResource(resources,R.drawable.music_icon_splash)
        }

     val notification=NotificationCompat.Builder(baseContext,applicationclass.CHANNEL_ID)
         .setContentIntent(contentIntent)
         .setContentTitle(player_activity.musiclist[player_activity.songposition].title)
         .setContentText(player_activity.musiclist[player_activity.songposition].artist)
         .setSmallIcon(R.drawable.music_icon)
         .setLargeIcon(image)
         .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediasession.sessionToken))
         .setPriority(NotificationCompat.PRIORITY_HIGH)
         .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
         .setOnlyAlertOnce(true)
         .addAction(R.drawable.previous_arrow,"previous",prevpendingintent)
         .addAction(playpausebtn,"play",playpendingintent)
         .addAction(R.drawable.ic_baseline_navigate_next_24,"next",nextpendingintent)
         .addAction(R.drawable.ic_baseline_close_24,"exit",exitpendingintent)
         .build()

        startForeground(13,notification)
    }
     fun createmediaplayer() {
        try {
            if (player_activity.musicservice!!.mediaplayer == null) player_activity.musicservice!!.mediaplayer = MediaPlayer()
            player_activity.musicservice!!.mediaplayer!!.reset()
            player_activity.musicservice!!.mediaplayer!!.setDataSource(player_activity.musiclist[player_activity.songposition].path)
            player_activity.musicservice!!.mediaplayer!!.prepare()
            player_activity.musicservice!!.shownotification(R.drawable.ic_baseline_pause_24)
            player_activity.bind.playpausebtn.setIconResource(R.drawable.ic_baseline_pause_24)
            player_activity.bind.playpausebtn.setIconResource(R.drawable.ic_baseline_pause_24)
            player_activity.bind.currenttime.text= formatduration(player_activity.musicservice!!.mediaplayer!!.currentPosition.toLong())
            player_activity.bind.endtime.text= formatduration(player_activity.musicservice!!.mediaplayer!!.duration.toLong())
            player_activity.bind.seekbar.max= player_activity.musicservice!!.mediaplayer!!.duration
            player_activity.nowplaying = player_activity.musiclist[player_activity.songposition].id
        } catch (e: Exception) {
            return
        }
    }

    override fun onAudioFocusChange(p0: Int) {
        if(p0<=0){
            player_activity.bind.playpausebtn.setIconResource(R.drawable.ic_baseline_play_arrow_24)
            Nowplaying.binding.playpausebtnnp.setIconResource(R.drawable.ic_baseline_play_arrow_24)
            shownotification(R.drawable.ic_baseline_play_arrow_24)
            player_activity.isplaying = false
            mediaplayer!!.pause()
        }else{
            player_activity.bind.playpausebtn.setIconResource(R.drawable.ic_baseline_pause_24)
            Nowplaying.binding.playpausebtnnp.setIconResource(R.drawable.ic_baseline_pause_24)
            shownotification(R.drawable.ic_baseline_pause_24)
            player_activity.isplaying = true
            mediaplayer!!.start()
        }
    }
}