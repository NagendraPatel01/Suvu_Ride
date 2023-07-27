package com.suviridedriver.ui.language

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.suviridedriver.databinding.ActivityLanguageBinding
import com.suviridedriver.utils.BaseActivity
import com.suviridedriver.utils.Constants
import com.suviridedriver.utils.NetworkResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LanguageActivity : BaseActivity() {
    private lateinit var binding: ActivityLanguageBinding
    var context: Context = this
    val authViewModel by viewModels<LanguageViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // get languages using api.
        getLanguages()
    }

    private fun getLanguages() {
        authViewModel.getLanguages()
        authViewModel.getLanguagesResponseLiveData.observe(this) {
            hideLoader()
            when (it) {
                is NetworkResult.Success -> {
                    Log.d("language", "language - "+it.data.toString())

                    if (it.data != null) {
                        if (it.data !=null) {
                            val adapter = LanguageAdapter(context, it.data.data, binding.tvContinue)
                            val linearLayoutManager =
                                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                            binding.rvLanguageList.layoutManager = linearLayoutManager
                            binding.rvLanguageList.adapter = adapter
                        }
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