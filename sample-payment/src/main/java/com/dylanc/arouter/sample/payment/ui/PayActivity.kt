package com.dylanc.arouter.sample.payment.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.dylanc.arouter.routerServices
import com.dylanc.arouter.sample.common.PATH_PAYMENT
import com.dylanc.arouter.sample.common.service.PaymentService
import com.dylanc.arouter.sample.payment.databinding.ActivityPayBinding
import com.dylanc.viewbinding.binding

@Route(path = PATH_PAYMENT)
class PayActivity : AppCompatActivity() {

  private val binding: ActivityPayBinding by binding()
  private val paymentService: PaymentService by routerServices()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    with(binding) {
      btnAliPay.setOnClickListener {
        paymentService.aliPay(100f)
      }
      btnWechatPay.setOnClickListener {
        paymentService.wechatPay(200f)
      }
    }
  }
}