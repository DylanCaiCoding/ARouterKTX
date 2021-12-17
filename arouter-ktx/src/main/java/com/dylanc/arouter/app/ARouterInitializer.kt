@file:Suppress("unused")

package com.dylanc.arouter.app

import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.startup.Initializer
import com.alibaba.android.arouter.launcher.ARouter

/**
 * @author Dylan Cai
 */
internal class ARouterInitializer : Initializer<Unit> {

  override fun create(context: Context) {
    val isDebug = context.packageManager.getApplicationInfo(context.packageName, 0).flags and
        ApplicationInfo.FLAG_DEBUGGABLE != 0
    if (isDebug) {
      ARouter.openLog()
      ARouter.openDebug()
    }
    ARouter.init(context as Application)
  }

  override fun dependencies() = emptyList<Class<Initializer<*>>>()
}