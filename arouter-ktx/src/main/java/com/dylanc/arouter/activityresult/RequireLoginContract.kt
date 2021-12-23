package com.dylanc.arouter.activityresult

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContract
import com.dylanc.arouter.interceptor.LoginInterceptor

fun ActivityResultCaller.requireLoginLauncher(block: (Boolean) -> Unit) =
  registerForActivityResult(RequireLoginContract(), block)

class RequireLoginContract : ActivityResultContract<Unit, Boolean>() {

  override fun createIntent(context: Context, input: Unit?) =
    Intent(context, routes.getActivityClass(loginActivityPath!!))

  override fun parseResult(resultCode: Int, intent: Intent?) = resultCode == Activity.RESULT_OK

  override fun getSynchronousResult(context: Context, input: Unit?): SynchronousResult<Boolean>? =
    when {
      LoginInterceptor.checkLogin?.invoke() == true -> SynchronousResult(true)
      loginActivityPath == null -> SynchronousResult(false)
      else -> null
    }
}
