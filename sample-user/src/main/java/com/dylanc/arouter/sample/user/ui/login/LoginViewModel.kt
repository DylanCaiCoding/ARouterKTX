package com.dylanc.arouter.sample.user.ui.login

import androidx.lifecycle.asLiveData
import com.dylanc.arouter.routerServices
import com.dylanc.arouter.sample.user.service.UserService
import com.dylanc.retrofit.coroutines.RequestViewModel
import com.dylanc.retrofit.coroutines.catchWith
import com.dylanc.retrofit.coroutines.showLoadingWith

/**
 * @author Dylan Cai
 */
class LoginViewModel : RequestViewModel() {

  private val userService: UserService by routerServices()

  fun login(username: String, password: String) =
    userService.login(username, password)
      .showLoadingWith(loadingFlow)
      .catchWith(exceptionFlow)
      .asLiveData()
}