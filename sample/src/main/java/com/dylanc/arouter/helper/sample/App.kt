package com.dylanc.arouter.helper.sample

import android.app.Application
import com.dylanc.arouter.helper.enableRouterLoginInterceptor
import com.dylanc.arouter.helper.initARouter
import com.dylanc.arouter.helper.routerServices
import com.dylanc.arouter.helper.sample.common.PATH_LOGIN
import com.dylanc.arouter.helper.sample.common.service.UserService
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
    initARouter(this, BuildConfig.DEBUG)
    enableRouterLoginInterceptor(PATH_LOGIN) { userService.isLogin() }

    initRetrofit {
      debug(BuildConfig.DEBUG)
      addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    }
  }
}