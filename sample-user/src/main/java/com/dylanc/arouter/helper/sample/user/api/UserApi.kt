package com.dylanc.arouter.helper.sample.user.api

import com.dylanc.arouter.helper.sample.core.bean.ApiResponse
import com.dylanc.arouter.helper.sample.user.bean.User
import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * @author Dylan Cai
 * @since 2020/5/24
 */
private const val USER = "mock/fcd983f6975dc5cfcb8b28b0f12b645f/component/user"

interface UserApi {

  @FormUrlEncoded
  @POST("$USER/login")
  fun login(
    @Field("username") username: String,
    @Field("password") password: String
  ): Single<ApiResponse<User>>
}