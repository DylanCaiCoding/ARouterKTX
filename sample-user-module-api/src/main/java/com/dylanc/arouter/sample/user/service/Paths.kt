package com.dylanc.arouter.sample.user.service

import com.dylanc.arouter.annotations.RequireLoginPath
import com.dylanc.arouter.annotations.LoginActivityPath

const val GROUP_USER = "/user"

@LoginActivityPath
const val PATH_LOGIN = "$GROUP_USER/login"

@RequireLoginPath
const val PATH_USER_INFO = "$GROUP_USER/userinfo"