package com.suviridedriver.ui.add_recharge

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.suviridedriver.databinding.ActivityAddRechargeBinding
import com.suviridedriver.ui.discount.DiscountActivity
import com.suviridedriver.ui.make_payment.MakePaymentViewModel
import com.suviridedriver.ui.make_payment.adapter.PaymentAdapter
import com.suviridedriver.utils.AppSession
import com.suviridedriver.utils.BaseActivity
import com.suviridedriver.utils.Constants
import com.suviridedriver.utils.NetworkResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddRechargeActivity : BaseActivity() {
    private lateinit var binding: ActivityAddRechargeBinding
    val context: Context = this
    var isPackcageSelect = false
    var orderID = ""
    var orderAmount = 0
    val makePaymentViewModel by viewModels<MakePaymentViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddRechargeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.btnNext.setOnClickListener {
            if (isPackcageSelect) {
                startActivity(intent)
            } else {
                showToast("Please select atleast a package")
            }
        }

        try {
            val vehicleId = AppSession.getValue(context,Constants.VEHICLE_ID)
            showLog("vehicleId","vehicleId - $vehicleId")
            if (vehicleId != null){
                makePaymentViewModel.getRidesPackages(vehicleId)
            }
        }catch (e:Exception){
            e.printStackTrace()
            showLog("vehicleId","Error - Vehicle Id is null")
        }

        getRidesPackagesObserve()
    }

    private fun getRidesPackagesObserve() {
        makePaymentViewModel.ridesPackagesResponseResponseLiveData.observe(this) {
            hideLoader()
            when (it) {
                is NetworkResult.Success -> {
                    if (it.data != null) {
                        Log.d("ridesPackages", "Success - " + it.data)
                        // Call adapter here
                        val paymentAdapter = PaymentAdapter(it.data.packages, context) {
                            isPackcageSelect = true
                            intent = Intent(applicationContext, DiscountActivity::class.java)
                            AppSession.save(context, Constants.PACKAGE_ID, it._id)
                            intent.putExtra("orderId", it.order_id)
                            intent.putExtra("orderAmount", it.price.toString())
                            intent.putExtra("orderCurrency", "INR")
                            intent.putExtra("intentFrom", "AddRecharge")
                            intent.putExtra("packageName", it.name)
                            intent.putExtra("discount", "20")

                            binding.btnNext.setOnClickListener {
                                if (isPackcageSelect) {
                                    startActivity(intent)
                                }
                            }
                        }
                        val linearLayoutManager =
                            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                        binding.rvPaymentList.layoutManager = linearLayoutManager
                        binding.rvPaymentList.adapter = paymentAdapter
                    }
                }
                is NetworkResult.Error -> {
                    Log.d("ridesPackages", "Error - " + it.message)
                }
                is NetworkResult.Loading -> {
                    showLoader()
                    Log.d("ridesPackages", "Loading")
                }
            }
        }
    }
}