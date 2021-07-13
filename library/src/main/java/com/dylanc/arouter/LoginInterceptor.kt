package com.dylanc.arouter

import android.content.Context
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Interceptor
import com.alibaba.android.arouter.facade.callback.InterceptorCallback
import com.alibaba.android.arouter.facade.template.IInterceptor

/**
 * @author Dylan Cai
 */
@Interceptor(priority = 65535)
class LoginInterceptor : IInterceptor {

  override fun process(postcard: Postcard, callback: InterceptorCallback) {
    val isCheckLogin = postcard.extras.getBoolean(KEY_CHECK_LOGIN)
    if (isCheckLogin && postcard.path != loginActivityPath && checkLogin?.invoke() == false) {
      callback.onInterrupt(null)
    } else {
      callback.onContinue(postcard)
    }
  }

  override fun init(context: Context) {}

  companion object {
    internal var checkLogin: (() -> Boolean)? = null
  }
}