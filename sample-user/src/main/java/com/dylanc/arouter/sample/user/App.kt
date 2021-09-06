package com.dylanc.arouter.sample.user

import android.app.Application
import android.util.Log
import com.dylanc.arouter.interceptor.LoginInterceptor
import com.dylanc.arouter.routerServices
import com.dylanc.arouter.sample.common.PATH_LOGIN
import com.dylanc.arouter.sample.common.PATH_USER_INFO
import com.dylanc.arouter.sample.common.service.UserService
import com.dylanc.longan.TAG
import com.dylanc.retrofit.helper.initRetrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

/**
 * @author Dylan Cai
 * @since 2020/5/24
 */
@Suppress("unused")
class App : Application() {

  private val userService: UserService by routerServices()

  override fun onCreate() {
    super.onCreate()
    LoginInterceptor.enable(PATH_LOGIN, listOf(PATH_USER_INFO)) { userService.isLogin() }

    initRetrofit {
      debug(BuildConfig.DEBUG)
      baseUrl("https://fastmock.site/")
      addCallAdapterFactory(RxJava2CallAdapterFactory.create())
      addHttpLog {
        Log.i(TAG, it)
      }
    }
  }
}