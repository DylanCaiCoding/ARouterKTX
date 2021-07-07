package com.dylanc.arouter.sample.common

import android.app.Activity
import com.alibaba.android.arouter.facade.Postcard
import com.dylanc.arouter.startRouterActivityCheckLogin

/**
 * @author Dylan Cai
 */
private const val GROUP_SAMPLE = "/sample"
const val PATH_MAIN = "$GROUP_SAMPLE/main"

private const val GROUP_USER = "/user"
const val PATH_LOGIN = "$GROUP_USER/login"
const val PATH_USER_INFO = "$GROUP_USER/userinfo"

private const val GROUP_PAYMENT = "/payment"
const val PATH_PAYMENT = "$GROUP_PAYMENT/payment"

fun Activity.startMainActivity(onArrival: (postcard: Postcard) -> Unit = { finish() }) =
  startRouterActivityCheckLogin(PATH_MAIN, onArrival = onArrival)