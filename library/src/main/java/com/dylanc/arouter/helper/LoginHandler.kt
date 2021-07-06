@file:JvmName("LoginHandler")

package com.dylanc.arouter.helper

import android.app.Activity

/**
 * @author Dylan Cai
 */
internal var loginObserver: (() -> Unit)? = null

@JvmName("post")
fun Activity.loginSuccess() {
  val path = intent.getStringExtra(KEY_ROUTER_PATH)
  if (path != null) {
    startRouterActivityAndFinish(path, extras = intent.extras)
  } else {
    finish()
  }
  loginObserver?.invoke()
  loginObserver = null
}