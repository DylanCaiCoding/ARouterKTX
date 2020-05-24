@file:JvmName("LoginHandler")

package com.dylanc.arouter.helper

import android.app.Activity

/**
 * @author Dylan Cai
 */
private var loginObserver: (() -> Unit)? = null

internal fun observeLogin(observer: (() -> Unit)?) {
  loginObserver = observer
}

@JvmName("post")
fun handleLogin(activity: Activity) {
  val intent = activity.intent
  val path = intent.getStringExtra(KEY_ROUTER_PATH)
  if (path != null) {
    activity.startRouterActivityAndFinish(path, bundle = intent.extras)
  } else {
    activity.finish()
  }
  loginObserver?.invoke()
  loginObserver = null
}