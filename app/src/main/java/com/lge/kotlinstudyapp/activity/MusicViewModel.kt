package com.lge.kotlinstudyapp.activity

import android.media.session.PlaybackState
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.lge.kotlinstudyapp.usecase.MusicActivityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MusicViewModel @Inject constructor(private val useCase: MusicActivityUseCase) : ViewModel() {
    companion object {
        private const val TAG = "MusicViewModel"
    }
    val musicList = useCase.getMusicListLiveData()
    val playMusic = useCase.getPlayMusicLiveData()
    val playStatePlaying = Transformations.map(useCase.getPlayStateLiveData()) { it.state == PlaybackState.STATE_PLAYING || it.state == PlaybackState.STATE_BUFFERING }
    val playPosValue = Transformations.map(useCase.getPlayStateLiveData()) { it.position }

    fun connect() = useCase.connect()
    fun disconnect() = useCase.disconnect()
    fun play() = useCase.play()
    fun play(idx: Int) = useCase.play(idx)
    fun pause() = useCase.pause()
    fun skipToNext() = useCase.skipToNext()
    fun skipToPrevious() = useCase.skipToPrevious()
}
