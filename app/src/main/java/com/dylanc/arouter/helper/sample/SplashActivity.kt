package com.dylanc.arouter.helper.sample

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.dylanc.arouter.helper.startRouterActivityCheckLogin

class SplashActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_splash)
    Handler().postDelayed({
      startRouterActivityCheckLogin("/app/main"){
        finish()
      }
    },1000)
  }
}
