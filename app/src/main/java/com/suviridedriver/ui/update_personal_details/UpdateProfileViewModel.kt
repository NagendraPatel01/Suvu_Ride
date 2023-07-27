package com.suviridedriver.ui.update_personal_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suviride.model.success_response.SuccessResponse
import com.suviridedriver.model.image_upload.ImageuploadRespons
import com.suviridedriver.model.update_profile.UpdateProfileRequest
import com.suviridedriver.model.update_profile.UpdateProfileResponse
import com.suviridedriver.repository.UpdateProfileRepository
import com.suviridedriver.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class UpdateProfileViewModel @Inject constructor(private val updateProfileRepository: UpdateProfileRepository) :
    ViewModel() {

    val updateProfileResponse: LiveData<NetworkResult<UpdateProfileResponse>>
        get() = updateProfileRepository.updateProfileResponse

    fun updateUserProfile(updateProfileRequest: UpdateProfileRequest) {
        viewModelScope.launch {
            updateProfileRepository.updateUserProfile(updateProfileRequest)
        }
    }

    fun validateCredentials(updateProfileRequest: UpdateProfileRequest): Pair<Boolean, String> {
        var result = Pair(true, "")
        if (updateProfileRequest.fullName!!.isEmpty()) {
            result = Pair(false, "Please change details")
        } else if (updateProfileRequest.mobileNumber!!.isEmpty()) {
            result = Pair(false, "Please change details")
        }else if (updateProfileRequest.address!!.isEmpty()) {
            result = Pair(false, "Please change details")
        }
        return result
    }

    val submitProfilelimage: LiveData<NetworkResult<ImageuploadRespons>>
        get() = updateProfileRepository.submitVehiclimage

    fun imageupload(image: MultipartBody.Part) {
        viewModelScope.launch {
            updateProfileRepository.imageupload(image)
        }
    }
}