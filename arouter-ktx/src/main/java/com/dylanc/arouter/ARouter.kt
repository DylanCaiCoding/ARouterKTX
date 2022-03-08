@file:JvmName("ARouterUtils")
@file:Suppress("unused")

package com.dylanc.arouter

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.template.IProvider
import com.alibaba.android.arouter.launcher.ARouter

internal const val KEY_ROUTER_PATH = "router_path"

@JvmName("startActivity")
fun startActivityByRouter(path: String, vararg pairs: Pair<String, Any?>, block: (PostcardBuilder.() -> Unit)? = null) =
  application.startActivityByRouter(path, *pairs, block = block)

@JvmName("startActivity")
fun Fragment.startActivityByRouter(path: String, vararg pairs: Pair<String, Any?>, block: (PostcardBuilder.() -> Unit)? = null) =
  requireActivity().startActivityByRouter(path, *pairs, block = block)

@JvmName("startActivity")
fun Context.startActivityByRouter(path: String, vararg pairs: Pair<String, Any?>, block: (PostcardBuilder.() -> Unit)? = null) {
  ARouter.getInstance().build(path).with(bundleOf(*pairs)).navigation(this, block)
}

inline fun <reified T : IProvider> routerServices() =
  lazy<T?> { ARouter.getInstance().navigation(T::class.java) }

inline fun <reified T : IProvider> routerServices(path: String) =
  lazy { ARouter.getInstance().build(path).navigation() as? T }

fun createFragmentByRouter(path: String, block: Postcard.() -> Unit = {}) =
  ARouter.getInstance().build(path).apply(block).navigation() as? Fragment

fun ActivityResultLauncher<Intent>.launchByRouter(path: String, vararg pairs: Pair<String, Any?>, block: (PostcardBuilder.() -> Unit)? = null) {
  ARouter.getInstance().build(path).with(bundleOf(*pairs)).navigation(this, block = block)
}

fun Activity.loginSuccess() {
  val path = intent.getStringExtra(KEY_ROUTER_PATH)
  if (path != null) {
    startActivityByRouter(path) {
      onArrival { finish() }
    }
  } else {
    setResult(Activity.RESULT_OK)
    finish()
  }
}