package com.dylanc.arouter.helper.sample.user.utils

import com.dylanc.arouter.helper.sample.user.bean.User
import com.dylanc.utilktx.*

/**
 * @author Dylan Cai
 */
object UserRepository {
  private const val KEY_USER = "user"
  private var userCache: User? = null

  @JvmStatic
  fun getUser(): User? {
    if (userCache == null) {
      val json = spValueOf(KEY_USER, null)
      userCache = json?.toInstance()
    }
    return userCache
  }

  @JvmStatic
  fun saveUser(user: User) {
    userCache = user
    putSP(KEY_USER, user.toJson())
  }

  @JvmStatic
  fun logout() {
    if (isLogin()) {
      userCache = null
      removeSp(KEY_USER)
    }
  }

  @JvmStatic
  fun isLogin() = getUser() != null
}