package com.lge.kotlinstudyapp.activity

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lge.kotlinstudyapp.adapter.PLPMainAdapter
import com.lge.kotlinstudyapp.db.AdvertiseDto
import com.lge.kotlinstudyapp.db.ProductDto
import com.lge.kotlinstudyapp.repo.Repo
import com.lge.kotlinstudyapp.usecase.PLPUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class PLPViewModel @ViewModelInject constructor(private val useCase: PLPUseCase): ViewModel() {
    val plpList = useCase.getPLPListLiveData()

    fun updatePLPList() {
        viewModelScope.launch(Dispatchers.Main) {
            useCase.updatePLPList()
        }
    }
}