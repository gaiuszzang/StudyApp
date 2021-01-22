package com.lge.kotlinstudyapp.usecase

import android.media.AudioAttributes
import android.media.MediaMetadata
import android.media.MediaPlayer
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.os.Bundle
import android.os.ResultReceiver
import androidx.annotation.IntDef
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lge.kotlinstudyapp.KotlinStudyApplication
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

    private val parentJob = Job()
    private val useCaseScope = CoroutineScope(parentJob + Dispatchers.Main)
    private val context = KotlinStudyApplication.instance.applicationContext

    private var musicList : List<Music>? = null
    private val musicListListener = mutableListOf<(List<Music>?) -> Unit>()
    private var mediaIdx = 0
    private var mediaPlayer: MediaPlayer? = null
    private var updateProgressJob : Job? = null
    @MediaPlayState var playState : Int = MEDIA_STATE_STOP
        private set(@MediaPlayState state) {
            field = state
            notifyPlayStateListener()
        }
    private val stateBuilder = PlaybackState.Builder()
        .setActions(PlaybackState.ACTION_PLAY or PlaybackState.ACTION_PAUSE
                or PlaybackState.ACTION_SKIP_TO_PREVIOUS or PlaybackState.ACTION_SKIP_TO_NEXT
                or PlaybackState.ACTION_STOP)

    lateinit var mediaSession: MediaSession
    private val sessionCallback = object: MediaSession.Callback() {
        override fun onPlay() {
            useCaseScope.launch {
                if (playState != MEDIA_STATE_PAUSE) {
                    logd(TAG, "onPlay : begin")
                    playMusic()
                } else {
                    logd(TAG, "onPlay : restart")
                    restartMusic()
                }
            }
        }
        override fun onPause() {
            logd(TAG, "onPause")
            useCaseScope.launch {
                pauseMusic()
            }
        }
        override fun onStop() {
            logd(TAG, "onStop")
            useCaseScope.launch {
                stopMusic()
            }
        }
        override fun onSkipToNext() {
            logd(TAG, "onSkipToNext")
            useCaseScope.launch {
                playNextMusic()
            }
        }
        override fun onSkipToPrevious() {
            logd(TAG, "onSkipToPrevious")
            useCaseScope.launch {
                playPrevMusic()
            }
        }
        override fun onCommand(command: String, args: Bundle?, cb: ResultReceiver?) {
            logd(TAG, "onCommand($command)")
            when(command) {
                "PlayIndex" -> {
                    val idx = args?.getInt("index", 0) ?: 0
                    useCaseScope.launch {
                        playMusic(idx)
                    }
                }
            }
        }
    }
    private val playStateListener = object: PlayStateListener {
        override fun update(state: Int) {
            when(state) {
                MEDIA_STATE_PLAY -> {
                    mediaSession.setPlaybackState(stateBuilder.setState(PlaybackState.STATE_PLAYING, 0, 1f).build())
                }
                MEDIA_STATE_PAUSE -> {
                    mediaSession.setPlaybackState(stateBuilder.setState(PlaybackState.STATE_PAUSED, 0, 1f).build())
                }
                MEDIA_STATE_PREPARE -> {
                    mediaSession.setPlaybackState(stateBuilder.setState(PlaybackState.STATE_BUFFERING, 0, 1f).build())
                }
                MEDIA_STATE_STOP -> {
                    mediaSession.setPlaybackState(stateBuilder.setState(PlaybackState.STATE_STOPPED, 0, 1f).build())
                }
            }
        }
    }
    private val playStateListenerList = mutableListOf<PlayStateListener>()

    interface PlayStateListener {
        fun update(@MediaPlayState state: Int)
    }

    fun updateMusicList() {
        useCaseScope.launch(Dispatchers.IO) {
            val list = repo.getMusicList()
            withContext(Dispatchers.Main) {
                musicList = list
                musicListListener.forEach { listener -> listener.invoke(musicList) }
                musicListListener.clear()
            }
        }
    }

    fun getMusicList() : List<Music>? = musicList

    fun addMusicListListener(listener: (List<Music>?) -> Unit) {
        musicListListener.add(listener)
    }

    suspend fun playMusic(idx: Int) {
        mediaIdx = idx
        musicList?.apply {
            if (mediaIdx > (size - 1)) mediaIdx = 0
            if (mediaIdx < 0) mediaIdx = size - 1
            playMusic()
        }
    }
    suspend fun playMusic() = withContext(Dispatchers.Main) {
        if (musicList == null || mediaIdx > (musicList!!.size - 1)) {
            logw(TAG, "musicList null or size < idx")
            stopMusic()
            return@withContext
        }
        logw(TAG, "let's play music $mediaIdx")
        musicList?.let {
            val music = it[mediaIdx]
            if (playState == MEDIA_STATE_PLAY || playState == MEDIA_STATE_PAUSE) {
                //release prev mediaPlayer
                mediaPlayer?.apply {
                    stop()
                    release()
                    updateProgressJob?.cancelAndJoin()
                    updateProgressJob = null
                }
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
                val mediaMetadata = MediaMetadata.Builder()
                    .putString(MediaMetadata.METADATA_KEY_MEDIA_ID, music.id)
                    .putString(MediaMetadata.METADATA_KEY_MEDIA_URI, music.url)
                    .putString(MediaMetadata.METADATA_KEY_ARTIST, music.artist)
                    .putString(MediaMetadata.METADATA_KEY_GENRE, music.genre)
                    .putString(MediaMetadata.METADATA_KEY_TITLE, music.title)
                    .putString(MediaMetadata.METADATA_KEY_DISPLAY_DESCRIPTION, music.artist)
                    .putLong(MediaMetadata.METADATA_KEY_DURATION, mediaPlayer?.duration?.toLong() ?: 0L)
                    .build()
                mediaSession.setMetadata(mediaMetadata)
                start()
                playState = MEDIA_STATE_PLAY
                logd(TAG, "startMusic now start")
                enableUpdateProgress()
            }
        }
    }

    suspend fun playNextMusic() {
        playMusic(mediaIdx + 1)
    }

    suspend fun playPrevMusic() {
        playMusic(mediaIdx - 1)
    }

    suspend fun stopMusic() = withContext(Dispatchers.Main) {
        if (playState != MEDIA_STATE_STOP) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
            disableUpdateProgress()
            playState = MEDIA_STATE_STOP
        } else {
            logw(TAG, "stopMusic : invalid current status $playState")
        }
    }

    suspend fun pauseMusic() = withContext(Dispatchers.Main) {
        mediaPlayer?.apply {
            if (playState == MEDIA_STATE_PLAY) {
                pause()
                disableUpdateProgress()
                playState = MEDIA_STATE_PAUSE
            } else {
                logw(TAG, "pauseMusic : invalid current status $playState")
            }
        }
    }

    suspend fun restartMusic() = withContext(Dispatchers.Main) {
        mediaPlayer?.apply {
            if (playState == MEDIA_STATE_PAUSE) {
                start()
                playState = MEDIA_STATE_PLAY
                enableUpdateProgress()
            } else {
                logw(TAG, "restartMusic : invalid current status $playState")
            }
        }
    }

    fun isPlaying(): Boolean = (playState == MEDIA_STATE_PLAY || playState == MEDIA_STATE_PREPARE)

    fun getCurrentMusic(): Music? {
        return musicList?.get(mediaIdx)
    }

    fun registerPlayStateListener(listener : PlayStateListener) {
        playStateListenerList.add(listener)
        listener.update(playState)
    }

    fun unregisterPlayStateListener(listener : PlayStateListener) {
        playStateListenerList.remove(listener)
    }

    private fun notifyPlayStateListener() {
        playStateListenerList.forEach { it.update(playState) }
    }

    fun onInit() {
        playStateListenerList.add(playStateListener)
        mediaSession = MediaSession(context, TAG).apply {
            setPlaybackState(stateBuilder.setState(PlaybackState.STATE_STOPPED, 0, 1f).build())
            setCallback(sessionCallback)
        }
    }
    fun onDestroy() {
        parentJob.cancel()
        playStateListenerList.clear()
    }

    private suspend fun enableUpdateProgress() = withContext(Dispatchers.Main) {
        updateProgressJob = launch(Dispatchers.Main) {
            delay(500)
            while (mediaPlayer?.isPlaying == true) {
                val playMs = (mediaPlayer?.currentPosition ?: 0).toLong()
                logd(TAG, "Music Progress = $playMs / ${mediaPlayer?.duration}")
                mediaSession.setPlaybackState(stateBuilder.setState(PlaybackState.STATE_PLAYING, playMs, 1f).build())
                delay(500)
            }
        }
    }

    private suspend fun disableUpdateProgress() = withContext(Dispatchers.Main) {
        updateProgressJob?.cancelAndJoin()
        updateProgressJob = null
    }
}