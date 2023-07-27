package com.suviridedriver.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.suviridedriver.api.APIServices
import com.suviridedriver.model.back_detail.BankDetailResponse
import com.suviridedriver.model.back_detail.BankDetailRequest
import com.suviridedriver.utils.CommonFunction
import com.suviridedriver.utils.NetworkResult
import org.json.JSONObject
import javax.inject.Inject

class BankDetailRepository @Inject constructor(private val APIServices: APIServices) {

    private val _bankDetailResponseLiveData = MutableLiveData<NetworkResult<BankDetailResponse>>()
    val bankDetailResponseLiveData: LiveData<NetworkResult<BankDetailResponse>>
        get() = _bankDetailResponseLiveData

    suspend fun submitBankDetails(bankDetailRequest: BankDetailRequest) {
        _bankDetailResponseLiveData.postValue(NetworkResult.Loading())

        val response = APIServices.submitBankDetails(bankDetailRequest)
        if (response.isSuccessful && response.body() != null) {
            _bankDetailResponseLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            /*val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
             _allScreenLabelResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))*/
            _bankDetailResponseLiveData.postValue(NetworkResult.Error(CommonFunction.getError(response)))
        } else {
            _bankDetailResponseLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }
}