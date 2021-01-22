package com.lge.kotlinstudyapp.usecase

import android.content.ComponentName
import android.media.MediaMetadata
import android.media.browse.MediaBrowser
import android.media.session.MediaController
import android.media.session.PlaybackState
import android.os.Bundle
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
    private val musicList = MutableLiveData<List<Music>>()
    private val playMusic = MutableLiveData<Music>()
    private val playState = MutableLiveData<PlaybackState>()

    private val context = KotlinStudyApplication.instance.applicationContext
    private lateinit var mediaBrowser: MediaBrowser
    private var mediaController: MediaController? = null

    private val controllerCallback = object : MediaController.Callback() {
        override fun onMetadataChanged(metadata: MediaMetadata?) {
            //super.onMetadataChanged(metadata) todo remove
            logd(TAG, "onMetadataChanged(${metadata?.description?.title ?: "null"})")
            metadata?.let {
                playMusic.postValue(mapMediaMetadataToMusic(it))
            }

        }
        override fun onPlaybackStateChanged(state: PlaybackState?) {
            super.onPlaybackStateChanged(state)
            logd(TAG, "onPlaybackStateChanged($state)")
            state?.let { playState.postValue(it) }
        }
    }

    private val connectionCallback = object : MediaBrowser.ConnectionCallback() {
        override fun onConnected() {
            logd(TAG, "MediaBrowser onConnected")
            // Get the token for the MediaSession
            mediaBrowser.sessionToken.also { token ->
                // Create a MediaControllerCompat
                logd(TAG, "MediaBrowser token = $token")
                mediaController = MediaController(context, token)
                mediaController?.registerCallback(controllerCallback)
            }
        }

        override fun onConnectionSuspended() {
            logd(TAG, "MediaBrowser suspended : maybe crashed")
        }

        override fun onConnectionFailed() {
            logd(TAG, "MediaBrowser failed")
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
        mediaBrowser = MediaBrowser(context, ComponentName(context, KTBwMusicService::class.java), connectionCallback, null)
        mediaBrowser.subscribe(KTBwMusicService.BROWSER_ROOT_ID, subscriptionCallback)
    }

    fun connect() {
        mediaBrowser.connect()
    }

    fun disconnect() {
        mediaController?.unregisterCallback(controllerCallback)
        mediaBrowser.disconnect()
    }

    fun getMusicListLiveData() : LiveData<List<Music>> = musicList
    fun getPlayMusicLiveData() : LiveData<Music> = playMusic
    fun getPlayStateLiveData() : LiveData<PlaybackState> = playState

    fun play() {
        mediaController?.transportControls?.play()
    }

    fun play(index: Int) {
        val bundle = Bundle()
        bundle.putInt("index", index)
        mediaController?.sendCommand("PlayIndex", bundle, null)
    }

    fun pause() {
        mediaController?.transportControls?.pause()
    }

    fun skipToNext() {
        mediaController?.transportControls?.skipToNext()
    }

    fun skipToPrevious() {
        mediaController?.transportControls?.skipToPrevious()
    }

    private fun mapMediaMetadataToMusic(metadata: MediaMetadata) : Music {
        val id = metadata.getString(MediaMetadata.METADATA_KEY_MEDIA_ID)
        val url = metadata.getString(MediaMetadata.METADATA_KEY_MEDIA_URI)
        val artist = metadata.getString(MediaMetadata.METADATA_KEY_ARTIST)
        val genre = metadata.getString(MediaMetadata.METADATA_KEY_GENRE)
        val title = metadata.getString(MediaMetadata.METADATA_KEY_TITLE)
        val duration = metadata.getLong(MediaMetadata.METADATA_KEY_DURATION).toInt()
        return Music(id, url, title, artist, genre, duration)
    }
}