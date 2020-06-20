package com.dylanc.arouter.helper.sample.user.provider

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.dylanc.arouter.helper.sample.common.service.UserService
import com.dylanc.arouter.helper.sample.common.startMainActivity
import com.dylanc.arouter.helper.sample.core.bean.ApiResponse
import com.dylanc.arouter.helper.sample.core.net.showLoading
import com.dylanc.arouter.helper.sample.user.api.UserApi
import com.dylanc.arouter.helper.sample.user.bean.User
import com.dylanc.arouter.helper.sample.user.utils.UserRepository
import com.dylanc.retrofit.helper.apiServiceOf
import com.dylanc.retrofit.helper.rxjava.io2mainThread
import com.dylanc.utilktx.finishAllActivitiesExceptNewest
import com.dylanc.utilktx.logJson
import com.dylanc.utilktx.putSP
import com.dylanc.utilktx.spValueOf
import okhttp3.Callback
import retrofit2.Call
import retrofit2.Response

/**
 * @author Dylan Cai
 * @since 2020/5/24
 */
@Route(path = "/login/service")
class LoginServiceProvider : UserService {

  @SuppressLint("CheckResult")
  override fun login(
    activity: FragmentActivity,
    username: String,
    password: String,
    onSuccess: () -> Unit,
    onFailure: (throwable: Throwable) -> Unit
  ) {
    apiServiceOf<UserApi>()
      .login(username, password)
      .showLoading(activity)
      .io2mainThread()
      .subscribe(
        { response ->
          logJson(response)
          UserRepository.saveUser(response.data)
          onSuccess()
        },
        onFailure
      )
  }

  override fun logout(activity: Activity) {
    UserRepository.logout()
    activity.startMainActivity {
      finishAllActivitiesExceptNewest()
      activity.finish()
    }
  }

  override fun isLogin(): Boolean =
    UserRepository.isLogin()

  override val username: String?
    get() = UserRepository.getUser()?.name

  override fun init(context: Context) {}
}