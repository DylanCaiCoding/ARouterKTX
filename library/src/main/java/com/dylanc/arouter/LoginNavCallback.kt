package com.dylanc.arouter

import android.content.Context
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavCallback

/**
 * @author Dylan Cai
 */
internal class LoginNavCallback(
  private val context: Context,
  private var onArrival: ((postcard: Postcard) -> Unit)? = null
) : NavCallback() {

  override fun onInterrupt(postcard: Postcard) {
    if (checkLogin == null) return
    context.startRouterActivity(
      loginActivityPath!!,
      KEY_ROUTER_PATH to postcard.path,
      extras = postcard.extras,
      onArrival = {
        onArrival?.invoke(it)
        onArrival = null
      })
  }

  override fun onArrival(postcard: Postcard) {
    onArrival?.invoke(postcard)
  }
}