@file:Suppress("unused")

package com.dylanc.arouter.postcard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
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
import com.dylanc.arouter.interceptor.LoginInterceptor

fun Postcard.with(vararg pairs: Pair<String, Any?>): Postcard = with(bundleOf(*pairs))

fun Postcard.navigation(fragment: Fragment, requestCode: Int, callback: NavigationCallback? = null): Any? {
  val pretreatmentService = ARouter.getInstance().navigation(PretreatmentService::class.java)
  if (null != pretreatmentService && !pretreatmentService.onPretreatment(context, this)) {
    // Pretreatment failed, navigation canceled.
    return null
  }

  try {
    LogisticsCenter.completion(this)
  } catch (ex: NoRouteFoundException) {
    ARouter.logger.warning(Consts.TAG, ex.message)

    if (ARouter.debuggable()) {
      // Show friendly tips for user.
      fragment.requireActivity().runOnUiThread {
        Toast.makeText(
          fragment.requireContext(), """There's no route matched!
 Path = [$path]
 Group = [$group]""", Toast.LENGTH_LONG
        ).show()
      }
    }

    if (null != callback) {
      callback.onLost(this)
    } else {
      // No callback for this invoke, then we use the global degrade service.
      val degradeService = ARouter.getInstance().navigation(DegradeService::class.java)
      degradeService?.onLost(context, this)
    }
    return null
  }
  callback?.onFound(this)

  val interceptorService = ARouter.getInstance().build("/arouter/service/interceptor").navigation() as InterceptorService
  if (!isGreenChannel) {   // It must be run in async thread, maybe interceptor cost too mush time made ANR.
    interceptorService.doInterceptions(this, object : InterceptorCallback {
      /**
       * Continue process
       *
       * @param postcard route meta
       */
      override fun onContinue(postcard: Postcard) {
        _navigation(fragment, postcard, requestCode, callback)
      }

      /**
       * Interrupt process, pipeline will be destory when this method called.
       *
       * @param exception Reson of interrupt.
       */
      override fun onInterrupt(exception: Throwable) {
        callback?.onInterrupt(this@navigation)
        ARouter.logger.info(Consts.TAG, "Navigation failed, termination by interceptor : " + exception.message)
      }
    })
  } else {
    return _navigation(fragment, this, requestCode, callback)
  }
  return null
}

@Suppress("FunctionName", "DEPRECATION")
private fun Postcard._navigation(fragment: Fragment, postcard: Postcard, requestCode: Int, callback: NavigationCallback?): Any? {
  val currentContext = postcard.context
  when (postcard.type) {
    RouteType.ACTIVITY -> {
      // Build intent
      val intent = Intent(currentContext, postcard.destination)
      intent.putExtras(postcard.extras)

      // Set flags.
      val flags = postcard.flags
      if (0 != flags) {
        intent.flags = flags
      }

      // Non activity, need FLAG_ACTIVITY_NEW_TASK
      if (currentContext !is Activity) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }

      // Set Actions
      val action = postcard.action
      if (!TextUtils.isEmpty(action)) {
        intent.action = action
      }

      fragment.requireActivity().runOnUiThread {
        if (requestCode >= 0) {  // Need start for result
          if (currentContext is Activity) {
            fragment.startActivityForResult(intent, requestCode, postcard.optionsBundle)
          } else {
            ARouter.logger.warning(Consts.TAG, "Must use [navigation(activity, ...)] to support [startActivityForResult]")
          }
        } else {
          ActivityCompat.startActivity(currentContext, intent, postcard.optionsBundle)
        }

        if (-1 != postcard.enterAnim && -1 != postcard.exitAnim && currentContext is Activity) {    // Old version.
          currentContext.overridePendingTransition(postcard.enterAnim, postcard.exitAnim)
        }

        callback?.onArrival(postcard)
      }
    }
    RouteType.PROVIDER -> return postcard.provider
    RouteType.BOARDCAST, RouteType.CONTENT_PROVIDER, RouteType.FRAGMENT -> {
      val fragmentMeta = postcard.destination
      try {
        val instance = fragmentMeta.getConstructor().newInstance()
        if (instance is android.app.Fragment) {
          instance.arguments = postcard.extras
        } else if (instance is Fragment) {
          instance.arguments = postcard.extras
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


class PostcardBuilder(private val postcard: Postcard) {
  private var onArrival: ((Postcard) -> Unit)? = null
  private var onInterrupt: ((Postcard) -> Unit)? = null
  private var onFound: ((Postcard) -> Unit)? = null
  private var onLost: ((Postcard) -> Unit)? = null

  fun bundle(bundle: Bundle?) {
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

  fun finishAfterArrival() {
    onArrival = {
      (it.context as? Activity)?.finish()
    }
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
          if (LoginInterceptor.isInterceptPath(postcard.path)) {
            onArrival?.invoke(postcard)
          } else {
            onInterrupt?.invoke(postcard)
          }
        }
      }
    } else {
      null
    }
}