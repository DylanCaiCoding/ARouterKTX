package com.dylanc.arouter.sample.user.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.dylanc.arouter.sample.user.databinding.ActivityLoginBinding
import com.dylanc.arouter.loginSuccess
import com.dylanc.arouter.routerServices
import com.dylanc.arouter.sample.common.PATH_LOGIN
import com.dylanc.arouter.sample.common.service.UserService
import com.dylanc.arouter.sample.user.R
import com.dylanc.longan.fragmentActivity
import com.dylanc.longan.toast
import com.dylanc.viewbinding.binding

@Route(path = PATH_LOGIN)
class LoginActivity : AppCompatActivity() {

  private val binding: ActivityLoginBinding by binding()
  private val userService: UserService by routerServices()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_login)
    with(binding) {
      btnLogin.setOnClickListener {
        val username = etUsername.text.toString()
        val password = etPassword.text.toString()
        userService.login(fragmentActivity, username, password, {
          loginSuccess() // 使用拦截功能必须在登录后调用该方法
          toast("login success")
        })
      }
    }
  }
}