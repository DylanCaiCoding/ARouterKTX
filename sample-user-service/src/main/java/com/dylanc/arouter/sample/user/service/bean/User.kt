package com.dylanc.arouter.sample.user.service.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * @author Dylan Cai
 * @since 2020/5/24
 */
@Parcelize
data class User(
  val id: String,
  val name: String
) : Parcelable