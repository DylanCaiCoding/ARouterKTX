package com.dylanc.arouter.helper.sample.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.dylanc.arouter.helper.sample.common.PATH_LOGIN
import com.dylanc.arouter.helper.handleLogin
import com.dylanc.arouter.helper.routerServiceOf
import com.dylanc.arouter.helper.sample.R
import com.dylanc.arouter.helper.sample.common.service.UserService
import com.dylanc.utilktx.toast
import kotlinx.android.synthetic.main.activity_login.*

@Route(path = PATH_LOGIN)
class LoginActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_login)
    btn_login.setOnClickListener {
      val username = et_username.text.toString()
      val password = et_password.text.toString()
      routerServiceOf<UserService>()
        .login(this, username, password, {
          handleLogin(this)
          toast("登录成功")
        })
    }
  }
}
