package com.dylanc.arouter.sample.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.alibaba.android.arouter.facade.annotation.Route
import com.dylanc.arouter.doAfterLogin
import com.dylanc.arouter.routerServices
import com.dylanc.arouter.sample.R
import com.dylanc.arouter.sample.common.PATH_MAIN
import com.dylanc.arouter.sample.common.PATH_PAYMENT
import com.dylanc.arouter.sample.common.PATH_USER_INFO
import com.dylanc.arouter.sample.common.service.UserService
import com.dylanc.arouter.sample.databinding.ActivityMainBinding
import com.dylanc.arouter.startRouterActivity
import com.dylanc.viewbinding.binding

@Route(path = PATH_MAIN)
class MainActivity : AppCompatActivity() {

  private val binding: ActivityMainBinding by binding()
  private val userService: UserService by routerServices()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    with(binding) {
      setSupportActionBar(toolbar)
      if (userService.isLogin()) {
        tvUsername.text = userService.username
      } else {
        tvUsername.setText(R.string.login)
      }

      tvUsername.setOnClickListener {
        if (tvUsername.text.toString() != getString(R.string.login)) {
          startRouterActivity(PATH_USER_INFO)
        } else {
          doAfterLogin {
            tvUsername.text = userService.username
          }
        }
      }

      btnPay.setOnClickListener {
        startRouterActivity(PATH_PAYMENT)
      }
    }
  }
}