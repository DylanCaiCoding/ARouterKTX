package com.dylanc.arouter.sample.user.ui.userinfo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.dylanc.arouter.safeRouterServices
import com.dylanc.arouter.sample.user.databinding.ActivityUserInfoBinding
import com.dylanc.arouter.sample.user.service.PATH_USER_INFO
import com.dylanc.arouter.sample.user.service.UserService
import com.dylanc.longan.activity
import com.dylanc.viewbinding.binding

@Route(path = PATH_USER_INFO)
class UserInfoActivity : AppCompatActivity() {

  private val binding: ActivityUserInfoBinding by binding()
  private val userService: UserService by safeRouterServices()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding.btnLogout.setOnClickListener {
      userService.logout(activity)
//      startRouterActivity(PATH_MAIN) {
//        onArrival {
//          finishAllActivities()
//        }
//      }
    }
  }
}