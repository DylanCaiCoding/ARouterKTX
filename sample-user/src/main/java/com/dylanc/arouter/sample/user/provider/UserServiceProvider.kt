package com.dylanc.arouter.sample.user.provider

import android.app.Activity
import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.dylanc.arouter.sample.user.service.GROUP_USER
import com.dylanc.arouter.sample.user.service.UserService
import com.dylanc.arouter.sample.user.repository.UserRepository

/**
 * @author Dylan Cai
 * @since 2020/5/24
 */
@Route(path = "$GROUP_USER/service")
class UserServiceProvider : UserService {

  override fun login(username: String, password: String) =
    UserRepository.login(username, password)

  override fun logout(activity: Activity) {
    UserRepository.logout()
  }

  override fun isLogin(): Boolean =
    UserRepository.isLogin()

  override val username: String?
    get() = UserRepository.user?.name

  override fun init(context: Context) {}
}