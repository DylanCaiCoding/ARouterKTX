@file:JvmName("ARouterUtils")
@file:Suppress("unused")

package com.dylanc.arouter

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.template.IProvider
import com.alibaba.android.arouter.launcher.ARouter
import com.dylanc.arouter.app.application
import com.dylanc.arouter.interceptor.LoginInterceptor
import com.dylanc.arouter.postcard.PostcardBuilder
import com.dylanc.arouter.postcard.with

/**
 * @author Dylan Cai
 */

private var loginObserver: (() -> Unit)? = null
internal const val KEY_ROUTER_PATH = "router_path"

@JvmName("startActivity")
fun startRouterActivity(path: String, vararg pairs: Pair<String, Any?>, block: PostcardBuilder.() -> Unit = {}) =
  application.startRouterActivity(path, *pairs, block = block)

@JvmName("startActivity")
fun Fragment.startRouterActivity(path: String, vararg pairs: Pair<String, Any?>, block: PostcardBuilder.() -> Unit = {}) =
  requireActivity().startRouterActivity(path, *pairs, block = block)

@JvmName("startActivity")
fun Context.startRouterActivity(path: String, vararg pairs: Pair<String, Any?>, block: PostcardBuilder.() -> Unit = {}) {
  ARouter.getInstance().build(path).apply {
    val builder = PostcardBuilder(this).apply(block)
    with(*pairs).navigation(this@startRouterActivity, builder.callback())
  }
}

@JvmOverloads
@JvmName("startActivityForResult")
fun Fragment.startRouterActivityForResult(
  path: String,
  requestCode: Int,
  vararg pairs: Pair<String, Any?>,
  block: PostcardBuilder.() -> Unit = {}
) =
  requireActivity().startRouterActivityForResult(path, requestCode, *pairs, block = block)

@JvmOverloads
@JvmName("startActivityForResult")
fun Activity.startRouterActivityForResult(
  path: String,
  requestCode: Int,
  vararg pairs: Pair<String, Any?>,
  block: PostcardBuilder.() -> Unit = {}
) {
  ARouter.getInstance().build(path).apply {
    val builder = PostcardBuilder(this).apply(block)
    with(*pairs).navigation(this@startRouterActivityForResult, requestCode, builder.callback())
  }
}

fun <T : IProvider> findRouterService(clazz: Class<T>): T? =
  ARouter.getInstance().navigation(clazz)

@Suppress("UNCHECKED_CAST")
fun <T : IProvider> findRouterService(path: String): T? =
  ARouter.getInstance().build(path).navigation() as T?

inline fun <reified T : IProvider> routerServices() =
  lazy {
    try {
      findRouterService(T::class.java)!!
    } catch (e: Exception) {
      val canonicalName = T::class.java.canonicalName
      throw IllegalArgumentException("There's no router service matched. Interface is $canonicalName")
    }
  }

inline fun <reified T : IProvider> routerServices(path: String) =
  lazy {
    try {
      findRouterService<T>(path)!!
    } catch (e: NullPointerException) {
      throw IllegalArgumentException("There's no router service matched. path = [$path]")
    }
  }

@JvmOverloads
fun findRouterFragment(path: String, vararg pairs: Pair<String, Any?>, arguments: Bundle? = null): Fragment? =
  ARouter.getInstance().build(path).with(arguments).with(*pairs).navigation() as Fragment?

fun routerFragments(path: String, block: Postcard.() -> Unit = {}) =
  lazy {
    try {
      ARouter.getInstance().build(path).apply(block).navigation() as Fragment
    } catch (e: NullPointerException) {
      throw IllegalArgumentException("There's no router fragment matched. path = [$path]")
    }
  }

fun Activity.loginSuccess() {
  val path = intent.getStringExtra(KEY_ROUTER_PATH)
  if (path != null) {
    startRouterActivity(path) { finishOnArrival() }
  } else {
    finish()
    loginObserver?.invoke()
    loginObserver = null
  }
}

fun doAfterLogin(action: () -> Unit) =
  if (LoginInterceptor.loginActivityPath != null && LoginInterceptor.checkLogin?.invoke() == false) {
    application.startRouterActivity(LoginInterceptor.loginActivityPath!!)
    loginObserver = action
  } else {
    action.invoke()
  }
