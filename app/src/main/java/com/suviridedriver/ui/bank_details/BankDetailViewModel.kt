package com.suviridedriver.ui.bank_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suviridedriver.model.back_detail.BankDetailRequest
import com.suviridedriver.model.back_detail.BankDetailResponse
import com.suviridedriver.model.driving_licence_detail.SubmitDrivingLicenceRequest
import com.suviridedriver.repository.BankDetailRepository
import com.suviridedriver.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BankDetailViewModel @Inject constructor(private val bankDetailRepository: BankDetailRepository) :
    ViewModel() {

    val bankDetailResponseLiveData: LiveData<NetworkResult<BankDetailResponse>>
        get() = bankDetailRepository.bankDetailResponseLiveData

    fun submitBankDetails(bankDetailRequest: BankDetailRequest) {
        viewModelScope.launch {
            bankDetailRepository.submitBankDetails(bankDetailRequest)
        }
    }

    fun validateCredentials(submitDrivingLicenceRequest: SubmitDrivingLicenceRequest): Pair<Boolean, String> {
        var result = Pair(true, "")
        if (submitDrivingLicenceRequest.fullName!!.isEmpty() && submitDrivingLicenceRequest.fullName!!.length < 3) {
            result = Pair(false, "Please provide the full name")
        }/* else if (submitDrivingLicenceRequest.address!!.isEmpty() && submitDrivingLicenceRequest.address!!.length < 4) {
            result = Pair(false, "Please provide the address")
        } */else if (submitDrivingLicenceRequest.gender!!.equals("Select gender")) {
            result = Pair(false, "Please select gender")
        } else if (submitDrivingLicenceRequest.licenceNumber!!.isEmpty() && submitDrivingLicenceRequest.licenceNumber!!.length < 16) {
            result = Pair(false, "Please provide the licence number")
        }else if (submitDrivingLicenceRequest.issuedDate == null) {
            result = Pair(false, "Please select issue date")
        }else if (submitDrivingLicenceRequest.validitiy == null) {
            result = Pair(false, "Please select validity date")
        }

        return result
    }
}