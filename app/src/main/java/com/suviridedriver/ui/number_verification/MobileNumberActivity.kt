package com.suviridedriver.ui.number_verification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.suviridedriver.databinding.ActivityMobileNumberBinding
import com.suviridedriver.utils.BaseActivity
import com.suviridedriver.utils.Helper.Companion.showSoftKeyboard


class MobileNumberActivity : BaseActivity() {
    lateinit var binding: ActivityMobileNumberBinding
    var context: Context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMobileNumberBinding.inflate(layoutInflater)
        setContentView(binding.root)

       // showKeyboard(binding.edtNumber)

        binding.ivBack.setOnClickListener {
            finish()
        }
        //show soft keyboard
        showSoftKeyboard(binding.edtNumber)

       /* val screenLabelRequest = ScreenLabelRequest("driverScreens", AppSession.getValue(context, Constants.LANGUAGE_ID)!!)
        // getScreenLabel(screenLabelRequest)
        setScreenLabelData(Constants.USER_MOBILE_NUMBER_SCREEN_CODE, "mobile_number_heading", binding.tvHeading)
        setScreenLabelData(Constants.USER_MOBILE_NUMBER_SCREEN_CODE, "mobile_number_description_1", binding.tvDescription1)
        setScreenLabelData(Constants.USER_MOBILE_NUMBER_SCREEN_CODE, "mobile_number_next", binding.tvNext)
        setScreenLabelData(Constants.USER_MOBILE_NUMBER_SCREEN_CODE, "mobile_number_description_2", binding.tvDescription2)
    */
    }

    fun onContinue(view: View) {
        if (binding.edtNumber.text.toString().length == 10){
            val intent = Intent(this, OTPActivity::class.java)
            intent.putExtra("MOBILE_NUMBER", "${binding.edtNumber.text.trim()}")
            startActivity(intent)
        }
        else{
            showToast("Please enter correct number.")
        }
    }
}