package com.dylanc.arouter.sample.user.repository

import com.dylanc.arouter.sample.user.service.bean.User
import com.dylanc.arouter.sample.user.api.UserApi
import com.dylanc.mmkv.MMKVOwner
import com.dylanc.mmkv.mmkvParcelable
import com.dylanc.retrofit.apiServices
import kotlinx.coroutines.flow.flow

/**
 * @author Dylan Cai
 */
object UserRepository : MMKVOwner {
  private val api: UserApi by apiServices()
  var user by mmkvParcelable<User>()

  fun login(username: String, password: String) = flow {
    api.login(username, password).data.let {
      user = it
      emit(it)
    }
  }

  fun logout() {
    if (kv.containsKey(this::user.name)) {
      kv.removeValueForKey(this::user.name)
    }
  }

  fun isLogin() = user != null
}