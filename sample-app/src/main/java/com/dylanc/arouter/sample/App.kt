package com.dylanc.arouter.sample

import android.app.Application
import com.dylanc.arouter.interceptor.LoginInterceptor
import com.dylanc.arouter.safeRouterServices
import com.dylanc.arouter.sample.base.constants.PATH_MAIN
import com.dylanc.arouter.sample.user.service.PATH_LOGIN
import com.dylanc.arouter.sample.user.service.PATH_USER_INFO
import com.dylanc.arouter.sample.user.service.UserService

/**
 * @author Dylan Cai
 */
@Suppress("unused")
class App : Application() {

  private val userService: UserService by safeRouterServices()

  override fun onCreate() {
    super.onCreate()
    LoginInterceptor.enable(PATH_LOGIN, listOf(PATH_MAIN, PATH_USER_INFO)) { userService.isLogin() }
  }
}