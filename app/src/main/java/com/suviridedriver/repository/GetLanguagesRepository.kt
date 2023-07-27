package com.suviridedriver.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.suviridedriver.api.APIServices
import com.suviridedriver.model.languages.GetLanguagesResponse
import com.suviridedriver.utils.CommonFunction
import com.suviridedriver.utils.NetworkResult
import javax.inject.Inject

class GetLanguagesRepository @Inject constructor(private val APIServices: APIServices) {

    private val _languagesResponseLiveData = MutableLiveData<NetworkResult<GetLanguagesResponse>>()
    val languagesResponseLiveData: LiveData<NetworkResult<GetLanguagesResponse>>
        get() = _languagesResponseLiveData

    suspend fun geLanguages() {
        _languagesResponseLiveData.postValue(NetworkResult.Loading())

        val response = APIServices.getAllLanguages()
        if (response.isSuccessful && response.body() != null) {
            _languagesResponseLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            /*val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
               _allScreenLabelResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))*/
            _languagesResponseLiveData.postValue(NetworkResult.Error(CommonFunction.getError(response)))
        } else {
            _languagesResponseLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }
}