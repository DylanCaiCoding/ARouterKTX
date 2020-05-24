package com.dylanc.arouter.helper.sample.common.service

import android.app.Activity
import androidx.fragment.app.FragmentActivity
import com.alibaba.android.arouter.facade.template.IProvider

/**
 * @author Dylan Cai
 * @since 2020/5/24
 */
interface UserService : IProvider {
  fun login(
    activity: FragmentActivity,
    username: String,
    password: String,
    onSuccess: () -> Unit,
    onFailure: (throwable: Throwable) -> Unit = {}
  )

  fun logout(activity: Activity)

  fun isLogin(): Boolean

  val username: String?
}