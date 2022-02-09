package com.dylanc.arouter.sample

import android.app.Application
import com.dylanc.arouter.interceptor.LoginInterceptor
import com.dylanc.arouter.routerServices
import com.dylanc.arouter.sample.user.service.UserService

/**
 * @author Dylan Cai
 */
@Suppress("unused")
class App : Application() {

  private val userService: UserService? by routerServices()

  override fun onCreate() {
    super.onCreate()
    LoginInterceptor.enable { userService?.isLogin() == true }
  }
}