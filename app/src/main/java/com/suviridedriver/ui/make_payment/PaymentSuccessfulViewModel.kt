package com.suviridedriver.ui.make_payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suviridedriver.model.make_payment.MakePaymentRequest
import com.suviridedriver.model.make_payment.PaymentSuccessResponse
import com.suviridedriver.repository.PaymentSuccessfulRepository
import com.suviridedriver.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentSuccessfulViewModel @Inject constructor(private val paymentSuccessfulRepository: PaymentSuccessfulRepository) :
    ViewModel() {

    val paymentSuccessResponseLiveData: LiveData<NetworkResult<PaymentSuccessResponse>>
        get() = paymentSuccessfulRepository.paymentSuccessResponseLiveData

    fun paymentSuccess(mapPaymentRequest: MakePaymentRequest) {
        viewModelScope.launch {
            paymentSuccessfulRepository.paymentSuccess(mapPaymentRequest)
        }
    }
}