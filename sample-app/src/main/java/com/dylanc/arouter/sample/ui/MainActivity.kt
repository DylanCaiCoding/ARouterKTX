package com.dylanc.arouter.sample.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.dylanc.arouter.activityresult.requireLoginLauncher
import com.dylanc.arouter.safeRouterServices
import com.dylanc.arouter.sample.R
import com.dylanc.arouter.sample.constant.PATH_MAIN
import com.dylanc.arouter.sample.databinding.ActivityMainBinding
import com.dylanc.arouter.sample.payment.service.PATH_PAYMENT
import com.dylanc.arouter.sample.user.service.PATH_USER_INFO
import com.dylanc.arouter.sample.user.service.UserService
import com.dylanc.arouter.startRouterActivity
import com.dylanc.viewbinding.binding

@Route(path = PATH_MAIN)
class MainActivity : AppCompatActivity() {

  private val binding: ActivityMainBinding by binding()
  private val userService: UserService by safeRouterServices()

  //  private val startRouterActivityLauncher = startRouterActivityLauncher {
//    if (it.resultCode == RESULT_OK) {
//      toast("nice")
//    }
//  }
  private val requireLoginLauncher = requireLoginLauncher {
    if (it) {
      binding.tvUsername.text = userService.username
    }
  }

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
          requireLoginLauncher.launch(Unit)
//          tvUsername.text = userService.username
        }

//        startRouterActivityLauncher.launch(PATH_LOGIN)
      }

      btnPay.setOnClickListener {
        startRouterActivity(PATH_PAYMENT)
      }
    }
  }
}