package com.dylanc.arouter.helper.sample

import android.app.Application
import com.dylanc.arouter.helper.enableLoginInterceptor
import com.dylanc.arouter.helper.initARouter
import com.dylanc.arouter.helper.routerServiceOf
import com.dylanc.arouter.helper.sample.common.PATH_LOGIN
import com.dylanc.arouter.helper.sample.common.PATH_MAIN
import com.dylanc.arouter.helper.sample.common.service.UserService
import com.dylanc.retrofit.helper.initRetrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

/**
 * @author Dylan Cai
 * @since 2020/5/23
 */
@Suppress("unused")
class App : Application() {

  override fun onCreate() {
    super.onCreate()

    initARouter(this, BuildConfig.DEBUG)
    enableLoginInterceptor(PATH_LOGIN, PATH_MAIN) {
      routerServiceOf<UserService>().isLogin()
    }

    initRetrofit {
      debug(BuildConfig.DEBUG)
      addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    }
  }
}