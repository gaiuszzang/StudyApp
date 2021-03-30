package com.lge.kotlinstudyapp.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.lge.kotlinstudyapp.usecase.ILPUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ILPViewModel @Inject constructor(useCase: ILPUseCase): ViewModel() {
    val itemListFlow = useCase.getItemListFlow().cachedIn(viewModelScope)
}
