package com.dylanc.arouter.helper

import android.content.Context
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Interceptor
import com.alibaba.android.arouter.facade.callback.InterceptorCallback
import com.alibaba.android.arouter.facade.template.IInterceptor

/**
 * @author Dylan Cai
 */
@Interceptor(priority = 1000)
class LoginInterceptor : IInterceptor {

  override fun process(postcard: Postcard, callback: InterceptorCallback) {
    isLogin?.let {
      if (!it.invoke()) {
        if (loginInterceptPaths.contains(postcard.path)) {
          callback.onInterrupt(null)
          return
        }
      }
    }
    callback.onContinue(postcard)
  }

  override fun init(context: Context) {}
}
