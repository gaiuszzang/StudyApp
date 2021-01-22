package com.lge.kotlinstudyapp.usecase

import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.annotation.IntDef
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lge.kotlinstudyapp.db.Music
import com.lge.kotlinstudyapp.logd
import com.lge.kotlinstudyapp.loge
import com.lge.kotlinstudyapp.logw
import com.lge.kotlinstudyapp.repo.Repo
import com.lge.kotlinstudyapp.service.KTBwMusicService
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton

class MusicServiceUseCase @Inject constructor(private val repo: Repo) {
    companion object {
        private const val TAG = "MusicServiceUseCase"
        const val MEDIA_STATE_STOP     = 0
        const val MEDIA_STATE_PREPARE  = 1
        const val MEDIA_STATE_PLAY     = 2
        const val MEDIA_STATE_PAUSE    = 3
        @IntDef(MEDIA_STATE_STOP, MEDIA_STATE_PREPARE, MEDIA_STATE_PLAY, MEDIA_STATE_PAUSE)
        annotation class MediaPlayState
    }

    /* for Service */
    private var musicList : List<Music>? = null
    private val musicListListener = mutableListOf<(List<Music>?) -> Unit>()

    private var mediaIdx = 0
    private var mediaPlayer: MediaPlayer? = null
    private var updateProgressJob : Job? = null
    @MediaPlayState var playState : Int = MEDIA_STATE_STOP
        private set

    suspend fun updateMusicList() = withContext(Dispatchers.IO) {
        val list = repo.getMusicList()
        withContext(Dispatchers.Main) {
            musicList = list
            musicListListener.forEach { listener -> listener.invoke(musicList) }
            musicListListener.clear()
        }
    }

    fun getMusicList() : List<Music>? = musicList

    fun addMusicListListener(listener: (List<Music>?) -> Unit) {
        musicListListener.add(listener)
    }

    suspend fun playNextMusic() {
        stopMusic()
        playMusic(mediaIdx + 1)
    }
    suspend fun playMusic(idx: Int) {
        mediaIdx = idx
        playMusic()
    }
    suspend fun playMusic() = withContext(Dispatchers.Main) {
        /*
        TODO
        01-22 17:37:11.069 31478 31478 W KotlinStudyApp: [MusicServiceUseCase] let's play music 4
        01-22 17:37:11.102 31478 31478 E AndroidRuntime: FATAL EXCEPTION: main
        01-22 17:37:11.102 31478 31478 E AndroidRuntime: Process: com.lge.kotlinstudyapp, PID: 31478
        01-22 17:37:11.102 31478 31478 E AndroidRuntime: java.lang.IndexOutOfBoundsException: Index: 4, Size: 4
        01-22 17:37:11.102 31478 31478 E AndroidRuntime: 	at java.util.ArrayList.get(ArrayList.java:437)
        01-22 17:37:11.102 31478 31478 E AndroidRuntime: 	at com.lge.kotlinstudyapp.usecase.MusicServiceUseCase$playMusic$3.invokeSuspend(MusicServiceUseCase.kt:69)

         */
        if (musicList == null || musicList!!.size < mediaIdx - 1) {
            logw(TAG, "musicList null or size < idx")
            return@withContext
        }
        logw(TAG, "let's play music $mediaIdx")
        musicList?.let {
            val music = it[mediaIdx]
            if (playState == MEDIA_STATE_PLAY) {
                stopMusic()
            }
            mediaPlayer = MediaPlayer()
            mediaPlayer?.apply {
                setAudioAttributes(AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())
                setOnCompletionListener {
                    launch {
                        logd(TAG, "mediaPlayer onCompleted, so try to next song")
                        playNextMusic()
                    }
                }
                playState = MEDIA_STATE_PREPARE
                //setOnErrorListener(this@KTMusicService)
                if (!withContext(Dispatchers.IO) {
                        try {
                            setDataSource(music.url)
                            prepare()
                            logd(TAG, "startMusic now prepared")
                            true
                        } catch (e: Exception) {
                            loge(TAG, "error : $e")
                            stopMusic()
                            false
                        }
                    }) return@let
                start()
                playState = MEDIA_STATE_PLAY
                logd(TAG, "startMusic now start")
                logd(TAG, "start UpdateProgressJob")
                updateProgressJob = launch(Dispatchers.Main) {
                    delay(500)
                    while (mediaPlayer?.isPlaying == true) {
                        val progress = mediaPlayer?.currentPosition
                        logd(TAG, "Music Progress = $progress / ${mediaPlayer?.duration}")
                        delay(500)
                    }
                }
            }
        }
    }

    suspend fun stopMusic() = withContext(Dispatchers.Main) {
        if (playState != MEDIA_STATE_STOP) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
            playState = MEDIA_STATE_STOP
            updateProgressJob?.cancelAndJoin()
            updateProgressJob = null
        } else {
            logw(TAG, "stopMusic : invalid status requested")
        }
    }

    suspend fun pauseMusic() = withContext(Dispatchers.Main) {
        mediaPlayer?.let {
            if (playState == MEDIA_STATE_PLAY) {
                it.pause()
                playState = MEDIA_STATE_PAUSE
            } else {
                logw(TAG, "pauseMusic : invalid status requested")
            }
        }
    }

    suspend fun restartMusic() = withContext(Dispatchers.Main) {
        mediaPlayer?.let {
            if (playState == MEDIA_STATE_PAUSE) {
                it.start()
                playState = MEDIA_STATE_PLAY
            } else {
                logw(TAG, "restartMusic : invalid status requested")
            }
        }
    }
}