package com.suviridedriver.ui.bank_details

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.suviridedriver.databinding.ActivityBankDetailBinding
import com.suviridedriver.model.back_detail.BankDetailRequest
import com.suviridedriver.utils.BaseActivity
import com.suviridedriver.utils.Constants
import com.suviridedriver.utils.NetworkResult

class BankDetailActivity : BaseActivity() {
    private lateinit var binding: ActivityBankDetailBinding
    var context: Context = this
    val bankDetailViewModel by viewModels<BankDetailViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBankDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvSave.setOnClickListener {
            submitBankDetails(BankDetailRequest("1236778787","1324fr454"))
        }

    }

    private fun submitBankDetails(bankDetailRequest: BankDetailRequest) {
        bankDetailViewModel.submitBankDetails(bankDetailRequest)
        bankDetailViewModel.bankDetailResponseLiveData.observe(this) {
            hideLoader()
            when (it) {
                is NetworkResult.Success -> {
                    if (it.data != null) {
                        Log.d(Constants.TAG, it.data.toString())
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