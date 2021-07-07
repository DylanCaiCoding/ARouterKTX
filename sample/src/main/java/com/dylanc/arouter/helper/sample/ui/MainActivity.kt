package com.dylanc.arouter.helper.sample.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.dylanc.arouter.helper.executeNeedLogin
import com.dylanc.arouter.helper.routerServices
import com.dylanc.arouter.helper.sample.R
import com.dylanc.arouter.helper.sample.common.PATH_MAIN
import com.dylanc.arouter.helper.sample.common.PATH_PAYMENT
import com.dylanc.arouter.helper.sample.common.PATH_USER_INFO
import com.dylanc.arouter.helper.sample.common.service.UserService
import com.dylanc.arouter.helper.sample.databinding.ActivityMainBinding
import com.dylanc.arouter.helper.startRouterActivity
import com.dylanc.arouter.helper.startRouterActivityNeedLogin
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
          startRouterActivityNeedLogin(PATH_USER_INFO)
        } else {
          executeNeedLogin {
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