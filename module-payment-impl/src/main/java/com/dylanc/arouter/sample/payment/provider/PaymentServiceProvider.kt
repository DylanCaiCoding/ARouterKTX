package com.dylanc.arouter.sample.payment.provider

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.dylanc.arouter.sample.payment.service.PaymentService
import com.dylanc.longan.toast
import com.dylanc.longan.topActivity

/**
 * @author Dylan Cai
 */
@Route(path = "/payment/service")
class PaymentServiceProvider : PaymentService {

  override fun aliPay(money: Float) {
    topActivity.toast("Ali Pay ¥$money")
  }

  override fun wechatPay(money: Float) {
    topActivity.toast("Wechat Pay ¥$money")
  }

  override fun init(context: Context) {

  }
}