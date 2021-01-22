package com.lge.kotlinstudyapp.activity

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.lge.kotlinstudyapp.usecase.MusicActivityUseCase

class MusicViewModel @ViewModelInject constructor(private val useCase: MusicActivityUseCase) : ViewModel() {
    companion object {
        private const val TAG = "MusicViewModel"
    }
    val musicList = useCase.getMusicListLiveData()
    fun connect() = useCase.connect()
    fun disconnect() = useCase.disconnect()
    fun play() = useCase.play()
}