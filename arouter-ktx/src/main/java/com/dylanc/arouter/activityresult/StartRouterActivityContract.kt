@file:Suppress("unused")

package com.dylanc.arouter.activityresult

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.os.bundleOf
import com.alibaba.android.arouter.facade.enums.RouteType
import com.alibaba.android.arouter.facade.model.RouteMeta


fun ActivityResultCaller.startRouterActivityLauncher(block: (ActivityResult) -> Unit) =
  registerForActivityResult(StartRouterActivityContract(), block)

fun ActivityResultLauncher<RouterRequest>.launch(path: String, intent: Intent = Intent()) =
  launch(RouterRequest(path, intent))

fun ActivityResultLauncher<RouterRequest>.launch(path: String, vararg extras: Pair<String, Any?>) =
  launch(RouterRequest(path, Intent().apply { putExtras(bundleOf(*extras)) }))

@Suppress("UNCHECKED_CAST")
internal val routes: Map<String, RouteMeta> by lazy {
  val clazz = Class.forName("com.alibaba.android.arouter.core.Warehouse")
  val field = clazz.getDeclaredField("routes")
  field.isAccessible = true
  field[null] as Map<String, RouteMeta>
}

data class RouterRequest(
  val path: String,
  val intent: Intent = Intent()
)

class StartRouterActivityContract : ActivityResultContract<RouterRequest, ActivityResult>() {

  override fun createIntent(context: Context, input: RouterRequest) =
    input.intent.apply {
      val routeMeta = routes[input.path]
      if (routeMeta?.type != RouteType.ACTIVITY) {
        throw IllegalArgumentException("The routing class for the path is not an activity type.")
      }
      setClass(context, routeMeta.destination)
    }

  override fun parseResult(resultCode: Int, intent: Intent?) = ActivityResult(resultCode, intent)
}
