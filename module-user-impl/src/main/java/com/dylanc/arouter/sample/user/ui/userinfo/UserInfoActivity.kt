package com.dylanc.arouter.sample.user.ui.userinfo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.dylanc.arouter.routerServices
import com.dylanc.arouter.sample.user.databinding.ActivityUserInfoBinding
import com.dylanc.arouter.sample.user.service.PATH_USER_INFO
import com.dylanc.arouter.sample.user.service.UserService
import com.dylanc.arouter.startActivityByRouter
import com.dylanc.longan.activity
import com.dylanc.longan.finishAllActivities
import com.dylanc.viewbinding.binding

@Route(path = PATH_USER_INFO)
class UserInfoActivity : AppCompatActivity() {

  private val binding: ActivityUserInfoBinding by binding()
  private val userService: UserService? by routerServices()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding.btnLogout.setOnClickListener {
      userService?.logout(activity)
      startActivityByRouter("/app/main") {
        onArrival {
          finishAllActivities()
        }
      }
    }
  }
}