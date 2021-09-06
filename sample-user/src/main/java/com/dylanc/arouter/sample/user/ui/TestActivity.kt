package com.dylanc.arouter.sample.user.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dylanc.arouter.sample.common.PATH_USER_INFO
import com.dylanc.arouter.sample.user.databinding.ActivityMainBinding
import com.dylanc.arouter.startRouterActivity
import com.dylanc.viewbinding.binding

class TestActivity : AppCompatActivity() {

  private val binding: ActivityMainBinding by binding()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    with(binding) {
      btnUserInfo.setOnClickListener {
        startRouterActivity(PATH_USER_INFO)
      }
    }
  }
}