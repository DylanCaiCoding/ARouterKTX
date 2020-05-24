@file:JvmName("ARouterHelper")
@file:Suppress("unused")

package com.dylanc.arouter.helper

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
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

@JvmName("init")
fun initARouter(application: Application, isDebug: Boolean) {
  if (isDebug) {
    ARouter.openLog()
    ARouter.openDebug()
  }
  ARouter.init(application)
}

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

@JvmName("startActivity")
fun startRouterActivity(
  path: String,
  vararg postcard: Pair<String, Any>,
  bundle: Bundle? = null
) {
  ARouter.getInstance().build(path)
    .apply {
      with(bundle)
      putExtra(*postcard)
    }
    .navigation()
}

@JvmOverloads
@JvmName("startActivity")
fun Context.startRouterActivity(
  path: String,
  vararg postcard: Pair<String, Any>,
  bundle: Bundle? = null,
  callback: NavigationCallback? = null
) {
  ARouter.getInstance().build(path)
    .apply {
      with(bundle)
      putExtra(*postcard)
    }
    .navigation(this, callback)
}

@JvmName("startActivity")
fun Context.startRouterActivity(
  path: String,
  vararg postcard: Pair<String, Any>,
  bundle: Bundle? = null,
  onArrival: (postcard: Postcard) -> Unit
) =
  startRouterActivity(path, *postcard, bundle = bundle, callback = object : NavCallback() {
    override fun onArrival(postcard: Postcard) {
      onArrival.invoke(postcard)
    }
  })

@JvmName("startActivityAndFinish")
fun Activity.startRouterActivityAndFinish(
  path: String,
  vararg postcard: Pair<String, Any>,
  bundle: Bundle? = null
) =
  startRouterActivity(path, *postcard, bundle = bundle, onArrival = { finish() })

@JvmOverloads
@JvmName("startActivityForResult")
fun Activity.startRouterActivityForResult(
  path: String,
  requestCode: Int,
  vararg postcard: Pair<String, Any>,
  bundle: Bundle? = null,
  callback: NavigationCallback? = null
) {
  ARouter.getInstance().build(path)
    .apply {
      with(bundle)
      putExtra(*postcard)
    }
    .navigation(this, requestCode, callback)
}

@JvmName("startActivityForResult")
fun Activity.startRouterActivityForResult(
  path: String,
  requestCode: Int,
  vararg postcard: Pair<String, Any>,
  bundle: Bundle? = null,
  onArrival: (postcard: Postcard) -> Unit
) {
  ARouter.getInstance().build(path)
    .apply {
      with(bundle)
      putExtra(*postcard)
    }
    .navigation(this, requestCode, object : NavCallback() {
      override fun onArrival(postcard: Postcard) {
        onArrival.invoke(postcard)
      }
    })
}

@JvmName("startActivityForResultAndFinish")
fun Activity.startRouterActivityForResultAndFinish(
  path: String,
  requestCode: Int,
  vararg postcard: Pair<String, Any>,
  bundle: Bundle? = null
) =
  startRouterActivityForResult(path, requestCode, *postcard, bundle = bundle) { finish() }

@JvmOverloads
@JvmName("startActivityCheckLogin")
fun Context.startRouterActivityCheckLogin(
  path: String,
  vararg postcard: Pair<String, Any>,
  bundle: Bundle? = null,
  onArrival: ((postcard: Postcard) -> Unit)? = null
) =
  startRouterActivity(
    path,
    *postcard,
    bundle = bundle,
    callback = LoginNavCallback(this, onArrival)
  )

fun executeAfterLogin(observer: () -> Unit) =
  if (isLogin != null) {
    executeAfterLogin(observer, { startRouterActivity(loginActivityPath!!) })
  } else {
    observer.invoke()
  }

fun executeAfterLogin(observer: () -> Unit, startLoginActivity: () -> Unit) =
  if (isLogin != null && !isLogin!!()) {
    startLoginActivity()
    observeLogin(observer)
  } else {
    observer.invoke()
  }

@JvmOverloads
@JvmName("getFragment")
fun routerFragmentOf(
  path: String, vararg postcard: Pair<String, Any>,
  bundle: Bundle? = null
): Fragment =
  ARouter.getInstance().build(path).apply {
    with(bundle)
    putExtra(*postcard)
  }.navigation() as Fragment

@JvmName("getService")
fun <T : IProvider> routerServiceOf(clazz: Class<T>): T =
  ARouter.getInstance().navigation(clazz)

inline fun <reified T : IProvider> routerServiceOf(): T =
  routerServiceOf(T::class.java)

@JvmName("putPostcardExtra")
fun Postcard.putExtra(vararg postcard: Pair<String, Any>) {
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
