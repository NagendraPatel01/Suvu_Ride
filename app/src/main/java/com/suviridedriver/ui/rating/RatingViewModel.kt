package com.suviridedriver.ui.rating

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suviride.model.success_response.SuccessResponse
import com.suviridedriver.model.rating.RatingRequest
import com.suviridedriver.repository.RatingRepository
import com.suviridedriver.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RatingViewModel @Inject constructor(private val ratingRepository: RatingRepository) : ViewModel() {

    val submitRatingResponseLiveData: LiveData<NetworkResult<SuccessResponse>>
        get() = ratingRepository.submitRatingResponseLiveData

    fun submitRating(ratingRequest: RatingRequest) {
        viewModelScope.launch {
            ratingRepository.submitRating(ratingRequest)
        }
    }
}