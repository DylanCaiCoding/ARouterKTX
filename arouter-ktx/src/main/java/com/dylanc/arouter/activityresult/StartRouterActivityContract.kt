@file:Suppress("unused")

package com.dylanc.arouter.activityresult

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.os.bundleOf
import com.alibaba.android.arouter.core.LogisticsCenter
import com.alibaba.android.arouter.facade.enums.RouteType
import com.alibaba.android.arouter.launcher.ARouter

fun ActivityResultCaller.startRouterActivityLauncher(block: (ActivityResult) -> Unit) =
  registerForActivityResult(StartRouterActivityContract(), block)

fun ActivityResultLauncher<RouterRequest>.launch(path: String, intent: Intent = Intent()) =
  launch(RouterRequest(path, intent))

fun ActivityResultLauncher<RouterRequest>.launch(path: String, vararg extras: Pair<String, Any?>) =
  launch(RouterRequest(path, Intent().putExtras(bundleOf(*extras))))

data class RouterRequest(
  val path: String,
  val intent: Intent = Intent()
)

class StartRouterActivityContract : ActivityResultContract<RouterRequest, ActivityResult>() {

  override fun createIntent(context: Context, input: RouterRequest):Intent {
    val postcard = ARouter.getInstance().build(input.path)
    LogisticsCenter.completion(postcard)
    if (postcard.type != RouteType.ACTIVITY) {
      throw IllegalArgumentException("The routing class for the path is not an activity type.")
    }
    return input.intent.setClass(context, postcard.destination)
  }

  override fun parseResult(resultCode: Int, intent: Intent?) = ActivityResult(resultCode, intent)
}
