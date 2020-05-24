package com.dylanc.arouter.helper.sample.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.dylanc.arouter.helper.routerServiceOf
import com.dylanc.arouter.helper.sample.R
import com.dylanc.arouter.helper.sample.common.PATH_MAIN
import com.dylanc.arouter.helper.sample.common.service.UserService
import kotlinx.android.synthetic.main.activity_main.*

@Route(path = PATH_MAIN)
class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    setSupportActionBar(toolbar)
    btn_logout.setOnClickListener {
      routerServiceOf<UserService>().logout(this)
    }
  }
}
