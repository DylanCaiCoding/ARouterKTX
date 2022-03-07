package com.dylanc.arouter

import android.content.Context
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Interceptor
import com.alibaba.android.arouter.facade.callback.InterceptorCallback
import com.alibaba.android.arouter.facade.template.IInterceptor

/**
 * @author Dylan Cai
 */
@Interceptor(priority = Int.MAX_VALUE)
class SignInInterceptor : IInterceptor {

  override fun process(postcard: Postcard, callback: InterceptorCallback) {
    if (isInterceptPath(postcard.path)) {
      startActivityByRouter(SignInActivityPath!!, KEY_ROUTER_PATH to postcard.path) {
        onArrival { callback.onInterrupt(null) }
      }
    } else {
      callback.onContinue(postcard)
    }
  }

  override fun init(context: Context) = Unit

  companion object {
    internal var checkSignIn: (() -> Boolean)? = null
    internal var SignInActivityPath: String? = null
    internal val requireSignInPaths = arrayListOf<String>()

    fun enable(onCheckSignIn: () -> Boolean) {
      checkSignIn = onCheckSignIn
    }

    internal fun isInterceptPath(path: String) =
      SignInActivityPath != null && path != SignInActivityPath &&
          requireSignInPaths.contains(path) && checkSignIn?.invoke() == false
  }
}
