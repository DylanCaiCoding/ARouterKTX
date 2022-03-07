@file:Suppress("unused")

package com.dylanc.arouter

import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.startup.Initializer
import com.alibaba.android.arouter.launcher.ARouter

/**
 * @author Dylan Cai
 */
internal lateinit var application: Application

internal class ARouterInitializer : Initializer<Unit> {

  override fun create(context: Context) {
    application = context as Application
    val applicationInfo = context.packageManager.getApplicationInfo(context.packageName, 0)
    val isDebug = applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
    if (isDebug) {
      ARouter.openLog()
      ARouter.openDebug()
    }
    ARouter.init(context)
  }

  override fun dependencies() = emptyList<Class<Initializer<*>>>()
}