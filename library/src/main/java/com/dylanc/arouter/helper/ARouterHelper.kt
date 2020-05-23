@file:JvmName("ARouterHelper")
@file:Suppress("unused")

package com.dylanc.arouter.helper

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.facade.callback.NavigationCallback
import com.alibaba.android.arouter.facade.template.IProvider
import com.alibaba.android.arouter.launcher.ARouter
import java.io.Serializable
import kotlin.reflect.KClass

/**
 * @author Dylan Cai
 */
internal const val KEY_ROUTER_PATH = "router_path"
internal const val PATH_OBSERVE_LOGIN_SERVICE = "/arouter/observe_login"

val loginInterceptPaths = mutableSetOf<String>()
internal var loginActivityPath: String? = null
internal var isLogin: (() -> Boolean)? = null

fun enableLoginInterceptor(
  loginActivityPath: String,
  vararg loginInterceptPaths: String,
  isLogin: () -> Boolean
) {
  com.dylanc.arouter.helper.loginActivityPath = loginActivityPath
  for (path in loginInterceptPaths) {
    com.dylanc.arouter.helper.loginInterceptPaths.add(path)
  }
  com.dylanc.arouter.helper.isLogin = isLogin
}

@JvmOverloads
@JvmName("startActivity")
fun startRouterActivity(
  path: String,
  postcard: (Postcard.() -> Unit)? = null
) {
  ARouter.getInstance().build(path)
    .apply { postcard?.invoke(this) }
    .navigation()
}

@JvmOverloads
@JvmName("startActivityAndFinish")
fun Activity.startRouterActivityAndFinish(
  path: String,
  postcard: (Postcard.() -> Unit)? = null
) {
  startRouterActivity(path, object : NavCallback() {
    override fun onArrival(postcard: Postcard?) {
      finish()
    }
  }, postcard)
}

@JvmName("startActivity")
fun startRouterActivity(
  path: String,
  vararg postcard: Pair<String, Any>
) {
  ARouter.getInstance().build(path)
    .apply {
      postcard.forEach { pair ->
        val key = pair.first
        when (val value = pair.second) {
          is Int -> withInt(key, value)
          is Byte -> withByte(key, value)
          is Char -> withChar(key, value)
          is Short -> withShort(key, value)
          is Boolean -> withBoolean(key, value)
          is Long -> withLong(key, value)
          is Float -> withFloat(key, value)
          is Double -> withDouble(key, value)
          is String -> withString(key, value)
          is CharSequence -> withCharSequence(key, value)
          is Parcelable -> withParcelable(key, value)
          is Serializable -> withSerializable(key, value)
          is ByteArray -> withByteArray(key, value)
          is ShortArray -> withShortArray(key, value)
          is CharArray -> withCharArray(key, value)
          is FloatArray -> withFloatArray(key, value)
          is Bundle -> withBundle(key, value)
          is List<*> -> withList(key, value)
          else -> {
          }
        }
      }
    }
    .navigation()
}

@JvmOverloads
@JvmName("startActivity")
fun Context.startRouterActivity(
  path: String,
  onArrival: ((postcard: Postcard) -> Unit),
  postcard: (Postcard.() -> Unit)? = null
) =
  startRouterActivity(path, object : NavCallback() {
    override fun onArrival(postcard: Postcard) {
      onArrival.invoke(postcard)
    }
  }, postcard)

@JvmOverloads
@JvmName("startActivity")
fun Context.startRouterActivity(
  path: String,
  callback: NavigationCallback? = null,
  postcard: (Postcard.() -> Unit)? = null
) {
  ARouter.getInstance().build(path)
    .apply { postcard?.invoke(this) }
    .navigation(this, callback)
}


private fun Postcard.withList(key: String, value: List<*>) {
  if (value.isNotEmpty()) {
    when (value[0]) {
      is Int -> {
        withIntegerArrayList(key, arrayListOf<Int>().apply {
          value.forEach { add(it as Int) }
        })
      }
      is String -> {
        withStringArrayList(key, arrayListOf<String>().apply {
          value.forEach { add(it as String) }
        })
      }
      is CharSequence -> {
        withCharSequenceArrayList(key, arrayListOf<CharSequence>().apply {
          value.forEach { add(it as CharSequence) }
        })
      }
      is Parcelable -> {
        withParcelableArrayList(key, arrayListOf<Parcelable>().apply {
          value.forEach { add(it as Parcelable) }
        })
      }
      else -> {
      }
    }
  }
}

@JvmOverloads
@JvmName("startActivityForResult")
fun Activity.startRouterActivityForResult(
  path: String,
  requestCode: Int,
  callback: NavigationCallback? = null,
  postcard: (Postcard.() -> Unit)? = null
) {
  ARouter.getInstance().build(path)
    .apply { postcard?.invoke(this) }
    .navigation(this, requestCode, callback)
}

@JvmName("getService")
fun <T : IProvider> routerServiceOf(clazz: Class<T>): T =
  ARouter.getInstance().navigation(clazz)

@JvmName("getService")
fun <T : IProvider> routerServiceOf(clazz: KClass<T>): T =
  routerServiceOf(clazz.java)

@JvmName("getService")
@Suppress("UNCHECKED_CAST")
fun <T : IProvider> routerServiceOf(path: String): T =
  ARouter.getInstance().build(path).navigation() as T

inline fun <reified T : IProvider> routerServiceOf(): T =
  routerServiceOf(T::class)

@JvmOverloads
@JvmName("startActivityCheckLogin")
fun Context.startRouterActivityCheckLogin(
  path: String,
  onArrival: ((postcard: Postcard) -> Unit)? = null
) = run {
  startRouterActivity(path, LoginNavCallback(this, onArrival))
}

fun executeAfterLogin(observer: () -> Unit) =
  executeAfterLogin(observer, { startRouterActivity(loginActivityPath!!) })

fun executeAfterLogin(
  observer: () -> Unit,
  startLoginActivity: () -> Unit
) {
  if (isLogin != null) {
    if (isLogin!!.invoke()) {
      observer.invoke()
    } else {
      startLoginActivity()
      observeLogin(observer)
    }
  } else {
    observer.invoke()
  }
}