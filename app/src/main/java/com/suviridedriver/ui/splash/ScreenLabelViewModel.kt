package com.suviridedriver.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suviridecustomer.model.screenLabel.ScreenLabelRequest
import com.suviridecustomer.model.screenLabel.ScreenLabelResponse
import com.suviridedriver.repository.GetAllScreenLabelRepository
import com.suviridedriver.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScreenLabelViewModel @Inject constructor(private val getScreenLabelRepository: GetAllScreenLabelRepository) : ViewModel() {

    val getScreenLabelResponseLiveData: LiveData<NetworkResult<ScreenLabelResponse>>
        get() = getScreenLabelRepository.allScreenLabelResponseLiveData

    fun getScreenLabel(screenLabelRequest: ScreenLabelRequest) {
        viewModelScope.launch {
            getScreenLabelRepository.getAllScreenLabel(screenLabelRequest)
        }
    }
}