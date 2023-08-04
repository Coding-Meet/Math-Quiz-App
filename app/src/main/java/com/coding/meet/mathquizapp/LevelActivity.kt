package com.coding.meet.mathquizapp

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.coding.meet.mathquizapp.adapters.LevelAdapter
import com.coding.meet.mathquizapp.databinding.ActivityLevelBinding
import com.coding.meet.mathquizapp.util.SharedPreferenceManger

class LevelActivity : AppCompatActivity() {
    private val levelBinding: ActivityLevelBinding by lazy {
        ActivityLevelBinding.inflate(layoutInflater)
    }
    private val tickMusic: MediaPlayer by lazy {
        MediaPlayer.create(this, R.raw.tick)
    }
    private val sharedPreferenceManger: SharedPreferenceManger by lazy {
        SharedPreferenceManger(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(levelBinding.root)

        levelBinding.toolbarLayout.titleTxt.text = "Levels"
        levelBinding.toolbarLayout.backImg.setOnClickListener {
            tickMusic.start()
            finish() }
        levelBinding.levelRV.adapter = LevelAdapter(this, sharedPreferenceManger, tickMusic)
    }

    override fun onResume() {
        super.onResume()
        levelBinding.levelRV.adapter!!.notifyDataSetChanged()
    }
}