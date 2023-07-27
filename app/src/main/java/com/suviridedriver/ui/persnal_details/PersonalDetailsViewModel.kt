package com.suviridedriver.ui.persnal_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suviride.model.success_response.SuccessResponse
import com.suviridedriver.model.state_city.AddStateCityRequest
import com.suviridedriver.model.state_city.CityResponse
import com.suviridedriver.model.state_city.StateResponse
import com.suviridedriver.repository.PersonalDetailsRepository
import com.suviridedriver.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonalDetailsViewModel @Inject constructor(private val personalDetailsRepository: PersonalDetailsRepository) :
    ViewModel() {

    val stateResponseResponse: LiveData<NetworkResult<StateResponse>>
        get() = personalDetailsRepository.stateResponseResponse

    fun getState() {
        viewModelScope.launch {
            personalDetailsRepository.getState()
        }
    }

    val cityResponse: LiveData<NetworkResult<CityResponse>>
        get() = personalDetailsRepository.cityResponse

    fun getCities(stateId: String) {
        viewModelScope.launch {
            personalDetailsRepository.getCities(stateId)
        }
    }

    val addStateCityResponse: LiveData<NetworkResult<SuccessResponse>>
        get() = personalDetailsRepository.addStateCityResponse

    fun updateStateCity(addStateCityRequest: AddStateCityRequest) {
        viewModelScope.launch {
            personalDetailsRepository.updateStateCity(addStateCityRequest)
        }
    }

    fun validateCredentials(
        houseNumber: String,
        streetName: String,
        stateId: String,
        cityId: String,
        areaPincode: String,
    ): Pair<Boolean, String> {
        var result = Pair(true, "")
        if (houseNumber.isBlank()) {
            result = Pair(false, "Please enter House Number.")
        } else if (streetName.isBlank()) {
            result = Pair(false, "Please enter Street Name.")
        } else if (stateId.isBlank()) {
            result = Pair(false, "Please select State.")
        } else if (cityId.isBlank()) {
            result = Pair(false, "Please select City.")
        } else if (areaPincode.isBlank()) {
            result = Pair(false, "Please enter Area Pin Code.")
        } else if (areaPincode.length != 6) {
            result = Pair(false, "Please enter valid Area Pin Code.")
        }
        return result
    }


}