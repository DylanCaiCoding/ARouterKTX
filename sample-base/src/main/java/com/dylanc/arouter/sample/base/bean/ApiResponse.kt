package com.dylanc.arouter.sample.base.bean

/**
 * @author Dylan Cai
 * @since 2020/5/24
 */
data class ApiResponse<T>(
  val code: Int,
  val msg: String,
  val data: T
)