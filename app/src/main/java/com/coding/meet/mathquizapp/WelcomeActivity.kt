package com.coding.meet.mathquizapp

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.coding.meet.mathquizapp.util.setupDialog
import kotlin.system.exitProcess

class WelcomeActivity : AppCompatActivity() {
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            onBackPressedMethod()
        }
    }

    private val exitDialog : Dialog by lazy {
        Dialog(this,R.style.DialogCustomTheme).apply {
            setupDialog(R.layout.game_exit_dialog)
        }
    }
    private val tickMusic: MediaPlayer by lazy {
        MediaPlayer.create(this, R.raw.tick)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)

        // here setKeepOnScreenCondition true so, activity redirect another activity
        // and some api call here
        // if setKeepOnScreenCondition false so, activity code not redirect another activity
        splashScreen.setKeepOnScreenCondition { false }


        val startBtn = findViewById<Button>(R.id.startBtn)
        startBtn.startAnimation(
            AnimationUtils.loadAnimation(this,R.anim.zoom_in_cut)
        )
        startBtn.setOnClickListener {
            tickMusic.start()
            startActivity(Intent(this,LevelActivity::class.java))
        }


        val yesBtn : Button = exitDialog.findViewById(R.id.yesBtn)
        val noBtn : Button = exitDialog.findViewById(R.id.noBtn)
        yesBtn.setOnClickListener {
            tickMusic.start()
            moveTaskToBack(true)
            android.os.Process.killProcess(android.os.Process.myPid())
            exitProcess(1)
        }
        noBtn.setOnClickListener {
            tickMusic.start()
            exitDialog.dismiss()
        }

    }
    private fun onBackPressedMethod() {
        exitDialog.show()
    }
}