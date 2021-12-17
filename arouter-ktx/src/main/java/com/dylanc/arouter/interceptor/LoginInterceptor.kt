package com.dylanc.arouter.interceptor

import android.content.Context
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Interceptor
import com.alibaba.android.arouter.facade.callback.InterceptorCallback
import com.alibaba.android.arouter.facade.template.IInterceptor
import com.dylanc.arouter.KEY_ROUTER_PATH
import com.dylanc.arouter.startRouterActivity

/**
 * @author Dylan Cai
 */
@Interceptor(priority = Int.MAX_VALUE)
class LoginInterceptor : IInterceptor {

  override fun process(postcard: Postcard, callback: InterceptorCallback) {
    if (isInterceptPath(postcard.path)) {
      startRouterActivity(loginActivityPath!!, KEY_ROUTER_PATH to postcard.path) {
        onArrival {
          callback.onInterrupt(null)
        }
      }
    } else {
      callback.onContinue(postcard)
    }
  }

  override fun init(context: Context) {}

  companion object {
    internal var checkLogin: (() -> Boolean)? = null
    internal var loginActivityPath: String? = null
    private var interceptedPaths: List<String>? = null

    fun enable(loginPath: String, interceptedPaths: List<String>, onCheckLogin: () -> Boolean) {
      loginActivityPath = loginPath
      this.interceptedPaths = interceptedPaths
      checkLogin = onCheckLogin
    }

    internal fun isInterceptPath(path: String) =
      path != loginActivityPath && interceptedPaths?.contains(path) == true && checkLogin?.invoke() == false
  }
}
