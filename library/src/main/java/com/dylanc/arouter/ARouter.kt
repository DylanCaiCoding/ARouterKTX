@file:JvmName("ARouterUtils")
@file:Suppress("unused")

package com.dylanc.arouter

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavigationCallback
import com.alibaba.android.arouter.facade.template.IProvider
import com.alibaba.android.arouter.launcher.ARouter

/**
 * @author Dylan Cai
 */
internal const val KEY_ROUTER_PATH = "router_path"
internal const val KEY_CHECK_LOGIN = "router_check_login"
internal var loginActivityPath: String? = null

@JvmName("init")
fun Application.initARouter(isDebug: Boolean = false) {
  if (isDebug) {
    ARouter.openLog()
    ARouter.openDebug()
  }
  ARouter.init(this)
}

fun <T : IProvider> findRouterService(clazz: Class<T>): T =
  ARouter.getInstance().navigation(clazz)

@Suppress("UNCHECKED_CAST")
fun <T : IProvider> findRouterService(path: String): T =
  ARouter.getInstance().build(path).navigation() as T

inline fun <reified T : IProvider> routerServices() =
  lazy { findRouterService(T::class.java) }

inline fun <reified T : IProvider> routerServices(path: String) =
  lazy { findRouterService<T>(path) }

@JvmOverloads
fun findRouterFragment(path: String, vararg pairs: Pair<String, Any?>, arguments: Bundle? = null): Fragment =
  ARouter.getInstance().build(path).with(arguments).with(*pairs).navigation() as Fragment

fun routerFragments(path: String, block: Postcard.() -> Unit = {}) =
  lazy { ARouter.getInstance().build(path).apply(block).navigation() as Fragment }

@JvmName("startActivity")
fun startRouterActivity(path: String, vararg pairs: Pair<String, Any?>, extras: Bundle? = null) {
  ARouter.getInstance().build(path).with(extras).with(*pairs).navigation()
}

@JvmOverloads
@JvmName("startActivity")
fun Context.startRouterActivity(path: String, vararg pairs: Pair<String, Any?>, extras: Bundle? = null, callback: NavigationCallback? = null) {
  ARouter.getInstance().build(path).with(extras).with(*pairs).navigation(this, callback)
}

@JvmName("startActivity")
fun Context.startRouterActivity(path: String, vararg pairs: Pair<String, Any?>, extras: Bundle? = null, onArrival: (Postcard) -> Unit) =
  startRouterActivity(path, *pairs, extras = extras, callback = NavCallback(onArrival = onArrival))

@JvmOverloads
@JvmName("startActivity")
fun Fragment.startRouterActivity(path: String, vararg pairs: Pair<String, Any?>, extras: Bundle? = null, callback: NavigationCallback? = null) =
  requireContext().startRouterActivity(path, *pairs, extras = extras, callback = callback)

@JvmName("startActivity")
fun Fragment.startRouterActivity(path: String, vararg pairs: Pair<String, Any?>, extras: Bundle? = null, onArrival: (Postcard) -> Unit) =
  requireContext().startRouterActivity(path, *pairs, extras = extras, onArrival = onArrival)

@JvmOverloads
@JvmName("startActivityForResult")
fun Activity.startRouterActivityForResult(path: String, requestCode: Int, vararg pairs: Pair<String, Any?>, extras: Bundle? = null) {
  ARouter.getInstance().build(path).with(extras).with(*pairs).navigation(this, requestCode)
}

@JvmOverloads
@JvmName("startActivityForResult")
fun Fragment.startRouterActivityForResult(path: String, requestCode: Int, vararg pairs: Pair<String, Any?>, extras: Bundle? = null) =
  requireActivity().startRouterActivityForResult(path, requestCode, *pairs, extras = extras)

@JvmName("enableLoginInterceptor")
fun enableRouterLoginInterceptor(loginPath: String, onCheckLogin: () -> Boolean) {
  loginActivityPath = loginPath
  LoginInterceptor.checkLogin = onCheckLogin
}

@JvmOverloads
@JvmName("startActivityCheckLogin")
fun Context.startRouterActivityCheckLogin(
  path: String,
  vararg pairs: Pair<String, Any?>,
  extras: Bundle = Bundle(),
  onArrival: ((Postcard) -> Unit)? = null
) {
  extras.putBoolean(KEY_CHECK_LOGIN, true)
  startRouterActivity(path, *pairs, extras = extras, callback = LoginNavCallback(this, onArrival))
}

fun executeCheckLogin(action: () -> Unit) =
  if (loginActivityPath != null && LoginInterceptor.checkLogin?.invoke() == false) {
    startRouterActivity(loginActivityPath!!)
    loginObserver = action
  } else {
    action.invoke()
  }