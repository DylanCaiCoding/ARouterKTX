package com.dylanc.arouter.sample.user

import android.app.Application
import com.dylanc.arouter.interceptor.LoginInterceptor
import com.dylanc.arouter.safeRouterServices
import com.dylanc.arouter.sample.user.service.PATH_LOGIN
import com.dylanc.arouter.sample.user.service.PATH_USER_INFO
import com.dylanc.arouter.sample.user.service.UserService
import com.dylanc.retrofit.initRetrofit

/**
 * @author Dylan Cai
 */
@Suppress("unused")
class App : Application() {

  private val userService: UserService by safeRouterServices()

  override fun onCreate() {
    super.onCreate()
    LoginInterceptor.enable {
      userService.isLogin()
    }

    initRetrofit {
      baseUrl("https://fastmock.site/")
    }
  }
}