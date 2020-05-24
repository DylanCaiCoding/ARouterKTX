package com.dylanc.arouter.helper.sample.common

import android.app.Activity
import android.content.Context
import com.alibaba.android.arouter.facade.Postcard
import com.dylanc.arouter.helper.startRouterActivityCheckLogin

/**
 * @author Dylan Cai
 */
private const val GROUP_SAMPLE = "/sample"
const val PATH_LOGIN = "$GROUP_SAMPLE/login"
const val PATH_MAIN = "$GROUP_SAMPLE/main"

fun Activity.startMainActivity(
  onArrival: (postcard: Postcard) -> Unit = { finish() }
) =
  startRouterActivityCheckLogin(PATH_MAIN, onArrival = onArrival)