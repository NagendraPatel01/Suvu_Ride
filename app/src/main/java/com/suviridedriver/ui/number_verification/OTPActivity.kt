package com.suviridedriver.ui.number_verification

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.preference.PreferenceManager
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.messaging.FirebaseMessaging
import com.suviridecustomer.ui.number_verification.GenericKeyEvent
import com.suviridecustomer.ui.number_verification.GenericTextWatcher
import com.suviridedriver.R
import com.suviridedriver.databinding.ActivityOtpactivityBinding
import com.suviridedriver.model.login.LoginRequest
import com.suviridedriver.utils.*
import com.suviridedriver.utils.CommonFunction.getScreenHeight
import com.suviridedriver.utils.Constants.ADDRESS
import com.suviridedriver.utils.Constants.FCM_TOKEN
import com.suviridedriver.utils.Constants.LANGUAGE_ID
import com.suviridedriver.utils.Constants.PROFILE_PIC
import com.suviridedriver.utils.Constants.TAG
import com.suviridedriver.utils.Constants.USER_MOBILE_NUMBER
import com.suviridedriver.utils.Constants.USER_NAME
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@AndroidEntryPoint
class OTPActivity : BaseActivity() {

    private lateinit var binding: ActivityOtpactivityBinding
    var context: Context = this
    private var mAuth: FirebaseAuth? = null
    var number = ""
    private var verificationId: String? = null
    val authViewModel by viewModels<LoginViewModel>()

    @Inject
    lateinit var tokenManager: TokenManager

    private var editText1: EditText? = null
    private var editText2: EditText? = null
    private var editText3: EditText? = null
    private var editText4: EditText? = null
    private var editText5: EditText? = null
    private var editText6: EditText? = null

    val timer = object : CountDownTimer(120000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            //  binding.tvResend.text ="Resend OTP in "+(millisUntilFinished/1000).toInt().toString()+" sec"
            setTimer((millisUntilFinished / 1000).toInt().toString())
        }

        override fun onFinish() {
            binding.tvResend.text = "Resend OTP"
            // setScreenLabelData(Constants.OTP_TAG, "resend", binding.tvResend)
            binding.tvResend.setOnClickListener {
                sendVerificationCode("+91$number")
                otpTimer()
                showToast("resend")
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Create firebase instance.
        mAuth = FirebaseAuth.getInstance()
        //   mAuth!!.getFirebaseAuthSettings().setAppVerificationDisabledForTesting(true)

        // get OTP
        try {
            number = intent.getStringExtra("MOBILE_NUMBER")!!
            binding.tvDescription.text =
                "Please Check Your message and enter the code we have just sent to you $number"
            AppSession.save(context, USER_MOBILE_NUMBER, number)!!
            sendVerificationCode("+91$number")
        } catch (e: Exception) {
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
        }

        binding.ivBack.setOnClickListener {
            finish()
        }

        //show soft keyboard
        Helper.showSoftKeyboard(binding.etOtp1st)

        /*val screenLabelRequest = ScreenLabelRequest("driverScreens", AppSession.getValue(context, LANGUAGE_ID)!!)
        getScreenLabel(screenLabelRequest)
        setScreenLabelData(OTP_SCREEN_CODE, "otp_heading", binding.tvHeading)
        setScreenLabelData(OTP_SCREEN_CODE, "otp_description_1", binding.tvDescription)
        setScreenLabelData(OTP_SCREEN_CODE, "otp_verify", binding.tvVerify)
        setScreenLabelData(OTP_SCREEN_CODE, "otp_description_2", binding.tvDescription2)*/

        setViewSize()

        editText1 = binding.etOtp1st
        editText2 = binding.etOtp2nd
        editText3 = binding.etOtp3rd
        editText4 = binding.etOtp4th
        editText5 = binding.etOtp5th
        editText6 = binding.etOtp6th

        editText1!!.setTextColor(Color.WHITE)
        editText1!!.hint = ""
        editText1!!.setBackgroundResource(R.drawable.after_otp_bg)

        //GenericTextWatcher here works only for moving to next EditText when a number is entered
        //first parameter is the current EditText and second parameter is next EditText

        editText1!!.addTextChangedListener(GenericTextWatcher(editText1!!, editText2))
        editText2!!.addTextChangedListener(GenericTextWatcher(editText2!!, editText3))
        editText3!!.addTextChangedListener(GenericTextWatcher(editText3!!, editText4))
        editText4!!.addTextChangedListener(GenericTextWatcher(editText4!!, editText5))
        editText5!!.addTextChangedListener(GenericTextWatcher(editText5!!, editText6))
        editText6!!.addTextChangedListener(GenericTextWatcher(editText6!!, null))

        //GenericKeyEvent here works for deleting the element and to switch back to previous EditText
        //first parameter is the current EditText and second parameter is previous EditText
        editText1!!.setOnKeyListener(GenericKeyEvent(editText1!!, null))
        editText2!!.setOnKeyListener(GenericKeyEvent(editText2!!, editText1))
        editText3!!.setOnKeyListener(GenericKeyEvent(editText3!!, editText2))
        editText4!!.setOnKeyListener(GenericKeyEvent(editText4!!, editText3))
        editText5!!.setOnKeyListener(GenericKeyEvent(editText5!!, editText4))
        editText6!!.setOnKeyListener(GenericKeyEvent(editText6!!, editText5))

        // showKeyboard(binding.etOtp1st)

        binding.cvVerify.setOnClickListener {
            onVerify()
        }
/*        binding.tvDescription2.setOnClickListener {
            sendVerificationCode(number)

            binding.etOtp1st.setText("")
            binding.etOtp2nd.setText("")
            binding.etOtp3rd.setText("")
            binding.etOtp4th.setText("")
            binding.etOtp5th.setText("")
            binding.etOtp6th.setText("")
        }*/

        allObserver()
        otpTimer()
    }

    fun onVerify() {
        val otpCode = (binding.etOtp1st.text).toString() + (binding.etOtp2nd.text).toString() +
                (binding.etOtp3rd.text).toString() + (binding.etOtp4th.text).toString() +
                (binding.etOtp5th.text).toString() + (binding.etOtp6th.text).toString()

        if (otpCode.length == 6) {
            verifyCode(otpCode.trim())
            showLog("otpVerificationLog", " >6")
        } else {
            Toast.makeText(context, "Please enter valid OTP.", Toast.LENGTH_LONG).show()
        }
    }

    // set edit views size
    private fun setViewSize() {
        val height = (getScreenHeight() * 0.07).toInt()
        binding.etOtp1st.layoutParams.height = height
        binding.etOtp2nd.layoutParams.height = height
        binding.etOtp3rd.layoutParams.height = height
        binding.etOtp4th.layoutParams.height = height
        binding.etOtp5th.layoutParams.height = height
        binding.etOtp6th.layoutParams.height = height
    }


    private fun sendVerificationCode(number: String?) {
        val options = PhoneAuthOptions.newBuilder(mAuth!!)
            .setPhoneNumber(number!!)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(mCallBack)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }


    // OTP callbacks
    private val mCallBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onCodeSent(
                s: String, forceResendingToken: PhoneAuthProvider.ForceResendingToken
            ) {
                // Toast.makeText(this@OTPActivity, "onCodeSent", Toast.LENGTH_SHORT).show()

                super.onCodeSent(s, forceResendingToken)
                verificationId = s
                showLog("otpVerificationLog", " codeSent" + s)
            }

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {

                // Toast.makeText(this@OTPActivity, "onVerificationCompleted", Toast.LENGTH_SHORT).show()
                val code = phoneAuthCredential.smsCode
                if (code != null) {
                    verifyCode(code)
                    showLog("otpVerificationLog", " onVerificationCompleted " + code)
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.d("onVerificationFailed", e.message.toString())
                Toast.makeText(this@OTPActivity, e.message, Toast.LENGTH_SHORT).show()
                // Toast.makeText(this@OTPActivity, "onVerificationFailed", Toast.LENGTH_SHORT).show()
                showLog("otpVerificationLog", " onVerificationFailed")
            }
        }

    private fun verifyCode(code: String) {
        if (verificationId != null) {
            val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
            signInWithCredential(credential)
        }
        /*  else
          {
              callDirectLogin()
          }*/
    }

    //Lokesh
    private fun callDirectLogin() {
        try {
            var fcm_token = ""
            try {
                fcm_token = AppSession.getValue(context, FCM_TOKEN)!!
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val loginRequest = LoginRequest(
                AppSession.getValue(context, LANGUAGE_ID)!!,
                number.trim(),
                fcm_token
            )
            showLog("otpVerificationLog", " task.isSuccessful " + number.trim())
            login(loginRequest)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // firebase signup listener
    private fun signInWithCredential(credential: PhoneAuthCredential) {
        mAuth!!.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // call login api here
                try {
                    var fcm_token = ""
                    try {
                        fcm_token = AppSession.getValue(context, FCM_TOKEN)!!
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    if (fcm_token.isNullOrEmpty()) {
                        getFirebaseToken()
                    } else {
                        val loginRequest = LoginRequest(
                            AppSession.getValue(context, LANGUAGE_ID)!!,
                            number.trim(),
                            fcm_token
                        )
                        showLog("otpVerificationLog", " task.isSuccessful " + number.trim())
                        login(loginRequest)
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                //Toast.makeText(this, "isSuccessful", Toast.LENGTH_SHORT).show()
                // call login api here

            } else if (task.isComplete) {
                Toast.makeText(this, "Please enter valid OTP", Toast.LENGTH_SHORT).show()
            } else if (task.isCanceled) {
                Toast.makeText(this, "isCanceled", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, task.exception!!.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getFirebaseToken() {

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result

                val preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
                preferences.edit().putString(Constants.FCM_TOKEN, token).apply()
                AppSession.save(this, Constants.FCM_TOKEN, token)

                val loginRequest = LoginRequest(
                    AppSession.getValue(context, LANGUAGE_ID)!!,
                    number.trim(),
                    token
                )
                showLog("otpVerificationLog", " task.isSuccessful " + number.trim())
                login(loginRequest)

                // Log and toast
                /*  val msg = getString(R.string.msg_token_fmt, token)
                  Log.d(TAG, msg)
                  Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()*/
            })
    }


    /* // For for get OTP from firebase.
     private fun sendVerificationCode(number: String?) {
         val options = PhoneAuthOptions.newBuilder(mAuth!!)
             .setPhoneNumber(number!!)       // Phone number to verify
             .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
             .setActivity(this)                 // Activity (for callback binding)
             .setCallbacks(mCallBack)          // OnVerificationStateChangedCallbacks
             .build()
         PhoneAuthProvider.verifyPhoneNumber(options)
     }
     // OTP callbacks
     private val mCallBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
         object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

             override fun onCodeSent(
                 s: String, forceResendingToken: PhoneAuthProvider.ForceResendingToken
             ) {
                 // Toast.makeText(this@OTPActivity, "onCodeSent", Toast.LENGTH_SHORT).show()

                 super.onCodeSent(s, forceResendingToken)
                 verificationId = s
             }

             override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {

                 // Toast.makeText(this@OTPActivity, "onVerificationCompleted", Toast.LENGTH_SHORT).show()
                 val code = phoneAuthCredential.smsCode
                 if (code != null) {
                     verifyCode(code)
                 }
             }

             override fun onVerificationFailed(e: FirebaseException) {
                 Log.d("onVerificationFailed", e.message.toString())
                 Toast.makeText(this@OTPActivity, e.message, Toast.LENGTH_SHORT).show()
                 // Toast.makeText(this@OTPActivity, "onVerificationFailed", Toast.LENGTH_SHORT).show()
             }
         }
     // For verify code to enter code by user
     private fun verifyCode(code: String) {
         if (verificationId != null) {
             val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
             signInWithCredential(credential)
         }
     }
     // firebase signup listener
     private fun signInWithCredential(credential: PhoneAuthCredential) {
         mAuth!!.signInWithCredential(credential).addOnCompleteListener { task ->
             if (task.isSuccessful) {
                 // call login api here
                 val loginRequest = LoginRequest(
                     AppSession.getValue(context, LANGUAGE_ID)!!,
                     number.trim(),
                     AppSession.getValue(context, FCM_TOKEN)!!
                 )
                 login(loginRequest)
                 //  Toast.makeText(this, "isSuccessful", Toast.LENGTH_SHORT).show()
             } else if (task.isComplete) {
                 // Toast.makeText(this, "isComplete", Toast.LENGTH_SHORT).show()
             } else if (task.isCanceled) {
                 Toast.makeText(this, "isCanceled", Toast.LENGTH_SHORT).show()
             } else {
                 Toast.makeText(this, task.exception!!.message, Toast.LENGTH_SHORT).show()
             }
         }
     }*/


    // Login api call here
    private fun login(loginRequest: LoginRequest) {
        authViewModel.login(loginRequest)
        showLog("loginResponse", "loginRequest " + loginRequest)
    }

    fun allObserver() {
        authViewModel.loginResponseLiveData.observe(this) {
            hideLoader()
            when (it) {
                is NetworkResult.Success -> {
                    showLog("loginResponse", "loginResponse " + it.data!!)

                    if (it.data.token != null) {
                        TokenManager.saveToken(it.data.token, context)
                        authViewModel.getDriverDetail()
                    }

                    /*if (it.data!!.data != null) {
                        showLog(TAG, it.data.toString())
                        AppSession.save(context, USER_NAME, it.data.data.fullName)
                        AppSession.save(context, USER_MOBILE_NUMBER, it.data.data.mobileNumber.toString())
                        AppSession.save(context, LANGUAGE_ID, it.data.data.language)
                        AppSession.save(context, PROFILE_PIC, it.data.data.selfie)
                        AppSession.save(context, ADDRESS, it.data.data.address)

                        if (it.data.data.token != null) {
                            TokenManager.saveToken(it.data.data.token, this)
                            AppSession.save(context, DRIVER_ID, it.data.data.driverId)
                        }
                    } else {
                        showLog(TAG, "data is null")
                    }

                    if (it.data.nextScreen == "registration") {
                        AppSession.save(context, FILED_TYPE, it.data.nextScreen)
                    }
                    if (it.data.nextScreen == "verification") {
                        AppSession.save(context, FILED_TYPE, it.data.nextField)
                    }
                    FlowManager().navigate(context, it.data.nextScreen)*/
                }
                is NetworkResult.Error -> {
                    Log.d(TAG, "Error - " + it.message)
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
                is NetworkResult.Loading -> {
                    showLoader()
                    Log.d(TAG, "Loading")
                }
            }
        }

        authViewModel.getDriverDetailResponseLiveData.observe(this) {
            hideLoader()
            when (it) {
                is NetworkResult.Success -> {
                    if (it.data != null) {
                        showLog("driver_details", "loginResponse " + it.data)
                        val data = it.data
                        val drivingStatus = data.drivingLicence.documentSubmission
                        val vehiclesStatus = data.vehiclesDetails.documentSubmission
                        val addressStatus = data.drivingLicence.address
                        val takeSelfieStatus = data.takeSelfie.documentSubmission
                        val paymentStatus = data.paymentVerification
                        val isVerification = authViewModel.loginVerification(
                            drivingStatus,
                            vehiclesStatus,
                            addressStatus,
                            takeSelfieStatus,
                            paymentStatus
                        )

                        if (isVerification.first) {
                            AppSession.save(context, USER_NAME, it.data.drivingLicence.fullName)
//                            AppSession.save(context, USER_MOBILE_NUMBER, it.data.mobileNumber.toString())
//                            AppSession.save(context, LANGUAGE_ID, it.data.language)
//                            AppSession.save(context, PROFILE_PIC, it.data.takeSelfie.selfie)
//                            AppSession.save(context, ADDRESS, it.data.drivingLicence.fullName)
                            FlowManager().navigate(context, "home_screen")
                        } else {
                            showToast(isVerification.second)
                            FlowManager().navigate(context, "registration")
                        }


                    }
                }
                is NetworkResult.Error -> {
                    Log.d("driver_details", "Error - " + it.message)
                }
                is NetworkResult.Loading -> {
                    showLoader()
                    Log.d(TAG, "Loading")
                }
            }
        }
    }


    fun otpTimer() {
        timer.start()
    }


    private fun setTimer(str: String) {
        try {

            if (str.toInt() > 60) {
                var sec = str.toInt() % 60
                var min = str.toInt() / 60
                if (sec > 9) {
                    binding.tvResend.text = "Send OTP again in " + 0 + min + ":" + sec + " min"
                } else {
                    binding.tvResend.text = "Send OTP again in " + 0 + min + ":0" + sec + " min"
                }

            } else {
                var min = str.toInt() % 60
                if (min > 9) {
                    binding.tvResend.text = "Send OTP again in 00:" + min + " min"
                } else {
                    binding.tvResend.text = "Send OTP again in 00:0" + min + " min"
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}