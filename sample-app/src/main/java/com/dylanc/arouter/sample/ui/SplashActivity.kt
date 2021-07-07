package com.dylanc.arouter.sample.ui

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.dylanc.arouter.sample.R
import com.dylanc.arouter.sample.common.startMainActivity

class SplashActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_splash)
    Handler().postDelayed({
      startMainActivity()
    }, 1000)
  }
}
