package com.lge.kotlinstudyapp.service

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.MediaDescription
import android.media.MediaMetadata
import android.media.browse.MediaBrowser
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
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
    private var isForegrounded = false
    private val notiManager : NotificationManager by lazy { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }

    private fun updateNotification() {
        val currentMusic = useCase.getCurrentMusic()
        if (currentMusic == null || sessionToken == null) return
        val noti = KTBwMusicNotificationBuilder.getNotiBuilder(this, currentMusic.title, currentMusic.artist, sessionToken!!, useCase.isPlaying()).build()
        if (isForegrounded) {
            notiManager.notify(NOTI_ID, noti)
        } else {
            startForegroundService(Intent(this, this.javaClass))
            startForeground(NOTI_ID, noti)
            isForegrounded = true
        }
    }

    private fun stopNotification() {
        if (isForegrounded) {
            stopForeground(true)
            isForegrounded = false
        }
    }

    //MediaPlayer Status Callback
    private val playStateListener = object: MusicServiceUseCase.PlayStateListener {
        override fun update(state: Int) {
            logd(TAG, "playState Updated : $state")
            when(state) {
                MusicServiceUseCase.MEDIA_STATE_PLAY -> updateNotification()
                MusicServiceUseCase.MEDIA_STATE_PAUSE -> updateNotification()
                MusicServiceUseCase.MEDIA_STATE_PREPARE -> updateNotification()
                MusicServiceUseCase.MEDIA_STATE_STOP -> stopNotification()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        //init UseCase
        useCase.onInit()
        //set SessionToken
        sessionToken = useCase.mediaSession.sessionToken
        //register
        useCase.registerPlayStateListener(playStateListener)
        //Request Music List
        useCase.updateMusicList()
        //Create Notification Channel
        KTBwMusicNotificationBuilder.createChannel(notiManager)
    }

    override fun onDestroy() {
        super.onDestroy()
        useCase.onDestroy()
        useCase.unregisterPlayStateListener(playStateListener)
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
            useCase.addMusicListListener {
                if (it != null) {
                    logd(TAG, "list received.")
                    val mediaItemList = mapMusicToMediaItem(it)
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
        list.forEach { music -> mediaItemList.add(mapMusicToMediaItem(music)) }
        return mediaItemList
    }

    private fun mapMusicToMediaItem(music: Music) : MediaBrowser.MediaItem {
        val desc = MediaDescription.Builder()
            .setMediaId(music.id)
            .setMediaUri(Uri.fromFile(File(music.url)))
            .setTitle(music.title)
            .setDescription(music.artist)
            .build()
        return MediaBrowser.MediaItem(desc, MediaBrowser.MediaItem.FLAG_PLAYABLE or MediaBrowser.MediaItem.FLAG_BROWSABLE)
    }
}
