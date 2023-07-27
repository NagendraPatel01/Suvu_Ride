package com.suviridedriver.ui.earning

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suviridedriver.model.earnings.GetEarningResponse
import com.suviridedriver.repository.EarningRepository
import com.suviridedriver.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EarningViewModel@Inject constructor(private val earningRepository: EarningRepository) : ViewModel() {

    // For get earnings
    val getRidesResponse: LiveData<NetworkResult<GetEarningResponse>>
        get() = earningRepository.getRidesResponse

    fun getEarning() {
        viewModelScope.launch {
            earningRepository.getEarning()
        }
    }
}