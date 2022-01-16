@file:Suppress("unused")

package com.dylanc.arouter.postcard

import android.app.Activity
import android.os.Bundle
import android.os.Parcelable
import androidx.core.os.bundleOf
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.facade.callback.NavigationCallback
import com.dylanc.arouter.interceptor.LoginInterceptor
import java.io.Serializable

fun Postcard.with(vararg pairs: Pair<String, Any?>): Postcard = with(bundleOf(*pairs))

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
      (it.context as? Activity)?.finish()
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