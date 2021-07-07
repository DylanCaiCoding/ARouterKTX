package com.dylanc.arouter.sample.payment

import android.app.Application
import com.dylanc.arouter.initARouter
import com.dylanc.arouter.sample.payment.BuildConfig

/**
 * @author Dylan Cai
 * @since 2020/5/24
 */
@Suppress("unused")
class App : Application() {
  override fun onCreate() {
    super.onCreate()
    initARouter(BuildConfig.DEBUG)
  }
}