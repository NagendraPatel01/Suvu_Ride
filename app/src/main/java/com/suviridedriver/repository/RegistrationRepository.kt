package com.suviridedriver.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.suviridedriver.api.APIServices
import com.suviridedriver.model.verification.DocumentsVerificationResponse
import com.suviridedriver.utils.CommonFunction
import com.suviridedriver.utils.NetworkResult
import org.json.JSONObject
import javax.inject.Inject

class RegistrationRepository @Inject constructor(private val APIServices: APIServices) {

    private val _documentsVerificationResponseLiveData = MutableLiveData<NetworkResult<DocumentsVerificationResponse>>()
    val documentsVerificationResponseLiveData: LiveData<NetworkResult<DocumentsVerificationResponse>>
        get() = _documentsVerificationResponseLiveData

    suspend fun documentsVerification() {
        _documentsVerificationResponseLiveData.postValue(NetworkResult.Loading())

        val response = APIServices.documentsVerification()
        if (response.isSuccessful && response.body() != null) {
            _documentsVerificationResponseLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            /*val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
 _allScreenLabelResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))*/
            _documentsVerificationResponseLiveData.postValue(NetworkResult.Error(CommonFunction.getError(response)))
        } else {
            _documentsVerificationResponseLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }
}