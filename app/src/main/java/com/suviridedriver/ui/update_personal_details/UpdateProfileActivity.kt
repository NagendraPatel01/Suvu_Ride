package com.suviridedriver.ui.update_personal_details

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.suviridedriver.databinding.ActivityUpdateProfileBinding
import com.suviridedriver.utils.AppSession
import com.suviridedriver.utils.BaseActivity
import com.suviridedriver.utils.Constants
import com.suviridedriver.utils.NetworkResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdateProfileActivity : BaseActivity() {
    lateinit var binding: ActivityUpdateProfileBinding
    val context: Context = this
    val updateProfileViewModel by viewModels<UpdateProfileViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        try {
            var name = AppSession.getValue(context, Constants.USER_NAME)
            val number = AppSession.getValue(context, Constants.USER_MOBILE_NUMBER)
            binding.edtUserName.hint = name
            binding.edtUserNumber.hint = number
        } catch (e: Exception) {
            e.printStackTrace()
        }

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.tvUpdate.setOnClickListener {
           // val validateCredentials = updateProfileViewModel.validateCredentials(binding.edtUserName.text.toString().trim(),binding.edtUserNumber.text.toString().trim(),"")
           /* if (validateCredentials.first){
                //updateProfile(binding.edtUserName.text.toString().trim(),binding.edtUserNumber.text.toString().trim())
            }else{
                showToast(validateCredentials.second)
            }*/
        }

    }

    private fun updateProfile(name: String, number: String) {
       // updateProfileViewModel.updateUserProfile(UpdateProfileRequest(name,number))
        updateProfileViewModel.updateProfileResponse.observe(this) {
            hideLoader()
            when (it) {
                is NetworkResult.Success -> {
                    if (it.data != null) {
                        Log.d(Constants.TAG, it.data.toString())
                        showToast(it.data.message)
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