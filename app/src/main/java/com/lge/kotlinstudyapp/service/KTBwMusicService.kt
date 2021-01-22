package com.lge.kotlinstudyapp.service

import android.content.Intent
import android.media.MediaDescription
import android.media.browse.MediaBrowser
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.net.Uri
import android.os.Bundle
import android.service.media.MediaBrowserService
import com.lge.kotlinstudyapp.db.Music
import com.lge.kotlinstudyapp.logd
import com.lge.kotlinstudyapp.logw
import com.lge.kotlinstudyapp.usecase.MusicServiceUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class KTBwMusicService : MediaBrowserService() {
    companion object {
        const val BROWSER_ROOT_ID = "KSAMusicBrowserRoot"
        private const val TAG = "KSAMusicService"
        private const val NOTI_ID = 1
    }

    @Inject lateinit var useCase : MusicServiceUseCase
    private lateinit var mediaSession: MediaSession
    private lateinit var stateBuilder: PlaybackState.Builder

    private val parentJob = Job()
    private val serviceScope = CoroutineScope(parentJob + Dispatchers.Main)

    private var isForegrounded = false

    private fun startForeground() {
        startForegroundService(Intent(this@KTBwMusicService, this@KTBwMusicService.javaClass))
        startForeground(NOTI_ID, KTBwMusicNotificationBuilder.getNotiBuilder(this@KTBwMusicService, mediaSession).build())
        isForegrounded = true
    }

    private fun stopForeground() {
        stopForeground(true)
        isForegrounded = false
    }

    private val sessionCallback = object: MediaSession.Callback() {
        override fun onPlay() {
            logd(TAG, "onPlay")
            super.onPlay()
            // Display the notification and place the service in the foreground
            if (!isForegrounded) startForeground()
            startMusic()
        }

        override fun onPause() {
            super.onPause()
            logd(TAG, "onPause")
            pauseMusic()
        }

        override fun onStop() {
            super.onStop()
            logd(TAG, "onStop")
            stopMusic()
            stopForeground()
        }

        override fun onSkipToNext() {
            super.onSkipToNext()
            logd(TAG, "onSkipToNext")
            playNextMusic()
        }

        override fun onSkipToPrevious() {
            super.onSkipToPrevious()
            logd(TAG, "onSkipToPrevious")
        }
    }

    override fun onCreate() {
        super.onCreate()
        mediaSession = MediaSession(baseContext, TAG).apply {
            stateBuilder = PlaybackState.Builder()
                .setActions(PlaybackState.ACTION_PLAY or PlaybackState.ACTION_PAUSE
                or PlaybackState.ACTION_SKIP_TO_PREVIOUS or PlaybackState.ACTION_SKIP_TO_NEXT
                or PlaybackState.ACTION_STOP)
            setPlaybackState(stateBuilder.build())
            setCallback(sessionCallback)
            setSessionToken(sessionToken)
        }

        serviceScope.launch {
            useCase.updateMusicList()
        }
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot {
        logd(TAG, "onGetRoot : clientPackageName = $clientPackageName, clientUid = $clientUid")
        return BrowserRoot(BROWSER_ROOT_ID, null)
    }

    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowser.MediaItem>>) {
        logd(TAG, "onLoadChildren : parentId = $parentId")

        val list = useCase.getMusicList()
        if (list != null) {
            logd(TAG, "list exist, sendResult")
            val mediaItemList = mapMusicToMediaItem(list)
            result.sendResult(mediaItemList)
        } else {
            logw(TAG, "list null, add listener and detach")
            useCase.addMusicListListener { list ->
                if (list != null) {
                    logd(TAG, "list received.")
                    val mediaItemList = mapMusicToMediaItem(list)
                    result.sendResult(mediaItemList)
                } else {
                    logd(TAG, "list received, but empty..")
                }
            }
            result.detach()
        }
    }

    private fun mapMusicToMediaItem(list : List<Music>) : MutableList<MediaBrowser.MediaItem> {
        val mediaItemList = arrayListOf<MediaBrowser.MediaItem>()
        list.forEach { music ->
            val desc = MediaDescription.Builder()
                .setMediaId(music.id)
                .setMediaUri(Uri.fromFile(File(music.url)))
                .setTitle(music.title)
                .setDescription(music.artist)
                .build()
            mediaItemList.add(
                MediaBrowser.MediaItem(
                    desc,
                    MediaBrowser.MediaItem.FLAG_PLAYABLE or MediaBrowser.MediaItem.FLAG_BROWSABLE
                )
            )
        }
        return mediaItemList
    }

    private fun startMusic() {
        serviceScope.launch {
            if (useCase.playState != MusicServiceUseCase.MEDIA_STATE_PAUSE) {
                logd(TAG, "startMusic : play")
                useCase.playMusic()
            } else {
                logd(TAG, "startMusic : restart")
                useCase.restartMusic()
            }
        }
    }
    private fun stopMusic() {
        logd(TAG, "stopMusic")
        serviceScope.launch {
            useCase.stopMusic()
        }
    }
    private fun pauseMusic() {
        logd(TAG, "pauseMusic")
        serviceScope.launch {
            useCase.pauseMusic()
        }
    }
    private fun playNextMusic() {
        logd(TAG, "playNextMusic")
        serviceScope.launch {
            useCase.playNextMusic()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        parentJob.cancel()
    }

}
