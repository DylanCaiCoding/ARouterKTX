package com.dylanc.arouter.helper.sample.user

import android.app.Application
import com.dylanc.arouter.helper.initARouter

/**
 * @author Dylan Cai
 * @since 2020/5/24
 */
@Suppress("unused")
class App: Application() {
  override fun onCreate() {
    super.onCreate()
    initARouter(this,BuildConfig.DEBUG)
  }
}