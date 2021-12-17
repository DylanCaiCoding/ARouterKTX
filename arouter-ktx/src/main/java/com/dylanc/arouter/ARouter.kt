@file:JvmName("ARouterUtils")
@file:Suppress("unused")

package com.dylanc.arouter

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.template.IProvider
import com.alibaba.android.arouter.launcher.ARouter
import com.dylanc.arouter.postcard.PostcardBuilder
import com.dylanc.arouter.postcard.with

internal const val KEY_ROUTER_PATH = "router_path"

@JvmName("startActivity")
fun startRouterActivity(path: String, vararg pairs: Pair<String, Any?>, block: PostcardBuilder.() -> Unit = {}) =
  null.startRouterActivity(path, *pairs, block = block)

@JvmName("startActivity")
fun Fragment.startRouterActivity(path: String, vararg pairs: Pair<String, Any?>, block: PostcardBuilder.() -> Unit = {}) =
  requireActivity().startRouterActivity(path, *pairs, block = block)

@JvmName("startActivity")
fun Context?.startRouterActivity(path: String, vararg pairs: Pair<String, Any?>, block: PostcardBuilder.() -> Unit = {}) {
  ARouter.getInstance().build(path).apply {
    val builder = PostcardBuilder(this).apply(block)
    with(*pairs).navigation(this@startRouterActivity, builder.callback)
  }
}

inline fun <reified T : IProvider> routerServices() =
  lazy<T?> { ARouter.getInstance().navigation(T::class.java) }

inline fun <reified T : IProvider> safeRouterServices() =
  lazy {
    try {
      ARouter.getInstance().navigation(T::class.java)!!
    } catch (e: Exception) {
      val canonicalName = T::class.java.canonicalName
      throw IllegalArgumentException("There's no router service matched. Interface is $canonicalName")
    }
  }

inline fun <reified T : IProvider> routerServices(path: String) =
  lazy { ARouter.getInstance().build(path).navigation() as T? }

inline fun <reified T : IProvider> safeRouterServices(path: String) =
  lazy {
    try {
      ARouter.getInstance().build(path).navigation() as T
    } catch (e: NullPointerException) {
      throw IllegalArgumentException("There's no router service matched. path = [$path]")
    }
  }

fun routerFragments(path: String, block: Postcard.() -> Unit = {}) =
  lazy { ARouter.getInstance().build(path).apply(block).navigation() as Fragment? }

fun safeRouterFragments(path: String, block: Postcard.() -> Unit = {}) =
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
    startRouterActivity(path) { finishAfterArrival() }
  } else {
    setResult(Activity.RESULT_OK)
    finish()
  }
}