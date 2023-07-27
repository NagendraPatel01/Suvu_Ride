package com.suviridedriver.ui.help_support

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.suviridedriver.R
import com.suviridedriver.databinding.ActivityOtherIssueBinding
import com.suviridedriver.model.issue_submit.IssueSubmitRequest
import com.suviridedriver.utils.BaseActivity
import com.suviridedriver.utils.Constants.TAG
import com.suviridedriver.utils.NetworkResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OtherIssueActivity : BaseActivity() {
    lateinit var binding: ActivityOtherIssueBinding
    val context: Context = this
    val otherIssueViewModel by viewModels<OtherIssueViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtherIssueBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.tvSubject.setOnClickListener {
            val validate = otherIssueViewModel.validateCredentials(
                binding.edtSubject.text.toString().trim(),
                binding.edtEmail.text.toString().trim(),
                binding.edtDescription.text.toString().trim()
            )
            if (validate.first) {
                otherIssueViewModel.submitIssue(
                    IssueSubmitRequest(
                        binding.edtSubject.text.toString().trim(),
                        binding.edtEmail.text.toString().trim(),
                        binding.edtDescription.text.toString().trim()
                    )
                )
            } else {
                showToast(validate.second)
            }
        }

        otherIssueViewModel.otherIssueResponseLiveData.observe(this) {
            hideLoader()
            when (it) {
                is NetworkResult.Success -> {
                    if (it.data != null) {
                        Log.d(TAG, it.data.toString())
                        showToast(it.data.message)
                    }
                }
                is NetworkResult.Error -> {
                    Log.d(TAG, "Error - " + it.message)
                }
                is NetworkResult.Loading -> {
                    showLoader()
                    Log.d(TAG, "Loading")
                }
            }
        }
    }
}