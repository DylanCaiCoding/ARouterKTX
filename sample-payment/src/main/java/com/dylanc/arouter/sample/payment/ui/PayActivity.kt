package com.dylanc.arouter.sample.payment.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.dylanc.arouter.safeRouterServices
import com.dylanc.arouter.sample.payment.databinding.ActivityPayBinding
import com.dylanc.arouter.sample.payment.service.PATH_PAYMENT
import com.dylanc.arouter.sample.payment.service.PaymentService
import com.dylanc.viewbinding.binding

@Route(path = PATH_PAYMENT)
class PayActivity : AppCompatActivity() {

  private val binding: ActivityPayBinding by binding()
  private val paymentService: PaymentService by safeRouterServices()

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