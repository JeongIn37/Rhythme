package com.cs496.rhythm

import android.content.Intent
import android.os.Handler
import com.airbnb.lottie.LottieAnimationView
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        var loadingImage = findViewById(R.id.loading_image) as LottieAnimationView

        loadingImage.playAnimation()

        val handler: Handler = Handler()
        handler.postDelayed({
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
            finish()
        },3000)
    }
}