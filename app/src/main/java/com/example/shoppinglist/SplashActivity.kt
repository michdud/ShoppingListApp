package com.example.shoppinglist

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView

class SplashActivity : AppCompatActivity() {
    private lateinit var animationDrawable : AnimationDrawable

    private var splashDisplayLength : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var imageContainer = findViewById<ImageView>(R.id.imgSplash)
        imageContainer.setImageResource(R.drawable.splash_animation)

        animationDrawable = imageContainer.drawable as AnimationDrawable
        splashDisplayLength = animationDrawable.numberOfFrames * animationDrawable.getDuration(0) * 2
    }

    override fun onResume() {
        super.onResume()
        animationDrawable.start()
        checkAnimationStatus()
    }

    private fun checkAnimationStatus() {
        var handler = Handler()
        handler.postDelayed({
            var mainIntent = Intent(this@SplashActivity, ShoppingListActivity::class.java)
            startActivity(mainIntent)
            finish()
        }, splashDisplayLength.toLong())
    }
}
