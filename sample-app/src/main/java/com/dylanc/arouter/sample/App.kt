package com.dylanc.arouter.sample

import android.app.Application
import com.dylanc.arouter.enableRouterLoginInterceptor
import com.dylanc.arouter.initARouter
import com.dylanc.arouter.routerServices
import com.dylanc.arouter.sample.common.PATH_LOGIN
import com.dylanc.arouter.sample.common.service.UserService
import com.dylanc.retrofit.helper.initRetrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

/**
 * @author Dylan Cai
 * @since 2020/5/23
 */
@Suppress("unused")
class App : Application() {

  private val userService: UserService by routerServices()

  override fun onCreate() {
    super.onCreate()
    initARouter(BuildConfig.DEBUG)
    enableRouterLoginInterceptor(PATH_LOGIN) { userService.isLogin() }

    initRetrofit {
      debug(BuildConfig.DEBUG)
      addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    }
  }
}