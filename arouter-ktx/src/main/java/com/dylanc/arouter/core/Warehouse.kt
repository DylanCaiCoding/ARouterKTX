package com.dylanc.arouter.core

import com.alibaba.android.arouter.facade.enums.RouteType
import com.alibaba.android.arouter.facade.model.RouteMeta

@Suppress("UNCHECKED_CAST")
internal val routes: Map<String, RouteMeta> by lazy {
  val clazz = Class.forName("com.alibaba.android.arouter.core.Warehouse")
  val field = clazz.getDeclaredField("routes")
  field.isAccessible = true
  field[null] as Map<String, RouteMeta>
}

internal fun Map<String, RouteMeta>.getActivityClass(path: String): Class<*> {
  val routeMeta = get(path)
  if (routeMeta == null) {
    throw IllegalArgumentException("The path has no routing class.")
  } else if (routeMeta.type != RouteType.ACTIVITY) {
    throw IllegalArgumentException("The routing class for the path is not an activity type.")
  }
  return routeMeta.destination
}

internal var loginActivityPath: String? = null
internal val requireLoginPaths = arrayListOf<String>()
