package com.dylanc.arouter

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContract
import com.alibaba.android.arouter.core.LogisticsCenter
import com.alibaba.android.arouter.facade.enums.RouteType
import com.alibaba.android.arouter.launcher.ARouter
import com.dylanc.arouter.SignInInterceptor.Companion.SignInActivityPath

fun ActivityResultCaller.requireLoginLauncher(onLoginFailure: (() -> Unit)? = null, onLoginSuccess: () -> Unit) =
  registerForActivityResult(RequireSignInContract()) {
    if (it) onLoginSuccess() else onLoginFailure?.invoke()
  }

class RequireSignInContract : ActivityResultContract<Unit, Boolean>() {

  override fun createIntent(context: Context, input: Unit?): Intent {
    val postcard = ARouter.getInstance().build(SignInActivityPath!!)
    LogisticsCenter.completion(postcard)
    if (postcard.type != RouteType.ACTIVITY) {
      throw IllegalArgumentException("The routing class for the path of $SignInActivityPath is not an activity type.")
    }
    return Intent(context, postcard.destination)
  }

  override fun parseResult(resultCode: Int, intent: Intent?) = resultCode == Activity.RESULT_OK

  override fun getSynchronousResult(context: Context, input: Unit?): SynchronousResult<Boolean>? =
    when {
      SignInActivityPath == null -> SynchronousResult(true)
      SignInInterceptor.checkSignIn?.invoke() == true -> SynchronousResult(true)
      else -> null
    }
}
