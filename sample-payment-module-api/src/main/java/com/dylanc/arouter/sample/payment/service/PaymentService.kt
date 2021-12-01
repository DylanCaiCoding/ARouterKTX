package com.dylanc.arouter.sample.payment.service

import com.alibaba.android.arouter.facade.template.IProvider

/**
 * @author Dylan Cai
 */
interface PaymentService : IProvider {
  fun aliPay(money: Float)

  fun wechatPay(money: Float)
}