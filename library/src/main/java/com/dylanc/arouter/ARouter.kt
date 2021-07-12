@file:JvmName("ARouterUtils")
@file:Suppress("unused")

package com.dylanc.arouter

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.facade.callback.NavigationCallback
import com.alibaba.android.arouter.facade.template.IProvider
import com.alibaba.android.arouter.launcher.ARouter

/**
 * @author Dylan Cai
 */
internal const val KEY_ROUTER_PATH = "router_path"
internal const val KEY_CHECK_LOGIN = "router_check_login"
internal var loginActivityPath: String? = null
internal var checkLogin: (() -> Boolean)? = null

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
fun findRouterFragment(path: String, vararg postcard: Pair<String, Any>, bundle: Bundle? = null): Fragment =
  ARouter.getInstance().build(path).with(bundle).with(*postcard).navigation() as Fragment

fun routerFragments(path: String, block: Postcard.() -> Unit = {}) =
  lazy { ARouter.getInstance().build(path).apply(block).navigation() as Fragment }

@JvmName("startActivity")
fun startRouterActivity(path: String, vararg postcard: Pair<String, Any>, bundle: Bundle? = null) {
  ARouter.getInstance().build(path).with(bundle).with(*postcard).navigation()
}

@JvmOverloads
@JvmName("startActivity")
fun Context.startRouterActivity(
  path: String,
  vararg postcard: Pair<String, Any>,
  extras: Bundle? = null,
  callback: NavigationCallback? = null
) {
  ARouter.getInstance().build(path).with(extras).with(*postcard).navigation(this, callback)
}

@JvmName("startActivity")
fun Context.startRouterActivity(
  path: String,
  vararg postcard: Pair<String, Any>,
  extras: Bundle? = null,
  onArrival: (postcard: Postcard) -> Unit
) =
  startRouterActivity(path, *postcard, extras = extras, callback = object : NavCallback() {
    override fun onArrival(postcard: Postcard) {
      onArrival.invoke(postcard)
    }
  })

@JvmOverloads
@JvmName("startActivityForResult")
fun Activity.startRouterActivityForResult(
  path: String,
  requestCode: Int,
  vararg postcard: Pair<String, Any>,
  extras: Bundle? = null,
  callback: NavigationCallback? = null
) {
  ARouter.getInstance().build(path).with(extras).with(*postcard).navigation(this, requestCode, callback)
}

@JvmName("startActivityForResult")
fun Activity.startRouterActivityForResult(
  path: String,
  requestCode: Int,
  vararg postcard: Pair<String, Any>,
  bundle: Bundle? = null,
  onArrival: (postcard: Postcard) -> Unit
) {
  ARouter.getInstance().build(path).with(bundle).with(*postcard).navigation(this, requestCode, object : NavCallback() {
    override fun onArrival(postcard: Postcard) {
      onArrival.invoke(postcard)
    }
  })
}

@JvmOverloads
@JvmName("startActivity")
fun Fragment.startRouterActivity(
  path: String,
  vararg postcard: Pair<String, Any>,
  extras: Bundle? = null,
  callback: NavigationCallback? = null
) =
  requireContext().startRouterActivity(path, *postcard, extras = extras, callback = callback)

@JvmName("startActivity")
fun Fragment.startRouterActivity(
  path: String,
  vararg postcard: Pair<String, Any>,
  extras: Bundle? = null,
  onArrival: (postcard: Postcard) -> Unit
) =
  requireContext().startRouterActivity(path, *postcard, extras = extras, onArrival = onArrival)

//@JvmOverloads
//@JvmName("startActivityForResult")
//fun Fragment.startRouterActivityForResult(
//  path: String,
//  requestCode: Int,
//  vararg postcard: Pair<String, Any>,
//  extras: Bundle? = null,
//  callback: NavigationCallback? = null
//) =
//  requireActivity().startRouterActivityForResult(path, requestCode, *postcard, extras = extras, callback = callback)
//
//@JvmName("startActivityForResult")
//fun Fragment.startRouterActivityForResult(
//  path: String,
//  requestCode: Int,
//  vararg postcard: Pair<String, Any>,
//  extras: Bundle? = null,
//  onArrival: (postcard: Postcard) -> Unit
//) =
//  requireActivity().startRouterActivityForResult(path, requestCode, *postcard, extras = extras, onArrival = onArrival)

@JvmName("enableLoginInterceptor")
fun enableRouterLoginInterceptor(loginPath: String, onCheckLogin: () -> Boolean) {
  loginActivityPath = loginPath
  checkLogin = onCheckLogin
}

@JvmOverloads
@JvmName("startActivityCheckLogin")
fun Context.startRouterActivityCheckLogin(
  path: String,
  vararg postcard: Pair<String, Any>,
  extras: Bundle = Bundle(),
  onArrival: ((postcard: Postcard) -> Unit)? = null
) {
  extras.putBoolean(KEY_CHECK_LOGIN, true)
  startRouterActivity(path, *postcard, extras = extras, callback = LoginNavCallback(this, onArrival))
}

fun executeCheckLogin(observer: () -> Unit) =
  if (checkLogin?.invoke() == false && loginActivityPath != null) {
    startRouterActivity(loginActivityPath!!)
    loginObserver = observer
  } else {
    observer.invoke()
  }