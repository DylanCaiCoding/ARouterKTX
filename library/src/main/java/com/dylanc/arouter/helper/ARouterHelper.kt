@file:JvmName("ARouterHelper")
@file:Suppress("unused")

package com.dylanc.arouter.helper

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.facade.callback.NavigationCallback
import com.alibaba.android.arouter.facade.template.IProvider
import com.alibaba.android.arouter.launcher.ARouter
import java.io.Serializable

/**
 * @author Dylan Cai
 */
internal const val KEY_ROUTER_PATH = "router_path"
internal const val KEY_NEED_LOGIN = "router_need_login"
internal var loginActivityPath: String? = null
internal var checkLogin: (() -> Boolean)? = null

@JvmName("init")
fun initARouter(application: Application, isDebug: Boolean = false) {
  if (isDebug) {
    ARouter.openLog()
    ARouter.openDebug()
  }
  ARouter.init(application)
}

@JvmName("enableLoginInterceptor")
fun enableRouterLoginInterceptor(
  loginPath: String,
  onCheckLogin: () -> Boolean
) {
  loginActivityPath = loginPath
  checkLogin = onCheckLogin
}

@JvmName("startActivity")
fun startRouterActivity(
  path: String,
  vararg postcard: Pair<String, Any>,
  bundle: Bundle? = null
) {
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

@JvmName("startActivityAndFinish")
fun Activity.startRouterActivityAndFinish(
  path: String,
  vararg postcard: Pair<String, Any>,
  extras: Bundle? = null
) =
  startRouterActivity(path, *postcard, extras = extras, onArrival = { finish() })

@JvmOverloads
@JvmName("startActivityForResult")
fun Activity.startRouterActivityForResult(
  path: String,
  requestCode: Int,
  vararg postcard: Pair<String, Any>,
  bundle: Bundle? = null,
  callback: NavigationCallback? = null
) {
  ARouter.getInstance().build(path).with(bundle).with(*postcard).navigation(this, requestCode, callback)
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

@JvmName("startActivityForResultAndFinish")
fun Activity.startRouterActivityForResultAndFinish(
  path: String,
  requestCode: Int,
  vararg postcard: Pair<String, Any>,
  extras: Bundle? = null
) =
  startRouterActivityForResult(path, requestCode, *postcard, bundle = extras) { finish() }

@JvmOverloads
@JvmName("startActivityNeedSignIn")
fun Context.startRouterActivityNeedLogin(
  path: String,
  vararg postcard: Pair<String, Any>,
  extras: Bundle = Bundle(),
  onArrival: ((postcard: Postcard) -> Unit)? = null
) {
  extras.putBoolean(KEY_NEED_LOGIN, true)
  startRouterActivity(path, *postcard, extras = extras, callback = LoginNavCallback(this, onArrival))
}

fun executeNeedLogin(observer: () -> Unit) =
  if (checkLogin?.invoke() == false && loginActivityPath != null) {
    startRouterActivity(loginActivityPath!!)
    loginObserver = observer
  } else {
    observer.invoke()
  }

@JvmOverloads
fun findRouterFragment(path: String, vararg postcard: Pair<String, Any>, bundle: Bundle? = null): Fragment =
  ARouter.getInstance().build(path).with(bundle).with(*postcard).navigation() as Fragment

fun <T : IProvider> findRouterService(clazz: Class<T>): T =
  ARouter.getInstance().navigation(clazz)

inline fun <reified T : IProvider> routerServices() =
  lazy { findRouterService(T::class.java) }

private fun Postcard.with(vararg pairs: Pair<String, Any?>) = apply {
  for ((key, value) in pairs) {
    when (value) {
      null -> withString(key, null)

      is Boolean -> withBoolean(key, value)
      is Byte -> withByte(key, value)
      is Char -> withChar(key, value)
      is Double -> withDouble(key, value)
      is Float -> withFloat(key, value)
      is Int -> withInt(key, value)
      is Long -> withLong(key, value)
      is Short -> withShort(key, value)

      is Bundle -> withBundle(key, value)
      is CharSequence -> withCharSequence(key, value)
      is Parcelable -> withParcelable(key, value)

      is ByteArray -> withByteArray(key, value)
      is CharArray -> withCharArray(key, value)
      is FloatArray -> withFloatArray(key, value)


      is ShortArray -> withShortArray(key, value)

      is Array<*> -> {
        val componentType = value::class.java.componentType!!
        @Suppress("UNCHECKED_CAST")
        when {
          Parcelable::class.java.isAssignableFrom(componentType) -> {
            withParcelableArray(key, value as Array<Parcelable>)
          }
          else -> {
            val valueType = componentType.canonicalName
            throw IllegalArgumentException(
              "Illegal value array type $valueType for key \"$key\""
            )
          }
        }
      }

      is List<*> -> {
        val componentType = value::class.java.componentType!!
        when {
          Int::class.java.isAssignableFrom(componentType) -> {
            withIntegerArrayList(key, value.toArrayList())
          }
          String::class.java.isAssignableFrom(componentType) -> {
            withStringArrayList(key, value.toArrayList())
          }
          CharSequence::class.java.isAssignableFrom(componentType) -> {
            withCharSequenceArrayList(key, value.toArrayList())
          }
          Parcelable::class.java.isAssignableFrom(componentType) -> {
            withParcelableArrayList(key, value.toArrayList())
          }
          else -> {
            val valueType = value.javaClass.canonicalName
            throw IllegalArgumentException("Illegal value list type $valueType for key \"$key\"")
          }
        }
      }

      is Serializable -> withSerializable(key, value)

      else -> {
        val valueType = value.javaClass.canonicalName
        throw IllegalArgumentException("Illegal value type $valueType for key \"$key\"")
      }
    }
  }
}

@Suppress("UNCHECKED_CAST")
private fun <T> List<*>.toArrayList() =
  ArrayList<T>().also { arrayList -> forEach { arrayList.add(it as T) } }