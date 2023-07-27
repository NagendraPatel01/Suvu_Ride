package com.suviridedriver.ui.language

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suviridedriver.model.languages.GetLanguagesResponse
import com.suviridedriver.repository.GetLanguagesRepository
import com.suviridedriver.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LanguageViewModel @Inject constructor(private val getLanguageRepository: GetLanguagesRepository) :
    ViewModel() {

    val getLanguagesResponseLiveData: LiveData<NetworkResult<GetLanguagesResponse>>
        get() = getLanguageRepository.languagesResponseLiveData

    fun getLanguages() {
        viewModelScope.launch {
            getLanguageRepository.geLanguages()
        }
    }
}