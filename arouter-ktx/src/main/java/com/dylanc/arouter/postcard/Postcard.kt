@file:Suppress("unused")

package com.dylanc.arouter.postcard

import android.app.Activity
import android.os.Bundle
import android.os.Parcelable
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.facade.callback.NavigationCallback
import com.dylanc.arouter.interceptor.LoginInterceptor
import java.io.Serializable

/**
 * @author Dylan Cai
 */


fun Postcard.with(vararg pairs: Pair<String, Any?>) = apply {
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
      is Serializable -> withSerializable(key, value)

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

      else -> {
        val valueType = value.javaClass.canonicalName
        throw IllegalArgumentException("Illegal value type $valueType for key \"$key\"")
      }
    }
  }
}

class PostcardBuilder(private val postcard: Postcard) {
  private var onArrival: ((Postcard) -> Unit)? = null
  private var onInterrupt: ((Postcard) -> Unit)? = null
  private var onFound: ((Postcard) -> Unit)? = null
  private var onLost: ((Postcard) -> Unit)? = null

  fun bundle(bundle: Bundle?) {
    postcard.with(bundle)
  }

  fun flags(flag: Int) {
    postcard.withFlags(flag)
  }

  fun transition(enterAnim: Int, exitAnim: Int) {
    postcard.withTransition(enterAnim, exitAnim)
  }

  fun onArrival(block: (Postcard) -> Unit) {
    onArrival = block
  }

  fun finishAfterArrival() {
    onArrival = {
      it.context.run {
        if (this is Activity) {
          finish()
        }
      }
    }
  }

  fun onInterrupt(block: (Postcard) -> Unit) {
    onInterrupt = block
  }

  fun onFound(block: (Postcard) -> Unit) {
    onFound = block
  }

  fun onLost(block: (Postcard) -> Unit) {
    onLost = block
  }

  internal val callback: NavigationCallback?
    get() = if (onArrival != null || onInterrupt != null || onFound != null || onLost != null) {
      object : NavCallback() {
        override fun onArrival(postcard: Postcard) {
          onArrival?.invoke(postcard)
        }

        override fun onFound(postcard: Postcard) {
          onFound?.invoke(postcard)
        }

        override fun onLost(postcard: Postcard) {
          onLost?.invoke(postcard)
        }

        override fun onInterrupt(postcard: Postcard) {
          if (LoginInterceptor.isInterceptPath(postcard.path)) {
            onArrival?.invoke(postcard)
          } else {
            onInterrupt?.invoke(postcard)
          }
        }
      }
    } else {
      null
    }
}