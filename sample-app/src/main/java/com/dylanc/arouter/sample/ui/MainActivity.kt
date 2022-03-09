package com.dylanc.arouter.sample.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.dylanc.arouter.requireLoginLauncher
import com.dylanc.arouter.routerServices
import com.dylanc.arouter.sample.R
import com.dylanc.arouter.sample.constant.PATH_MAIN
import com.dylanc.arouter.sample.databinding.ActivityMainBinding
import com.dylanc.arouter.sample.payment.service.PATH_PAYMENT
import com.dylanc.arouter.sample.user.service.PATH_USER_INFO
import com.dylanc.arouter.sample.user.service.UserService
import com.dylanc.arouter.startActivityByRouter
import com.dylanc.viewbinding.binding

@Route(path = PATH_MAIN)
class MainActivity : AppCompatActivity() {

  private val binding: ActivityMainBinding by binding()
  private val userService: UserService? by routerServices()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    with(binding) {
      setSupportActionBar(toolbar)
      if (userService?.isLogin() == true) {
        tvUsername.text = userService?.username
      } else {
        tvUsername.setText(R.string.login)
      }

      tvUsername.setOnClickListener {
        if (tvUsername.text.toString() != getString(R.string.login)) {
          startActivityByRouter(PATH_USER_INFO)
        } else {
          changeUsernameLauncher.launch(Unit)
        }
      }

      btnPay.setOnClickListener {
        startActivityByRouter(PATH_PAYMENT)
      }
    }
  }

  private val changeUsernameLauncher = requireLoginLauncher {
      binding.tvUsername.text = userService?.username
  }

}