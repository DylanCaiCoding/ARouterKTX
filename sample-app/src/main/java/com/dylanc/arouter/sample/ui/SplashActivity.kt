package com.dylanc.arouter.sample.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.dylanc.arouter.sample.R
import com.dylanc.arouter.sample.constant.PATH_MAIN
import com.dylanc.arouter.startActivityByRouter

class SplashActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_splash)
    Handler(Looper.getMainLooper()).postDelayed({
      startActivityByRouter(PATH_MAIN) {
        onArrival { finish() }
      }
    }, 1000)
  }
}
