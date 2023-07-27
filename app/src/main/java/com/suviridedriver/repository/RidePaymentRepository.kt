package com.suviridedriver.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.suviride.model.success_response.SuccessResponse
import com.suviridedriver.api.APIServices
import com.suviridedriver.model.collect_payment.CollectPayment
import com.suviridedriver.utils.CommonFunction
import com.suviridedriver.utils.NetworkResult
import javax.inject.Inject

class RidePaymentRepository @Inject constructor(private val APIServices: APIServices) {

    private val _collectPaymentResponseLiveData = MutableLiveData<NetworkResult<SuccessResponse>>()
    val collectPaymentResponseLiveData: LiveData<NetworkResult<SuccessResponse>>
        get() = _collectPaymentResponseLiveData

    suspend fun collectPayment(collectPayment: CollectPayment) {
        _collectPaymentResponseLiveData.postValue(NetworkResult.Loading())
        val response = APIServices.collectPayment(collectPayment)
        if (response.isSuccessful && response.body() != null) {
            _collectPaymentResponseLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            /*val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
    _allScreenLabelResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))*/
            _collectPaymentResponseLiveData.postValue(NetworkResult.Error(CommonFunction.getError(response)))
        } else {
            _collectPaymentResponseLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }
}