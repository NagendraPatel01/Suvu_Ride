package com.suviridedriver.ui.ride_payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suviride.model.success_response.SuccessResponse
import com.suviridedriver.model.collect_payment.CollectPayment
import com.suviridedriver.repository.RidePaymentRepository
import com.suviridedriver.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RidePaymentViewModel @Inject constructor(private val ridePaymentRepository: RidePaymentRepository) :
    ViewModel() {

    val collectPaymentResponseLiveData: LiveData<NetworkResult<SuccessResponse>>
        get() = ridePaymentRepository.collectPaymentResponseLiveData

    fun collectPayment(collectPayment: CollectPayment) {
        viewModelScope.launch {
            ridePaymentRepository.collectPayment(collectPayment)
        }
    }
}