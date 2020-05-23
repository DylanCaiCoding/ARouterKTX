package com.dylanc.arouter.helper.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.dylanc.arouter.helper.handleLogin
import kotlinx.android.synthetic.main.activity_login.*

var isLogin = false

@Route(path = "/app/login")
class LoginActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_login)
    btn_login.setOnClickListener {
      handleLogin(this)
      isLogin = true
    }
  }
}
