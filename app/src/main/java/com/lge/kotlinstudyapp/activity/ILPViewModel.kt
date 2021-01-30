package com.lge.kotlinstudyapp.activity

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.lge.kotlinstudyapp.usecase.ILPUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ILPViewModel @ViewModelInject constructor(useCase: ILPUseCase): ViewModel() {
    val itemListFlow = useCase.getItemListFlow().cachedIn(viewModelScope)
}