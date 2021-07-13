package com.dylanc.arouter

import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.NavCallback

/**
 * @author Dylan Cai
 */
fun NavCallback(
  onArrival: (Postcard) -> Unit = {},
  onInterrupt: (Postcard) -> Unit = {},
  onFound: (Postcard) -> Unit = {},
  onLost: (Postcard) -> Unit = {},
) = object : NavCallback() {
  override fun onArrival(postcard: Postcard) {
    onArrival(postcard)
  }

  override fun onFound(postcard: Postcard) {
    onFound(postcard)
  }

  override fun onLost(postcard: Postcard) {
    onLost(postcard)
  }

  override fun onInterrupt(postcard: Postcard) {
    onInterrupt(postcard)
  }
}