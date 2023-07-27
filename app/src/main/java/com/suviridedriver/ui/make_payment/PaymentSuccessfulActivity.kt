package com.suviridedriver.ui.make_payment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import com.suviridedriver.databinding.ActivityPaymentSuccessfulBinding
import com.suviridedriver.model.make_payment.MakePaymentRequest
import com.suviridedriver.ui.add_recharge.AddRechargeActivity
import com.suviridedriver.ui.bottom_navigation.MainActivity
import com.suviridedriver.ui.waiting_screen.WaitingActivity
import com.suviridedriver.utils.*
import com.suviridedriver.utils.Constants.TAG
import java.text.SimpleDateFormat
import java.util.*

class PaymentSuccessfulActivity : BaseActivity() {
    val context: Context = this
    private lateinit var binding: ActivityPaymentSuccessfulBinding
    val paymentSuccessfulViewModel by viewModels<PaymentSuccessfulViewModel>()

    //    val hashMap = HashMap<String, String>()
    val makePaymentRequest = MakePaymentRequest()
    var orderId: String? = null
    var isFromHome = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentSuccessfulBinding.inflate(layoutInflater)
        setContentView(binding.root)


        try {
            val sdf = SimpleDateFormat("dd MMM YYYY", Locale.getDefault())
            val sdf1 = SimpleDateFormat("HH:mm", Locale.getDefault())
            val currentDate: String = sdf.format(Date())
            val currenthours: String = sdf1.format(Date())


            if (intent != null) {
                //val bundle = intent.extras
                orderId = intent.getStringExtra("orderId")
                val orderAmount = intent.getStringExtra("orderAmount")


                //binding.tvtxMsg.text = txMsg
                binding.tvPaidPrice.text = "\u20B9 $orderAmount"
                binding.tvDateTime.text = currentDate + " at " + currenthours
                binding.tvTxIdValue.text = orderId

                val txMsg = intent.getStringExtra("txMsg")
                val txTime = intent.getStringExtra("txTime")
                isFromHome = intent.getStringExtra("isFromHome")!!


                // val bundleData1 = transformBundleToString(bundle!!)
                //  Log.d("bundleData1", "onCreate: =======================  $bundleData1")
                /*  binding.tvResponse.text = transformBundleToString(bundle!!)
                  val bundleData1 = transformBundleToString(bundle!!)
                  Log.d("bundleData1", "onCreate: =======================  $bundleData1")
                  Log.d("bundleData1", "onCreate:saperate data ========================================================================")
                  Log.d("bundleData1", "onCreate: =======================  ${bundleData1["orderId"]},  ${bundleData1["orderAmount"]}")
  */
            }
        } catch (e: Exception) {
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
        }

        binding.tvCopy.setOnClickListener {
            copyToClipboard(orderId)
        }
        val currentTime = Calendar.getInstance().time

        binding.tvContinue.setOnClickListener {
            if (orderId != null) {
                //val txStatus = intent.getStringExtra("txStatus")
                makePaymentRequest.packageId = AppSession.getValue(context, Constants.PACKAGE_ID)!!
                makePaymentRequest.paymentMode ="UPI"
                makePaymentRequest.orderId = intent.getStringExtra("orderId")
                makePaymentRequest.status = "OK"
                makePaymentRequest.txTime = "$currentTime"
                makePaymentRequest.referenceId = "$currentTime"
                makePaymentRequest.txMsg = "Transaction Successfull"
                makePaymentRequest.signature = "$currentTime"
                makePaymentRequest.orderAmount = intent.getStringExtra("orderAmount")
                makePaymentRequest.txStatus =  "SUCCESS"
                paymentSuccess(makePaymentRequest)

                /*when (txStatus) {
                    "SUCCESS" -> {
  *//*paymentMode : UPI
  orderId : 644a466017e7e4854d967806
  status : OK
  txTime : 2023-04-27 20:39:34
  referenceId : 1491412362
  txMsg : Transaction Successful
  signature : yEiq6v2mDs3x9L4hghlXCcHom5LQSr8yLTB4mCUh43E=
  orderAmount : 500.00
  txStatus : SUCCESS*//*
                        showToast("Status is: Success")
                        makePaymentRequest.packageId = AppSession.getValue(context, Constants.PACKAGE_ID)!!
                        makePaymentRequest.paymentMode = intent.getStringExtra("paymentMode")
                        makePaymentRequest.orderId = intent.getStringExtra("orderId")
                        makePaymentRequest.status = intent.getStringExtra("status")
                        makePaymentRequest.txTime = intent.getStringExtra("txTime")
                        makePaymentRequest.referenceId = intent.getStringExtra("referenceId")
                        makePaymentRequest.txMsg = intent.getStringExtra("txMsg")
                        makePaymentRequest.signature = intent.getStringExtra("signature")
                        makePaymentRequest.orderAmount = intent.getStringExtra("orderAmount")
                        makePaymentRequest.txStatus = txStatus
                        paymentSuccess(makePaymentRequest)
                    }
                    "FAILED" -> {
                        if (isFromHome.equals("true")) {
                            if (AppSession.getValue(context, Constants.SCREEN_TYPE) != null) {
                                FlowManager().navigate(
                                    context,
                                    AppSession.getValue(context, Constants.SCREEN_TYPE)
                                )
                            }
                        } else {
                            showToast(intent.getStringExtra("txMsg"))
                        }

                    }
                    else -> {
                        showToast("$txStatus -> " + intent.getStringExtra("txMsg"))
                    }
                }*/
            }
        }
    }

    private fun copyToClipboard(copiedText: String?) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Copied Text", copiedText)
        clipboard.setPrimaryClip(clipData)
        showToast("Copied to clipboard")
    }

    /*fun transformBundleToString(bundle: Bundle): String {
        var response = ""
        for (key in bundle.keySet()) {
            response = response + String.format("%s -> %s\n", key, bundle.getString(key))
            hashMap.put(key, bundle.getString(key)!!)
        }
        Log.d("Payment Successful", "transformBundleToString: $bundle")
        return response
    }*/

    /*  MakePaymentNewRequest(
      mapPaymentRequest.orderAmount!!,
      mapPaymentRequest.orderId!!,
      mapPaymentRequest.paymentMode!!,
      mapPaymentRequest.referenceId!!,
      mapPaymentRequest.signature!!,
      mapPaymentRequest.status!!,
      mapPaymentRequest.txMsg!!,
      mapPaymentRequest.txStatus!!,
      mapPaymentRequest.txTime!!
      )*/

    private fun paymentSuccess(mapPaymentRequest: MakePaymentRequest) {
        showLog("mapPaymentRequest - ", mapPaymentRequest.toString())
        paymentSuccessfulViewModel.paymentSuccess(mapPaymentRequest)
        paymentSuccessfulViewModel.paymentSuccessResponseLiveData.observe(this) {
            hideLoader()
            when (it) {
                is NetworkResult.Success -> {
                    if (it.data != null) {
                        Log.d(TAG, it.data.toString())
                        Toast.makeText(
                            this,
                            "payment success suvirider" + it.data.message,
                            Toast.LENGTH_SHORT
                        ).show()

                        if (isFromHome.equals("MakePayment")) {
                            if (AppSession.getValue(context, Constants.SCREEN_TYPE) != null) {
                                FlowManager().navigate(context, AppSession.getValue(context, Constants.SCREEN_TYPE))
                            }
                        } else if(isFromHome.equals("AddRecharge")) {
                            startActivity(Intent(this, AddRechargeActivity::class.java))
                            // AppSession.save(context, Constants.FILED_TYPE, it.data.nextFiled)
                            finish()
                        }
                        else {
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }


                    }
                }
                is NetworkResult.Error -> {
                    Log.d(TAG, "Error - " + it.toString())
                    Toast.makeText(this, "payment error suvirider" + it.message, Toast.LENGTH_SHORT)
                        .show()
                }
                is NetworkResult.Loading -> {
                    showLoader()
                    Log.d(TAG, "Loading")
                }
            }
        }
    }

    /* fun showResponse(response: String?) {
         binding.tvResponse.text = response
        *//* MaterialAlertDialogBuilder(this)
            .setMessage(response)
            .setTitle("Payment")
            .setPositiveButton("OK") { dialog: DialogInterface, which: Int ->
                dialog.dismiss()
                val status = hashMap.get("txStatus")
                when(status) {
                    "SUCCESS" -> showToast(hashMap.get("txMsg"))
                    "FAILED" -> showToast(hashMap.get("txMsg"))
                    else -> showToast("$status -> "+hashMap.get("txMsg"))
                }
            }
            .show()*//*
    }*/
}


/*

binding.tvContinue.setOnClickListener {

            if (hashMap != null) {
                val status = hashMap.get("txStatus")
                when (status) {
                    "SUCCESS" -> {

                        /*paymentMode : UPI
  orderId : 644a466017e7e4854d967806
  status : OK
  txTime : 2023-04-27 20:39:34
  referenceId : 1491412362
  txMsg : Transaction Successful
  signature : yEiq6v2mDs3x9L4hghlXCcHom5LQSr8yLTB4mCUh43E=
  orderAmount : 500.00
  txStatus : SUCCESS*/
                        //showToast("check"+hashMap.get("txMsg"))
                        makePaymentRequest.packageId = AppSession.getValue(context,Constants.PACKAGE_ID)!!
                        makePaymentRequest.paymentMode = hashMap.get("paymentMode")
                        makePaymentRequest.orderId = hashMap.get("orderId")
                        makePaymentRequest.status = hashMap.get("status")
                        makePaymentRequest.txTime = hashMap.get("txTime")
                        makePaymentRequest.referenceId = hashMap.get("referenceId")
                        makePaymentRequest.txMsg = hashMap.get("txMsg")
                        makePaymentRequest.signature = hashMap.get("signature")
                        makePaymentRequest.orderAmount = hashMap.get("orderAmount")
                        makePaymentRequest.txStatus = hashMap.get("txStatus")
                        paymentSuccess(makePaymentRequest)
                    }
                    "FAILED" -> showToast(hashMap.get("txMsg"))
                    else -> showToast("$status -> " + hashMap.get("txMsg"))
                }
            }
        }
        */
