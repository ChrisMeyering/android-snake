package com.meyering.snake

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_main)
    }

    override fun onPause() {
        super.onPause()
        findViewById<GameView>(R.id.snakeGameView).pause()
    }

    override fun onResume() {
        super.onResume()
        findViewById<GameView>(R.id.snakeGameView).resume()
    }
}
