@file:Suppress("unused")

package com.dylanc.arouter

import android.content.Context
import android.util.Log
import androidx.startup.Initializer
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.android.arouter.utils.Consts
import com.dylanc.arouter.SignInInterceptor.Companion.SignInActivityPath
import com.dylanc.arouter.SignInInterceptor.Companion.requireSignInPaths

/**
 * @author Dylan Cai
 */
internal class PathsInitializer : Initializer<Unit> {

  @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
  override fun create(context: Context) {
    try {
      val clazz = Class.forName("${Consts.ROUTE_ROOT_PAKCAGE}.ARouter$\$LoginInfo")
      SignInActivityPath = clazz.getField("activityPath")[clazz.newInstance()] as? String

      val sharedPreferences = context.getSharedPreferences(Consts.AROUTER_SP_CACHE_KEY, Context.MODE_PRIVATE)
      val routerMap = HashSet(sharedPreferences.getStringSet(Consts.AROUTER_SP_KEY_MAP, HashSet()))
      for (className in routerMap) {
        if(className.startsWith("${Consts.ROUTE_ROOT_PAKCAGE}.ARouter$\$Paths$\$")){
          (Class.forName(className).newInstance() as? IRoutePaths)?.loadInto(requireSignInPaths)
        }
      }

      ARouter.logger.debug("Paths", requireSignInPaths.toString())
    } catch (e: NoSuchFieldException) {
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  override fun dependencies() = listOf(ARouterInitializer::class.java)
}
