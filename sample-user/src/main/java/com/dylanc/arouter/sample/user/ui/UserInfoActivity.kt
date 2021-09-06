package com.dylanc.arouter.sample.user.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.dylanc.arouter.routerServices
import com.dylanc.arouter.sample.common.PATH_MAIN
import com.dylanc.arouter.sample.common.PATH_USER_INFO
import com.dylanc.arouter.sample.common.service.UserService
import com.dylanc.arouter.sample.user.databinding.ActivityUserInfoBinding
import com.dylanc.arouter.startRouterActivity
import com.dylanc.longan.activity
import com.dylanc.longan.finishAllActivities
import com.dylanc.viewbinding.binding

@Route(path = PATH_USER_INFO)
class UserInfoActivity : AppCompatActivity() {

  private val binding: ActivityUserInfoBinding by binding()
  private val userService: UserService by routerServices()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    with(binding) {
      btnLogout.setOnClickListener {
        userService.logout(activity)
        activity.startRouterActivity(PATH_MAIN) {
          onArrival {
            finishAllActivities()
          }
        }
      }
    }
  }
}