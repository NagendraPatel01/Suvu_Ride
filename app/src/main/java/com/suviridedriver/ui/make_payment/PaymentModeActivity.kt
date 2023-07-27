package com.suviridedriver.ui.make_payment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.cashfree.pg.CFPaymentService
import com.cashfree.pg.ui.gpay.GooglePayStatusListener
import com.google.gson.JsonObject
import com.suviridedriver.R
import com.suviridedriver.databinding.ActivityPaymentModeBinding
import com.suviridedriver.ui.make_payment.network.API
import com.suviridedriver.ui.make_payment.network.APIClient
import com.suviridedriver.ui.make_payment.network.ExampleResponse
import com.suviridedriver.utils.BaseActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaymentModeActivity : BaseActivity() {
    val context: Context = this
    private lateinit var binding: ActivityPaymentModeBinding
    private var identityService: API = APIClient.identityService.create(API::class.java)
    var currentMode: SeamlessMode = SeamlessMode.CARD
    var orderID = ""
    var orderCurrency = ""
    var orderAmount = 0
    var discount = 0
    var intentFrom = ""

    // on below line we are creating variables.
    lateinit var radioGroup: RadioGroup

    enum class SeamlessMode {
        CARD, WALLET, NET_BANKING, UPI_COLLECT, PAY_PAL
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentModeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras = intent.extras
        orderID = extras!!.getString("orderId")!!
        orderAmount = extras.getString("orderAmount")!!.toInt()
        orderCurrency = extras.getString("orderCurrency")!!
        intentFrom = extras.getString("intentFrom")!!

        when (intentFrom) {
            "MakePayment" -> {
                showToast("Intent From - $intentFrom")
            }
            "AddRecharge" -> {
                showToast("Intent From - $intentFrom")
                // discount = extras.getString("discount")!!.toInt()
                discount = 20
            }
            else -> {showToast("Intent From Other Activity - $intentFrom")}
        }

       /* if (extras != null) {
            orderID = extras.getString("orderId")!!
            orderAmount = extras.getString("orderAmount")!!.toInt()
            orderCurrency = extras.getString("orderCurrency")!!
            intentFrom = extras.getString("intentFrom")!!

            when (intentFrom) {
                "MakePayment" -> {
                    showToast("Intent From - $intentFrom")
                }
                "AddRecharge" -> {
                    showToast("Intent From - $intentFrom")
                    // discount = extras.getString("discount")!!.toInt()
                    discount = 20
                }
                else -> {showToast("Intent From Other Activity - $intentFrom")}
            }
        }*/

        /* try {

         } catch (e: Exception) {
             showToast(e.toString())
         }*/

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.upi.setPadding(15, 0, 0, 0)
        binding.gpay.setPadding(15, 0, 0, 0)
        binding.phonePe.setPadding(15, 0, 0, 0)
        binding.amazon.setPadding(15, 0, 0, 0)
        binding.web.setPadding(15, 0, 0, 0)

        radioGroup = findViewById(R.id.radioGroup)
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = findViewById<RadioButton>(checkedId)
            binding.btnProceed.setOnClickListener {
                getToken(radioButton)
            }
        }
    }


    fun getToken(view: View) {
        Log.d("TAG_TAG", "getToken")
        try {
            val jsonMain = JsonObject()
            jsonMain.addProperty("orderId", orderID)
            jsonMain.addProperty("orderAmount", orderAmount)
            jsonMain.addProperty("orderCurrency", "INR")

            Log.d("TAG_TAG", "jsonMain " + jsonMain)

            val api = identityService.getToken(jsonMain)
            api.enqueue(object : Callback<ExampleResponse> {
                override fun onResponse(
                    call: Call<ExampleResponse>,
                    response: Response<ExampleResponse>
                ) {
                    try {
                        Toast.makeText(
                            this@PaymentModeActivity,
                            "" + response.body()!!.message,
                            Toast.LENGTH_LONG
                        ).show()

                        // response.body()

                        Log.d("TAG_TAG", "cf token : " + response.body()!!.cftoken.trim())
                        val token = response.body()!!.cftoken.trim()
                        val stage = "PROD"

                        // Show the UI for doGPayPayment and phonePePayment only after checking if the apps are ready for payment
                        if (view.id == R.id.phonePe_exists) {
                            Toast.makeText(
                                this@PaymentModeActivity,
                                CFPaymentService.getCFPaymentServiceInstance()
                                    .doesPhonePeExist(this@PaymentModeActivity, stage)
                                    .toString() + "",
                                Toast.LENGTH_SHORT
                            ).show()
                            return
                        } else if (view.id == R.id.gpay_ready) {
                            CFPaymentService.getCFPaymentServiceInstance()
                                .isGPayReadyForPayment(this@PaymentModeActivity, object :
                                    GooglePayStatusListener {
                                    override fun isReady() {
                                        Toast.makeText(
                                            this@PaymentModeActivity,
                                            "Ready",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                    override fun isNotReady() {
                                        Toast.makeText(
                                            this@PaymentModeActivity,
                                            "Not Ready",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                })
                            return
                        }

                        val cfPaymentService = CFPaymentService.getCFPaymentServiceInstance()
                        cfPaymentService.setOrientation(0)
                        when (view.id) {
                            R.id.web -> {
                                cfPaymentService.doPayment(
                                    this@PaymentModeActivity,
                                    getInputParams(),
                                    token,
                                    stage,
                                    "#784BD2",
                                    "#FFFFFF",
                                    false
                                )
                            }
                            R.id.upi -> {
                                // cfPaymentService.selectUpiClient("com.google.android.apps.nbu.paisa.user");
                                cfPaymentService.upiPayment(
                                    this@PaymentModeActivity,
                                    getInputParams(),
                                    token,
                                    stage
                                )
                            }
                            R.id.amazon -> {
                                cfPaymentService.doAmazonPayment(
                                    this@PaymentModeActivity,
                                    getInputParams(),
                                    token,
                                    stage
                                )
                            }
                            R.id.gpay -> {
                                cfPaymentService.gPayPayment(
                                    this@PaymentModeActivity,
                                    getInputParams(),
                                    token,
                                    stage
                                )
                            }
                            R.id.phonePe -> {
                                cfPaymentService.phonePePayment(
                                    this@PaymentModeActivity,
                                    getInputParams(),
                                    token,
                                    stage
                                )
                            }
                            R.id.web_seamless -> {
                                cfPaymentService.doPayment(
                                    this@PaymentModeActivity,
                                    getSeamlessCheckoutParams(),
                                    token,
                                    stage
                                )
                            }
                        }

                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                        Log.d("TAG_TAG", "1 try : " + e.toString())
                    }
                }

                override fun onFailure(call: Call<ExampleResponse>, t: Throwable) {
                    Toast.makeText(this@PaymentModeActivity, "" + t.message, Toast.LENGTH_LONG)
                        .show()
                    Log.d("TAG_TAG", "Failed :" + t.message)
                }
            })

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Log.d("TAG_TAG", "2 try" + e.toString())
        }
    }

    private fun getSeamlessCheckoutParams(): MutableMap<String, String>? {
        val params: MutableMap<String, String> = java.util.HashMap()
        when (currentMode) {
            SeamlessMode.CARD -> {
                params[CFPaymentService.PARAM_PAYMENT_OPTION] = "card"
                params[CFPaymentService.PARAM_CARD_NUMBER] = "VALID_CARD_NUMBER"
                params[CFPaymentService.PARAM_CARD_YYYY] = "YYYY"
                params[CFPaymentService.PARAM_CARD_MM] = "MM"
                params[CFPaymentService.PARAM_CARD_HOLDER] = "CARD_HOLDER_NAME"
                params[CFPaymentService.PARAM_CARD_CVV] = "CVV"
            }
            SeamlessMode.WALLET -> {
                params[CFPaymentService.PARAM_PAYMENT_OPTION] = "wallet"
                params[CFPaymentService.PARAM_WALLET_CODE] = "4007"
            }
            SeamlessMode.NET_BANKING -> {
                params[CFPaymentService.PARAM_PAYMENT_OPTION] = "nb"
                params[CFPaymentService.PARAM_BANK_CODE] = "3333"
            }
            SeamlessMode.UPI_COLLECT -> {
                params[CFPaymentService.PARAM_PAYMENT_OPTION] = "upi"
                params[CFPaymentService.PARAM_UPI_VPA] = "9644707485@ybl"
            }
            SeamlessMode.PAY_PAL -> params[CFPaymentService.PARAM_PAYMENT_OPTION] = "paypal"
        }

        return params
    }

    private fun getInputParams(): Map<String, String>? {
        val appId = "TEST370160395206780c433649d46c061073"
        val orderId = orderID
        val orderAmount = orderAmount
        val orderNote = "Test Order"
        val customerName = "John Doe"
        val customerPhone = "9900012345"
        val customerEmail = "test@gmail.com"
        val params: MutableMap<String, String> = java.util.HashMap()
        params[CFPaymentService.PARAM_APP_ID] = appId
        params[CFPaymentService.PARAM_ORDER_ID] = orderId
        params[CFPaymentService.PARAM_ORDER_AMOUNT] = orderAmount.toString()
        params[CFPaymentService.PARAM_ORDER_NOTE] = orderNote
        params[CFPaymentService.PARAM_CUSTOMER_NAME] = customerName
        params[CFPaymentService.PARAM_CUSTOMER_PHONE] = customerPhone
        params[CFPaymentService.PARAM_CUSTOMER_EMAIL] = customerEmail
        params[CFPaymentService.PARAM_ORDER_CURRENCY] = "INR"
        return params
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //Same request code for all payment APIs.
        Log.d(TAG, "ReqCode : " + CFPaymentService.REQ_CODE)
        Log.d(TAG, "APIResponse"+data)

        if (requestCode == CFPaymentService.REQ_CODE && data != null) {
            val bundle = data.extras
            Log.d("jgjufhuju", "tjtjgtj"+bundle)
            if (bundle != null) {
                val intent = Intent(applicationContext, PaymentSuccessfulActivity::class.java)
                intent.putExtra("orderId", orderID)
                intent.putExtra("orderAmount", "" + orderAmount)
                intent.putExtra("orderCurrency", orderCurrency)
                intent.putExtra("intentFrom", intentFrom)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        }
    }

    companion object {
        private const val TAG = "PaymentModeActivity"
    }
}