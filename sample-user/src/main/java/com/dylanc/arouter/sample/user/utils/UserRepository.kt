package com.dylanc.arouter.sample.user.utils

import com.dylanc.arouter.sample.user.bean.User
import com.dylanc.longan.SharedPreferencesOwner
import com.dylanc.longan.sharedPreferences
import com.google.gson.Gson

/**
 * @author Dylan Cai
 */
object UserRepository : SharedPreferencesOwner {
  private var userCache: User? = null
  private var user: String? by sharedPreferences()
  private val gson = Gson()

  @JvmStatic
  fun getUser(): User? {
    if (userCache == null) {
      val json = user
      userCache = gson.fromJson(json, User::class.java)
    }
    return userCache
  }

  @JvmStatic
  fun saveUser(user: User) {
    userCache = user
    this.user = gson.toJson(user)
  }

  @JvmStatic
  fun logout() {
    if (isLogin()) {
      userCache = null
      user = null
    }
  }

  @JvmStatic
  fun isLogin() = getUser() != null
}