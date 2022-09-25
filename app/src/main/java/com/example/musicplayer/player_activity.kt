package com.example.musicplayer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayer.databinding.ActivityPlayerBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class player_activity : AppCompatActivity(),ServiceConnection,MediaPlayer.OnCompletionListener {
    companion object {
        lateinit var musiclist: ArrayList<Music>
        var songposition: Int = 0
        //var mediaplayer: MediaPlayer? = null
        var isplaying = false
        var musicservice:Musicservice?=null
        lateinit var bind: ActivityPlayerBinding
        lateinit var runnable: Runnable
        var repeat:Boolean=false
        var min15:Boolean=false
        var min30:Boolean=false
        var min60:Boolean=false
        var nowplaying:String=""
        var isfavorite:Boolean=false
        var findex=-1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPink)
        bind = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(bind.root)
        songposition = intent.getIntExtra("index", 0)
        when (intent.getStringExtra("class")) {
            "Favoriteshuffle"->{
                val intenti= Intent(this, Musicservice::class.java)
                bindService(intenti,this, BIND_AUTO_CREATE)
                startService(intenti)
                musiclist= ArrayList()
                musiclist.addAll(favorite_activity.favsongs)
                musiclist.shuffle()
                setlayout()
            }
            "FavoriteAdapter"->{
                val intenti= Intent(this, Musicservice::class.java)
                bindService(intenti,this, BIND_AUTO_CREATE)
                startService(intenti)
                musiclist= ArrayList()
                musiclist.addAll(favorite_activity.favsongs)
                setlayout()
            }
            "Nowplaying"->{
                setlayout()
                bind.currenttime.text= formatduration(musicservice!!.mediaplayer!!.currentPosition.toLong())
                bind.seekbar.progress=musicservice!!.mediaplayer!!.currentPosition
                bind.endtime.text= formatduration(musicservice!!.mediaplayer!!.duration.toLong())
                bind.seekbar.max=musicservice!!.mediaplayer!!.duration
            }
            "MusicAdapterSearch"->{
                //service starting
                var intenti= Intent(this, Musicservice::class.java)
                bindService(intenti,this, BIND_AUTO_CREATE)
                startService(intenti)
                musiclist= ArrayList()
                musiclist=MainActivity.musicsearchlist
                setlayout()
            }
            "musicAdapter" -> {
                //service starting
                var intenti= Intent(this, Musicservice::class.java)
                bindService(intenti,this, BIND_AUTO_CREATE)
                startService(intenti)
                musiclist = ArrayList()
                musiclist.addAll(MainActivity.musiclistMA)
                setlayout()
            }
            "Mainactivity"->{
                //service starting
                var intenti= Intent(this, Musicservice::class.java)
                bindService(intenti,this, BIND_AUTO_CREATE)
                startService(intenti)
                musiclist = ArrayList()
                musiclist.addAll(MainActivity.musiclistMA)
                musiclist.shuffle()
                setlayout()
            }

        }
        bind.playpausebtn.setOnClickListener {
            if (isplaying) pause()
            else play()
        }
        bind.nextbtn.setOnClickListener {
           changesong(true)
            setlayout()
           createmediaplayer()
        }
        bind.prevbtn.setOnClickListener {
          changesong(false)
            setlayout()
           createmediaplayer()
        }
        bind.backbtn.setOnClickListener {
            finish()
        }
        bind.seekbar.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if(p2)musicservice!!.mediaplayer!!.seekTo(p1)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) =Unit

            override fun onStopTrackingTouch(p0: SeekBar?) =Unit

        })
        bind.repeatbtn.setOnClickListener{
            if(repeat)
            {
                repeat=false
                bind.repeatbtn.setColorFilter(ContextCompat.getColor(this,R.color.cool_pink))
            }else
            {
                repeat=true
                bind.repeatbtn.setColorFilter(ContextCompat.getColor(this,R.color.purple_500))
            }
        }
        bind.equalizer.setOnClickListener {
            val eqintent=Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
            eqintent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, musicservice!!.mediaplayer!!.audioSessionId)
            eqintent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME,baseContext.packageName)
            eqintent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE,AudioEffect.CONTENT_TYPE_MUSIC)
            startActivityForResult(eqintent,15)
        }
        bind.timerbtn.setOnClickListener {
            var timer= min15|| min30|| min60
            if(!timer)showBottomSheetDialog()
            else{
                val builder= MaterialAlertDialogBuilder(this)
                builder.setTitle("Stop timer")
                    .setMessage("Do you want to stop the timer ?")
                    .setPositiveButton("yes"){_, _ ->
                        min15=false
                        min30=false
                        min60=false
                        bind.timerbtn.setColorFilter(ContextCompat.getColor(this,R.color.purple_500))
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
        bind.sharebtn.setOnClickListener {
            val intent=Intent().apply {
                action=Intent.ACTION_SEND
                type="audio/*"
                putExtra(Intent.EXTRA_STREAM,Uri.parse(musiclist[songposition].path))

            }
            startActivity(Intent.createChooser(intent,"Share Music File"))
        }
        bind.favoritebtn.setOnClickListener {
            if(isfavorite){
                isfavorite=false
                bind.favoritebtn.setImageResource(R.drawable.favorite_empty)
                favorite_activity.favsongs.removeAt(findex)
            }
            else{
                isfavorite=true
                bind.favoritebtn.setImageResource(R.drawable.favorite_icon)
                favorite_activity.favsongs.add(musiclist[songposition])
            }
        }
    }

    fun setlayout() {
        findex= favchecker(musiclist[songposition].id)
        Glide.with(applicationContext).load(musiclist[songposition].arturi)
            .apply(RequestOptions().placeholder(R.drawable.music_icon_splash)).into(bind.songimage)
        if(isplaying)bind.playpausebtn.setIconResource(R.drawable.ic_baseline_pause_24)
        else bind.playpausebtn.setIconResource(R.drawable.ic_baseline_play_arrow_24)
        bind.songname.text = musiclist[songposition].title
        if(min15|| min30|| min60) bind.timerbtn.setColorFilter(ContextCompat.getColor(this,R.color.purple_500))
        if(isfavorite) bind.favoritebtn.setImageResource(R.drawable.favorite_icon)
        else bind.favoritebtn.setImageResource(R.drawable.favorite_empty)
    }

    fun createmediaplayer() {
        try {
            if (musicservice!!.mediaplayer == null)musicservice!!.mediaplayer = MediaPlayer()
            musicservice!!.mediaplayer!!.reset()
            musicservice!!.mediaplayer!!.setDataSource(player_activity.musiclist[player_activity.songposition].path)
            musicservice!!.mediaplayer!!.prepare()
            musicservice!!.mediaplayer!!.start()
            isplaying = true
            musicservice!!.shownotification(R.drawable.ic_baseline_pause_24)
            bind.playpausebtn.setIconResource(R.drawable.ic_baseline_pause_24)
            bind.currenttime.text= formatduration(musicservice!!.mediaplayer!!.currentPosition.toLong())
            bind.endtime.text= formatduration(musicservice!!.mediaplayer!!.duration.toLong())
            bind.seekbar.max= musicservice!!.mediaplayer!!.duration
            musicservice!!.mediaplayer!!.setOnCompletionListener(this)
            nowplaying= musiclist[songposition].id

        } catch (e: Exception) {
            return
        }
    }

    private fun pause() {
        bind.playpausebtn.setIconResource(R.drawable.ic_baseline_play_arrow_24)
        musicservice!!.shownotification(R.drawable.ic_baseline_play_arrow_24)
        isplaying = false
        musicservice!!.mediaplayer!!.pause()
    }

    private fun play() {
        bind.playpausebtn.setIconResource(R.drawable.ic_baseline_pause_24)
        musicservice!!.shownotification(R.drawable.ic_baseline_pause_24)
        isplaying = true
        musicservice!!.mediaplayer!!.start()
    }



    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
        p1 as Musicservice.Mybinder
        musicservice=p1.currentservice()
        createmediaplayer()
        seeksetup()
        musicservice?.audioManager=getSystemService(Context.AUDIO_SERVICE) as AudioManager
        musicservice?.audioManager?.requestAudioFocus(musicservice,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN)

    }

    override fun onServiceDisconnected(p0: ComponentName?) {
       musicservice=null
    }
    fun seeksetup(){
        runnable= Runnable {
            bind.currenttime.text= formatduration(musicservice!!.mediaplayer!!.currentPosition.toLong())
            bind.seekbar.progress=musicservice!!.mediaplayer!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable,200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable,0)
    }

    override fun onCompletion(p0: MediaPlayer?) {
        if(!repeat)
        changesong(true)
        createmediaplayer()
        setlayout()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== RESULT_OK && requestCode==15)return
    }
    private fun showBottomSheetDialog(){
        val dialog= BottomSheetDialog(this)
        dialog.setContentView(R.layout.bottomdialogsheet)
        dialog.show()
        dialog.findViewById<TextView>(R.id.mintimer15)?.setOnClickListener{
            Toast.makeText(baseContext,"Music will stop After 15 MIN",Toast.LENGTH_SHORT).show()
            bind.timerbtn.setColorFilter(ContextCompat.getColor(this,R.color.purple_500))
            min15=true
            Thread{
                Thread.sleep(15*60000)
                if(min15)
                    stopmusic()
                    dialog.dismiss()
            }.start()
        }
        dialog.findViewById<TextView>(R.id.mintimer30)?.setOnClickListener{
            Toast.makeText(baseContext,"Music will stop After 15 MIN",Toast.LENGTH_SHORT).show()
            bind.timerbtn.setColorFilter(ContextCompat.getColor(this,R.color.purple_500))
            min30=true
            Thread{
                Thread.sleep(30*60000)
                if(min30)
                    stopmusic()
                dialog.dismiss()
            }.start()
        }
        dialog.findViewById<TextView>(R.id.mintimer60)?.setOnClickListener{
            Toast.makeText(baseContext,"Music will stop After 15 MIN",Toast.LENGTH_SHORT).show()
            bind.timerbtn.setColorFilter(ContextCompat.getColor(this,R.color.purple_500))
            min60=true
            Thread{
                Thread.sleep(60*60000)
                if(min60)
                    stopmusic()
                dialog.dismiss()
            }.start()
        }
    }
}