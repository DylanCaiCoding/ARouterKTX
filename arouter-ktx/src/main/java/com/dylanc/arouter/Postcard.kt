@file:Suppress("unused")

package com.dylanc.arouter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.core.LogisticsCenter
import com.alibaba.android.arouter.exception.NoRouteFoundException
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.InterceptorCallback
import com.alibaba.android.arouter.facade.callback.NavCallback
import com.alibaba.android.arouter.facade.callback.NavigationCallback
import com.alibaba.android.arouter.facade.enums.RouteType
import com.alibaba.android.arouter.facade.service.DegradeService
import com.alibaba.android.arouter.facade.service.InterceptorService
import com.alibaba.android.arouter.facade.service.PretreatmentService
import com.alibaba.android.arouter.launcher.ARouter
import com.alibaba.android.arouter.utils.Consts
import com.alibaba.android.arouter.utils.TextUtils

fun Postcard.navigation(context: Context, block: (PostcardBuilder.() -> Unit)? = null): Any? =
  navigation(context, PostcardBuilder(this).apply { block?.invoke(this) }.callback)

fun Postcard.navigation(launcher: ActivityResultLauncher<Intent>, context: Context = application, block: (PostcardBuilder.() -> Unit)? = null): Any? =
  navigation(launcher, context, PostcardBuilder(this).apply { block?.invoke(this) }.callback)

fun Postcard.navigation(launcher: ActivityResultLauncher<Intent>, context: Context = application, callback: NavigationCallback? = null): Any? {
  val pretreatmentService = ARouter.getInstance().navigation(PretreatmentService::class.java)
  if (null != pretreatmentService && !pretreatmentService.onPretreatment(context, this)) {
    return null
  }

  this.context = context

  try {
    LogisticsCenter.completion(this)
  } catch (ex: NoRouteFoundException) {
    ARouter.logger.warning(Consts.TAG, ex.message)

    if (ARouter.debuggable()) {
      runInMainThread {
        val text = "There's no route matched!\nPath = [$path]\nGroup = [$group]"
        Toast.makeText(application, text, Toast.LENGTH_LONG).show()
      }
    }

    if (null != callback) {
      callback.onLost(this)
    } else {
      ARouter.getInstance().navigation(DegradeService::class.java)?.onLost(context, this)
    }
    return null
  }

  callback?.onFound(this)

  val interceptorService = ARouter.getInstance().build("/arouter/service/interceptor").navigation() as InterceptorService
  if (!isGreenChannel) {
    interceptorService.doInterceptions(this, object : InterceptorCallback {

      override fun onContinue(postcard: Postcard) {
        postcard._navigation(launcher, callback)
      }

      override fun onInterrupt(exception: Throwable) {
        callback?.onInterrupt(this@navigation)
        ARouter.logger.info(Consts.TAG, "Navigation failed, termination by interceptor : " + exception.message)
      }
    })
  } else {
    return _navigation(launcher, callback)
  }
  return null
}

@Suppress("FunctionName", "DEPRECATION")
private fun Postcard._navigation(launcher: ActivityResultLauncher<Intent>, callback: NavigationCallback?): Any? {
  val currentContext = context

  when (type) {
    RouteType.ACTIVITY -> {
      val intent = Intent(currentContext, destination).putExtras(extras)

      val flags = flags
      if (0 != flags) {
        intent.flags = flags
      }

      val action = action
      if (!TextUtils.isEmpty(action)) {
        intent.action = action
      }

      runInMainThread {
        launcher.launch(intent)

        if (-1 != enterAnim && -1 != exitAnim && currentContext is Activity) {    // Old version.
          currentContext.overridePendingTransition(enterAnim, exitAnim)
        }

        callback?.onArrival(this)
      }
    }
    RouteType.PROVIDER -> return provider
    RouteType.BOARDCAST, RouteType.CONTENT_PROVIDER, RouteType.FRAGMENT -> {
      val fragmentMeta = destination
      try {
        val instance = fragmentMeta.getConstructor().newInstance()
        if (instance is android.app.Fragment) {
          instance.arguments = extras
        } else if (instance is Fragment) {
          instance.arguments = extras
        }
        return instance
      } catch (ex: java.lang.Exception) {
        ARouter.logger.error(Consts.TAG, "Fetch fragment instance error, " + TextUtils.formatStackTrace(ex.stackTrace))
      }
      return null
    }
    RouteType.METHOD, RouteType.SERVICE -> return null
    else -> return null
  }
  return null
}

private val handler by lazy { Handler(Looper.getMainLooper()) }

private fun runInMainThread(runnable: Runnable) {
  if (Looper.getMainLooper().thread !== Thread.currentThread()) {
    handler.post(runnable)
  } else {
    runnable.run()
  }
}

class PostcardBuilder(private val postcard: Postcard) {
  private var onArrival: ((Postcard) -> Unit)? = null
  private var onInterrupt: ((Postcard) -> Unit)? = null
  private var onFound: ((Postcard) -> Unit)? = null
  private var onLost: ((Postcard) -> Unit)? = null

  fun bundle(bundle: Bundle) {
    postcard.with(bundle)
  }

  fun flags(flag: Int) {
    postcard.withFlags(flag)
  }

  fun transition(enterAnim: Int, exitAnim: Int) {
    postcard.withTransition(enterAnim, exitAnim)
  }

  fun onArrival(block: (Postcard) -> Unit) {
    onArrival = block
  }

  fun onInterrupt(block: (Postcard) -> Unit) {
    onInterrupt = block
  }

  fun onFound(block: (Postcard) -> Unit) {
    onFound = block
  }

  fun onLost(block: (Postcard) -> Unit) {
    onLost = block
  }

  internal val callback: NavigationCallback?
    get() = if (onArrival != null || onInterrupt != null || onFound != null || onLost != null) {
      object : NavCallback() {
        override fun onArrival(postcard: Postcard) {
          onArrival?.invoke(postcard)
        }

        override fun onFound(postcard: Postcard) {
          onFound?.invoke(postcard)
        }

        override fun onLost(postcard: Postcard) {
          onLost?.invoke(postcard)
        }

        override fun onInterrupt(postcard: Postcard) {
          if (SignInInterceptor.isInterceptPath(postcard.path)) {
            onArrival?.invoke(postcard)
          } else {
            onInterrupt?.invoke(postcard)
          }
        }
      }
    } else {
      null
    }

  companion object {
    internal fun Postcard.builder() = PostcardBuilder(this)
  }
}
