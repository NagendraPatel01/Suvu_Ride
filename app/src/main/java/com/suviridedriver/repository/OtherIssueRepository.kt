package com.suviridedriver.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.suviride.model.success_response.SuccessResponse
import com.suviridedriver.api.APIServices
import com.suviridedriver.model.issue_submit.IssueSubmitRequest
import com.suviridedriver.utils.CommonFunction
import com.suviridedriver.utils.NetworkResult
import org.json.JSONObject
import javax.inject.Inject

class OtherIssueRepository @Inject constructor(private val APIServices: APIServices) {

    private val _otherIssueResponseLiveData = MutableLiveData<NetworkResult<SuccessResponse>>()
    val otherIssueResponseLiveData: LiveData<NetworkResult<SuccessResponse>>
        get() = _otherIssueResponseLiveData

    suspend fun submitIssue(issueSubmitRequest: IssueSubmitRequest) {
        _otherIssueResponseLiveData.postValue(NetworkResult.Loading())
        val response = APIServices.submitIssue(issueSubmitRequest)
        if (response.isSuccessful && response.body() != null) {
            _otherIssueResponseLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            /*val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
_allScreenLabelResponseLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))*/
            _otherIssueResponseLiveData.postValue(NetworkResult.Error(CommonFunction.getError(response)))
        } else {
            _otherIssueResponseLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }
}