package com.dylanc.arouter.helper.sample

import android.app.Application
import com.alibaba.android.arouter.launcher.ARouter
import com.dylanc.arouter.helper.enableLoginInterceptor

/**
 * @author Dylan Cai
 * @since 2020/5/23
 */
@Suppress("unused")
class App : Application() {
  override fun onCreate() {
    super.onCreate()
    if (BuildConfig.DEBUG) {
      ARouter.openLog()
      ARouter.openDebug()
    }
    ARouter.init(this)
    enableLoginInterceptor("/app/login",
      "/app/main") {
      isLogin
    }
  }
}