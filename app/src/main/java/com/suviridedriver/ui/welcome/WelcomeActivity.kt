package com.suviridedriver.ui.welcome

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.suviridecustomer.model.screenLabel.ScreenLabelRequest
import com.suviridedriver.databinding.ActivityWelcomeBinding
import com.suviridedriver.ui.number_verification.MobileNumberActivity
import com.suviridedriver.utils.AppSession
import com.suviridedriver.utils.BaseActivity
import com.suviridedriver.utils.Constants

class WelcomeActivity : BaseActivity() {
    private lateinit var binding: ActivityWelcomeBinding
    var context: Context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppSession.save(context, Constants.SCREEN_TYPE, "welcome")

        val screenLabelRequest = ScreenLabelRequest("customerScreens", AppSession.getValue(context, Constants.LANGUAGE_ID)!!)
        getScreenLabel(screenLabelRequest)
        setScreenLabelData(Constants.WELCOME_SCREEN_CODE, "explore_new_ways", binding.tvTitle)
        setScreenLabelData(Constants.WELCOME_SCREEN_CODE, "continue_with_number", binding.tvContinue)
        setScreenLabelData(Constants.WELCOME_SCREEN_CODE, "or_continue_with", binding.tvOrContinueWith)

        setScreenLabelData(Constants.WELCOME_SCREEN_CODE, "terms_and_condition", binding.tvTermsCondition)

        binding.tvContinue.setOnClickListener {

            startActivity(Intent(context,MobileNumberActivity::class.java))
        }

        binding.ivBack.setOnClickListener {
           finish()
        }

    }
}