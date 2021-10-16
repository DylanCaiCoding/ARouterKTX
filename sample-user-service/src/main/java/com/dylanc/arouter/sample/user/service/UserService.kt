package com.dylanc.arouter.sample.user.service

import android.app.Activity
import com.alibaba.android.arouter.facade.template.IProvider
import com.dylanc.arouter.sample.user.service.bean.User
import kotlinx.coroutines.flow.Flow

/**
 * @author Dylan Cai
 * @since 2020/5/24
 */
interface UserService : IProvider {
  fun login(username: String, password: String): Flow<User>

  fun logout(activity: Activity)

  fun isLogin(): Boolean

  val username: String?
}