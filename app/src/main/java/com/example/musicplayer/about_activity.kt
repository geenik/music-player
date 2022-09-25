package com.example.musicplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayer.databinding.ActivityAboutBinding

class about_activity : AppCompatActivity() {
    lateinit var bind:ActivityAboutBinding
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPinkNav)
        bind= ActivityAboutBinding.inflate(layoutInflater)
        setContentView(bind.root)
        bind.abouttext.text="Developed By: Nikhil Saini"+
                "\n\nIf you want to provide feedback, I will \nlove to hear that."
    }
}