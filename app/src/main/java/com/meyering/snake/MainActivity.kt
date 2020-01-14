package com.meyering.snake

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
