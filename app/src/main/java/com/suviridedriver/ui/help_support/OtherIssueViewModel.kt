package com.suviridedriver.ui.help_support

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suviride.model.success_response.SuccessResponse
import com.suviridedriver.model.issue_submit.IssueSubmitRequest
import com.suviridedriver.repository.OtherIssueRepository
import com.suviridedriver.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtherIssueViewModel @Inject constructor(private val otherIssueRepository: OtherIssueRepository) : ViewModel() {

    val otherIssueResponseLiveData: LiveData<NetworkResult<SuccessResponse>>
        get() = otherIssueRepository.otherIssueResponseLiveData

    fun submitIssue(issueSubmitRequest: IssueSubmitRequest) {
        viewModelScope.launch {
            otherIssueRepository.submitIssue(issueSubmitRequest)
        }
    }


    fun validateCredentials(subject: String, email: String, description: String): Pair<Boolean, String> {
        var result = Pair(true, "")
        if (subject.isEmpty()) {
            result = Pair(false, "Enter subject")
        } else if (email.isEmpty()) {
            result = Pair(false, "Enter email")
        }else if (description.isEmpty()) {
            result = Pair(false, "Enter description")
        }
        return result
    }
}