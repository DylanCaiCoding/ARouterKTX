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
    val needLogin = postcard.extras.getBoolean(KEY_CHECK_LOGIN)
    if (needLogin && postcard.path != loginActivityPath && checkLogin?.invoke() == false) {
      callback.onInterrupt(null)
      return
    }
    callback.onContinue(postcard)
  }

  override fun init(context: Context) {}
}