package com.suviridedriver.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.suviridedriver.api.APIServices
import com.suviridedriver.model.take_selfie.TakeSelfieResponse
import com.suviridedriver.utils.CommonFunction
import com.suviridedriver.utils.NetworkResult
import okhttp3.MultipartBody
import org.json.JSONObject
import javax.inject.Inject

class TakeSelfieRepository @Inject constructor(private val APIServices: APIServices) {

    private val _takeSelfieResponseLiveData = MutableLiveData<NetworkResult<TakeSelfieResponse>>()
    val takeSelfieResponseLiveData: LiveData<NetworkResult<TakeSelfieResponse>>
        get() = _takeSelfieResponseLiveData

    suspend fun takeSelfie(selfieImage: MultipartBody.Part) {
        _takeSelfieResponseLiveData.postValue(NetworkResult.Loading())

        val response = APIServices.takeSelfie(selfieImage)

        if (response.isSuccessful && response.body() != null) {
            _takeSelfieResponseLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            /*val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
              _allScreenLabelResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))*/
            _takeSelfieResponseLiveData.postValue(NetworkResult.Error(CommonFunction.getError(response)))
        } else {
            _takeSelfieResponseLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }
}