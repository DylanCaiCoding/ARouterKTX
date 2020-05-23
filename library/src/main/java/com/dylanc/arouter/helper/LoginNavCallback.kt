package com.dylanc.arouter.helper

import android.content.Context
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavCallback

/**
 * @author Dylan Cai
 */
class LoginNavCallback(
  private val context: Context,
  private var onArrival: ((postcard: Postcard) -> Unit)? = null
) : NavCallback() {

  override fun onInterrupt(postcard: Postcard) {
    if (isLogin == null || !loginInterceptPaths.contains(postcard.path)) {
      return
    }
    loginActivityPath?.let {
      context.startRouterActivity(it, { postcard ->
        onArrival?.invoke(postcard)
        onArrival = null
      }) {
        with(postcard.extras)
        withString(KEY_ROUTER_PATH, postcard.path)
      }
    }
  }

  override fun onArrival(postcard: Postcard) {
    onArrival?.invoke(postcard)
  }
}