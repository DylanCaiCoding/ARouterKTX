package com.dylanc.arouter.helper.sample.core.bean

/**
 * @author Dylan Cai
 * @since 2020/5/24
 */
data class ApiResponse<T>(
  val code: Int,
  val msg: String,
  val data: T
)