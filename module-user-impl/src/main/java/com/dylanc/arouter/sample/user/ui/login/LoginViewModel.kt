package com.dylanc.arouter.sample.user.ui.login

import androidx.lifecycle.asLiveData
import com.dylanc.arouter.sample.user.repository.UserRepository
import com.dylanc.retrofit.coroutines.RequestViewModel
import com.dylanc.retrofit.coroutines.catchWith
import com.dylanc.retrofit.coroutines.showLoadingWith

/**
 * @author Dylan Cai
 */
class LoginViewModel : RequestViewModel() {

  fun login(username: String, password: String) =
    UserRepository.login(username, password)
      .showLoadingWith(loadingFlow)
      .catchWith(exceptionFlow)
      .asLiveData()
}