package com.suviridedriver.ui.driving_licence

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suviridedriver.model.driving_licence_detail.SubmitDrivingLicenceRequest
import com.suviridedriver.model.driving_licence_detail.SubmitDrivingLicenceResponse
import com.suviridedriver.model.image_upload.ImageuploadRespons
import com.suviridedriver.repository.DrivingLicenceRepository
import com.suviridedriver.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class DrivingLicenceViewModel @Inject constructor(private val drivingLicenceRepository: DrivingLicenceRepository) :
    ViewModel() {


    val submitVehiclimage: LiveData<NetworkResult<ImageuploadRespons>>
        get() = drivingLicenceRepository.submitVehiclimage

    fun imageupload(image: MultipartBody.Part) {
        viewModelScope.launch {
            drivingLicenceRepository.imageupload(image)
        }
    }

    val submitDrivingLicenceLiveData: LiveData<NetworkResult<SubmitDrivingLicenceResponse>>
        get() = drivingLicenceRepository.submitDrivingLicenceLiveData

    fun submitDrivingLicenceDetails(submitDrivingLicenceRequest: SubmitDrivingLicenceRequest) {
        viewModelScope.launch {
            drivingLicenceRepository.submitDrivingLicenceDetails(submitDrivingLicenceRequest)
        }
    }


    fun validateCredentials(submitDrivingLicenceRequest: SubmitDrivingLicenceRequest): Pair<Boolean, String> {
        var result = Pair(true, "")
        if (submitDrivingLicenceRequest.fullName!!.isEmpty() || submitDrivingLicenceRequest.fullName!!.length < 3) {
            result = Pair(false, "Please provide the full name")
        } /*else if (submitDrivingLicenceRequest.address!!.isEmpty() || submitDrivingLicenceRequest.address!!.length < 4) {
            result = Pair(false, "Please provide the address")
        }*/ else if (submitDrivingLicenceRequest.gender!!.equals("Select gender")) {
            result = Pair(false, "Please select gender")
        } else if (submitDrivingLicenceRequest.licenceNumber!!.isEmpty() || submitDrivingLicenceRequest.licenceNumber!!.length < 10) {
            result = Pair(false, "Please provide the licence number")
        }else if (submitDrivingLicenceRequest.issuedDate.equals("")) {
            result = Pair(false, "Please select issue date")
        }else if (submitDrivingLicenceRequest.validitiy.equals("")) {
            result = Pair(false, "Please select validity date")
        }

        return result
    }
}