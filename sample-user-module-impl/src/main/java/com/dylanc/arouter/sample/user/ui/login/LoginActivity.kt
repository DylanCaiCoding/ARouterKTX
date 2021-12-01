package com.dylanc.arouter.sample.user.ui.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.dylanc.arouter.loginSuccess
import com.dylanc.arouter.sample.user.service.PATH_LOGIN
import com.dylanc.arouter.sample.user.databinding.ActivityLoginBinding
import com.dylanc.longan.doOnClick
import com.dylanc.longan.toast
import com.dylanc.retrofit.coroutines.requestViewModels
import com.dylanc.viewbinding.binding

@Route(path = PATH_LOGIN)
class LoginActivity : AppCompatActivity() {

  private val binding: ActivityLoginBinding by binding()
  private val viewModel: LoginViewModel by requestViewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding.btnLogin.doOnClick {
      val username = binding.etUsername.text.toString()
      val password = binding.etPassword.text.toString()
      viewModel.login(username, password)
        .observe(this) {
          toast("login success")
          loginSuccess()
        }
    }
  }
}