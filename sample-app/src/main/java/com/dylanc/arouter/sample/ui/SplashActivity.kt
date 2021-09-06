package com.dylanc.arouter.sample.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.dylanc.arouter.sample.R
import com.dylanc.arouter.sample.common.PATH_MAIN
import com.dylanc.arouter.startRouterActivity

class SplashActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_splash)
    Handler(Looper.getMainLooper()).postDelayed({
      startRouterActivity(PATH_MAIN) {
        finishOnArrival()
      }
    }, 1000)
  }
}
