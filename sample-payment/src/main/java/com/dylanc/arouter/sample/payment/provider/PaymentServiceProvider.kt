package com.dylanc.arouter.sample.payment.provider

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.dylanc.arouter.sample.common.service.PaymentService
import com.dylanc.longan.toast

/**
 * @author Dylan Cai
 */
@Route(path = "/payment/service")
class PaymentServiceProvider : PaymentService {
  override fun aliPay(money: Float) {
    toast("Ali Pay ¥$money")
  }

  override fun wechatPay(money: Float) {
    toast("Wechat Pay ¥$money")
  }

  override fun init(context: Context) {

  }
}