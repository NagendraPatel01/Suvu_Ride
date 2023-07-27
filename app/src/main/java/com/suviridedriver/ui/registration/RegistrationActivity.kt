package com.suviridedriver.ui.registration

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.google.gson.JsonObject
import com.suviridedriver.R
import com.suviridedriver.databinding.ActivityRegistration2Binding
import com.suviridedriver.model.registration.DocumentSubmissionRequest
import com.suviridedriver.ui.bottom_navigation.MainActivity
import com.suviridedriver.ui.registration.adapter.RegistrationAdapter
import com.suviridedriver.ui.waiting_screen.WaitingActivity
import com.suviridedriver.utils.AppSession
import com.suviridedriver.utils.BaseActivity
import com.suviridedriver.utils.Constants
import com.suviridedriver.utils.NetworkResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegistrationActivity : BaseActivity() {
    val context: Context = this
    var arrayList = ArrayList<RegistrationModel>()
    private lateinit var binding: ActivityRegistration2Binding
    val authViewModel by viewModels<RegistrationViewModel2>()
    var drivingCheck = false
    var vehicleCheck = false
    var selfieCheck = false
    var personalDetailsCheck = false
    var paymentCheck = false
    var count = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistration2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btn.setOnClickListener {
            if (count == 1) {
                if (finalStatus()) {
                    startActivity(Intent(context, MainActivity::class.java))
                    finish()
                }
            }
        }
        binding.ivBack.setOnClickListener {
            finishAffinity()
        }
    }

    override fun onResume() {
        super.onResume()
        recycleSetter()

    }

    @SuppressLint("ResourceAsColor")
    fun recycleSetter() {
        val documentSubmissionRequest = DocumentSubmissionRequest(AppSession.getValue(this, Constants.USER_MOBILE_NUMBER)!!)
        authViewModel.getRegistrationCheck()
        authViewModel.getRegistrationResponseLiveData.observe(this) {
            hideLoader()
            when (it) {
                is NetworkResult.Success -> {
                    if (it.data != null) {
                        Log.d(
                            Constants.TAG + " Registration",
                            "Registration success" + it.data.toString()
                        )
                        if (!it.data.drivingLicence.equals("") && !it.data.vehiclesDetails.equals(
                                ""
                            ) && !it.data.selfie.equals("")
                        ) {
                            drivingCheck = status(it.data.drivingLicence)
                            vehicleCheck = status(it.data.vehiclesDetails)
                            selfieCheck = status(it.data.selfie)
                            personalDetailsCheck =
                                status(it.data.personalDetails)
                            paymentCheck = status(it.data   .payment)

                            if (drivingCheck && vehicleCheck && selfieCheck && paymentCheck && personalDetailsCheck) {
                                showLog(
                                    " RegistrationPage",
                                    drivingCheck.toString() + " " + vehicleCheck + " " + selfieCheck + " " + paymentCheck + " " + personalDetailsCheck
                                )
                                binding.btn.setBackgroundResource(R.color.yellow)
                                count = 1

                            } else {
                                binding.btn.setBackgroundResource(R.color.gray)
                                count = 0
                            }
                            arrayList.clear()
                            arrayList.add(
                                RegistrationModel(
                                    R.drawable.reg_img11,
                                    "Driving Licence",
                                    "Upload Your Driving Licence Details",
                                    drivingCheck
                                )
                            )
                            arrayList.add(
                                RegistrationModel(
                                    R.drawable.reg_img12,
                                    "Vehicle Details",
                                    "Upload your registration card Details ",
                                    vehicleCheck
                                )
                            )
                            arrayList.add(
                                RegistrationModel(
                                    R.drawable.ic_address,
                                    "Address Details",
                                    "Upload your Address Details ",
                                    personalDetailsCheck
                                )
                            )
                            arrayList.add(
                                RegistrationModel(
                                    R.drawable.reg_img14,
                                    "Take Selfie",
                                    "Take a clear selfie for your profile.",
                                    selfieCheck
                                )
                            )
                            arrayList.add(
                                RegistrationModel(
                                    R.drawable.reg_img15,
                                    "Make Payment",
                                    "Get Your Rides",
                                    paymentCheck
                                )
                            )

                            val ra = RegistrationAdapter(context, arrayList)
                            binding.recycle.adapter = ra
                        }
                    }
                }
                is NetworkResult.Error -> {
                    Log.d(Constants.TAG + " Registration", "Error - Registration" + it.message)
                }
                is NetworkResult.Loading -> {
                    showLoader()
                    Log.d(Constants.TAG + " Registration", "Loading")
                }
            }
        }
    }

    fun status(check: String): Boolean {
        return !check.equals("pending")
    }

    fun finalStatus(): Boolean {
        if (vehicleCheck && selfieCheck && drivingCheck
            && paymentCheck && personalDetailsCheck
        ) {
            return true
        }
        return false
    }
}