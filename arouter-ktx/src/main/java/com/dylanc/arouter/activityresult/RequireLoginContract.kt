package com.dylanc.arouter.activityresult

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContract
import com.alibaba.android.arouter.core.LogisticsCenter
import com.alibaba.android.arouter.facade.enums.RouteType
import com.alibaba.android.arouter.launcher.ARouter
import com.dylanc.arouter.interceptor.LoginInterceptor
import com.dylanc.arouter.interceptor.LoginInterceptor.Companion.loginActivityPath

fun ActivityResultCaller.requireLoginLauncher(block: (Boolean) -> Unit) =
  registerForActivityResult(RequireLoginContract(), block)

class RequireLoginContract : ActivityResultContract<Unit, Boolean>() {

  override fun createIntent(context: Context, input: Unit?): Intent {
    val postcard = ARouter.getInstance().build(loginActivityPath!!)
    LogisticsCenter.completion(postcard)
    if (postcard.type != RouteType.ACTIVITY) {
      throw IllegalArgumentException("The routing class for the path is not an activity type.")
    }
    return Intent(context, postcard.destination)
  }

  override fun parseResult(resultCode: Int, intent: Intent?) = resultCode == Activity.RESULT_OK

  override fun getSynchronousResult(context: Context, input: Unit?): SynchronousResult<Boolean>? =
    when {
      LoginInterceptor.checkLogin?.invoke() == true -> SynchronousResult(true)
      loginActivityPath == null -> SynchronousResult(false)
      else -> null
    }
}
