package com.suviridedriver.ui.ride_waiting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.suviridedriver.R
import com.suviridedriver.utils.BaseActivity
import com.suviridedriver.utils.Constants
import com.suviridedriver.utils.FlowManager
import com.suviridedriver.utils.NetworkResult

class RideWatingActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ride_wating)

        /*registrationViewModel.documentsVerificationResponseLiveData.observe(this) {
            when (it) {
                is NetworkResult.Success -> {
                    hideLoader()
                    if (it.data != null) {
                        Log.d(Constants.TAG, it.data.toString())
                        if (it.data.nextScreen != null && it.data.success) {
                            FlowManager().navigate(this, it.data.nextScreen)
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
        }*/
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
       // registrationViewModel.documentsVerification()
    }

    override fun onDestroy() {
        super.onDestroy()
        timeInterval_handler.removeCallbacks(runnable)
    }

}