package com.suviridedriver.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.suviridedriver.api.APIServices
import com.suviridedriver.model.make_payment.MakePaymentRequest
import com.suviridedriver.model.make_payment.PaymentSuccessResponse
import com.suviridedriver.utils.CommonFunction
import com.suviridedriver.utils.NetworkResult
import javax.inject.Inject

class PaymentSuccessfulRepository @Inject constructor(private val APIServices: APIServices) {

    private val _paymentSuccessResponseLiveData = MutableLiveData<NetworkResult<PaymentSuccessResponse>>()
    val paymentSuccessResponseLiveData: LiveData<NetworkResult<PaymentSuccessResponse>>
        get() = _paymentSuccessResponseLiveData

    suspend fun paymentSuccess(mapPaymentRequest: MakePaymentRequest) {
        _paymentSuccessResponseLiveData.postValue(NetworkResult.Loading())

        val response = APIServices.paymentSuccess(mapPaymentRequest)
        if (response.isSuccessful && response.body() != null) {
            _paymentSuccessResponseLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            /*val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
_allScreenLabelResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))*/
            _paymentSuccessResponseLiveData.postValue(NetworkResult.Error(CommonFunction.getError(response)))
        } else {
            _paymentSuccessResponseLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }
}