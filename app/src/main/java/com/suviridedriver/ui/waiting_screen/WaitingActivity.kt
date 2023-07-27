package com.suviridedriver.ui.waiting_screen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import com.suviridedriver.R
import com.suviridedriver.ui.registration.RegistrationActivity
import com.suviridedriver.ui.registration.RegistrationViewModel
import com.suviridedriver.utils.BaseActivity
import com.suviridedriver.utils.Constants
import com.suviridedriver.utils.FlowManager
import com.suviridedriver.utils.NetworkResult

class WaitingActivity : BaseActivity() {
    val ivBack: ImageView get() = findViewById(R.id.ivBack)
    val tvMsg: TextView get() = findViewById(R.id.tvMsg)
    val context: Context = this
    val registrationViewModel by viewModels<RegistrationViewModel>()

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, RegistrationActivity::class.java))
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiting)
        showLoader()
        ivBack.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
            finish()
        }

        registrationViewModel.documentsVerificationResponseLiveData.observe(this) {
            when (it) {
                is NetworkResult.Success -> {
                    hideLoader()
                    if (it.data != null) {
                        Log.d(Constants.TAG, it.data.toString())
                        if (it.data.success) {
                            FlowManager().navigate(this, "home_screen")
                            timeInterval_handler.removeCallbacks(runnable)
                            isHandlerRunning = false
                            hideLoader()
                            showLog("handler", "handler stop")
                            finish()
                        } else {
                            tvMsg.text = it.data.message
                        }
                    }
                }
                is NetworkResult.Error -> {
                    Log.d(Constants.TAG, "Error - " + it.message)
                    Toast.makeText(this, "Error " + it.message, Toast.LENGTH_SHORT).show()
                    hideLoader()
                }
                is NetworkResult.Loading -> {
                    Log.d(Constants.TAG, "Loading")
                }
                else -> {}
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!isOnResumeCalled) {
            isOnResumeCalled = true
            timeInterval_handler.postDelayed(runnable, 1000)
        }
    }

    override fun onStop() {
        super.onStop()
        if (isOnResumeCalled) {
            isOnResumeCalled = false
            timeInterval_handler.removeCallbacks(runnable)
        }
    }

    // handler for get driver current location
    val timeInterval_handler = Handler()
    var isHandlerRunning = false
    var isOnResumeCalled = false
    var runnable: Runnable = object : Runnable {
        override fun run() {
            timeInterval_handler.removeCallbacks(this)
            if (true) {
                timeInterval_handler.postDelayed(this, 3000)
                isHandlerRunning = false
                showLog("handler", "handler start")
                documentsVerification()
            }
        }
    }

    private fun documentsVerification() {
        registrationViewModel.documentsVerification()
    }

    override fun onDestroy() {
        super.onDestroy()
        timeInterval_handler.removeCallbacks(runnable)
    }

}