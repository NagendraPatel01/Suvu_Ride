package com.suviridedriver.ui.make_payment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.suviridedriver.databinding.ActivityMakePaymentBinding
import com.suviridedriver.ui.make_payment.adapter.PaymentAdapter
import com.suviridedriver.utils.AppSession
import com.suviridedriver.utils.BaseActivity
import com.suviridedriver.utils.Constants
import com.suviridedriver.utils.Constants.TAG
import com.suviridedriver.utils.NetworkResult

class MakePaymentActivity : BaseActivity() {
    val context: Context = this
    private lateinit var binding: ActivityMakePaymentBinding
    var isPackcageSelect = false
    var orderID = ""
    var orderAmount = 0
    val makePaymentViewModel by viewModels<MakePaymentViewModel>()
    //private lateinit var intent: Intent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMakePaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
           // val vehicleId = AppSession.getValue(context,Constants.VEHICLE_ID)
            val vehicleId ="646b65fbfc2bf852518e43fe"
            if (vehicleId != null){
                makePaymentViewModel.getRidesPackages(vehicleId)
            }
        }catch (e:Exception){
            e.printStackTrace()
            showLog(TAG,"Error - Vehicle Id is null")
        }


        getRidesPackagesObserve()

        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.btnNext.setOnClickListener {
            if (isPackcageSelect) {
                startActivity(intent)
            }else{
                showToast("Please select atleast a package")
            }
        }
    }


    private fun getRidesPackagesObserve() {
        makePaymentViewModel.ridesPackagesResponseResponseLiveData.observe(this) {
            hideLoader()
            when (it) {
                is NetworkResult.Success -> {
                    if (it.data != null) {
                        // Call adapter here
                        val paymentAdapter = PaymentAdapter(it.data.packages, context) {
                            isPackcageSelect = true
                            intent  = Intent(applicationContext, PaymentModeActivity::class.java)
                            AppSession.save(context,Constants.PACKAGE_ID,it._id)
                            intent.putExtra("orderId", it.order_id)
                            intent.putExtra("orderAmount", it.price.toString())
                            intent.putExtra("orderCurrency", "INR")
                            intent.putExtra("intentFrom", "MakePayment")

                            binding.btnNext.setOnClickListener {
                                if (isPackcageSelect) {
                                    startActivity(intent)
                                }
                            }
                        }
                        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                        binding.rvPaymentList.layoutManager = linearLayoutManager
                        binding.rvPaymentList.adapter = paymentAdapter
                    }
                }
                is NetworkResult.Error -> {
                    Log.d(Constants.TAG, "Error - " + it.message)
                }
                is NetworkResult.Loading -> {
                    showLoader()
                    Log.d(Constants.TAG, "Loading")
                }
            }
        }
    }
}