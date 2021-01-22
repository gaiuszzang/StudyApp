package com.lge.kotlinstudyapp.usecase

import android.content.ComponentName
import android.media.browse.MediaBrowser
import android.media.session.MediaController
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lge.kotlinstudyapp.KotlinStudyApplication
import com.lge.kotlinstudyapp.db.Music
import com.lge.kotlinstudyapp.logd
import com.lge.kotlinstudyapp.service.KTBwMusicService
import javax.inject.Inject

class MusicActivityUseCase @Inject constructor() {
    companion object {
        private const val TAG = "MusicActivityUseCase"
    }
    val musicList = MutableLiveData<List<Music>>()

    private val context = KotlinStudyApplication.instance.applicationContext
    private lateinit var mediaBrowser: MediaBrowser
    private lateinit var mediaController: MediaController
    private val connectionCallbacks = object : MediaBrowser.ConnectionCallback() {
        override fun onConnected() {
            logd(TAG, "MediaBrowser onConnected")
            // Get the token for the MediaSession
            mediaBrowser.sessionToken.also { token ->
                // Create a MediaControllerCompat
                logd(TAG, "MediaBrowser token = $token")
                mediaController = MediaController(context, token)
                // Save the controller
                //MediaControllerCompat.setMediaController(this@MusicActivity, mediaController)
            }
            // Finish building the UI
            //buildTransportControls()
        }

        override fun onConnectionSuspended() {
            logd(TAG, "MediaBrowser suspended : maybe crashed")
            // The Service has crashed. Disable transport controls until it automatically reconnects
        }

        override fun onConnectionFailed() {
            logd(TAG, "MediaBrowser failed") // The Service has refused our connection
        }
    }

    private val subscriptionCallback = object : MediaBrowser.SubscriptionCallback() {
        override fun onChildrenLoaded(parentId: String, children: MutableList<MediaBrowser.MediaItem>) {
            super.onChildrenLoaded(parentId, children)
            logd(TAG, "mediaBrowser list subscription : size = ${children.size}")
            val list = children.map { mediaItem ->
                Music(mediaItem.mediaId?: "unknown id", mediaItem.description.mediaUri.toString(), mediaItem.description.title.toString(), mediaItem.description.description.toString(), "testGenre")
            }
            musicList.postValue(list)
        }
    }

    init {
        mediaBrowser = MediaBrowser(context, ComponentName(context, KTBwMusicService::class.java), connectionCallbacks, null)
        mediaBrowser.subscribe(KTBwMusicService.BROWSER_ROOT_ID, subscriptionCallback)
    }

    fun connect() {
        mediaBrowser.connect()
    }

    fun disconnect() {
        mediaBrowser.disconnect()
    }

    fun getMusicListLiveData() : LiveData<List<Music>> {
        return musicList
    }

    fun play() {
        mediaController.transportControls.play()
    }

    fun play(idx: Int) {

    }
}