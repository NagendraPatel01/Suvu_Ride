package com.suviridedriver.ui.discount

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.suviridedriver.databinding.ActivityDiscountBinding
import com.suviridedriver.ui.make_payment.PaymentModeActivity
import com.suviridedriver.utils.BaseActivity

class DiscountActivity : BaseActivity() {
    private lateinit var binding: ActivityDiscountBinding
    val context: Context = this
    var orderID = ""
    var orderCurrency = ""
    var orderAmount = 0
    var intentFrom = ""
    var discount = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiscountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            if (intent != null) {
                orderID = intent.getStringExtra("orderId")!!
                orderAmount = intent.getStringExtra("orderAmount")!!.toInt()
                orderCurrency = intent.getStringExtra("orderCurrency")!!
                intentFrom = intent.getStringExtra("intentFrom")!!
                val packageName = intent.getStringExtra("packageName")!!
                discount = intent.getStringExtra("discount")!!

                binding.tvPackageName.text = "$packageName"
                binding.tvPrice.text = "₹ $orderAmount"
                binding.tvDiscount.text = "₹ - $discount"
                binding.tvTotalAmount.text = "₹ ${orderAmount - discount.toInt()}"
            }
        } catch (e: Exception) {
            showToast(e.message.toString())
        }

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.btnNext.setOnClickListener {
            val i = Intent(this@DiscountActivity, PaymentModeActivity::class.java)
            i.putExtra("orderId", orderID)
            i.putExtra("orderAmount", "$orderAmount")
            i.putExtra("discount", discount)
            i.putExtra("orderCurrency", orderCurrency)
            i.putExtra("intentFrom", intentFrom)
            startActivity(i)
        }
    }
}