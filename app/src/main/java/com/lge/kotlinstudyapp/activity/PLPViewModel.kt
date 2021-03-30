package com.lge.kotlinstudyapp.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lge.kotlinstudyapp.usecase.PLPUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PLPViewModel @Inject constructor(private val useCase: PLPUseCase): ViewModel() {
    val plpList = useCase.getPLPListLiveData()

    fun updatePLPList() {
        viewModelScope.launch(Dispatchers.Main) {
            useCase.updatePLPList()
        }
    }
}
