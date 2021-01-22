package com.lge.kotlinstudyapp.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.session.MediaSession
import android.os.Binder
import android.os.IBinder
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import androidx.media.session.MediaButtonReceiver
import com.lge.kotlinstudyapp.R
import com.lge.kotlinstudyapp.db.Music
import com.lge.kotlinstudyapp.logd
import com.lge.kotlinstudyapp.usecase.MusicServiceUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class KTMusicService : LifecycleService(), MediaPlayer.OnErrorListener {
    companion object {
        private const val TAG = "KTMusicService"
        private const val CHANNEL_ID = "KTMUSIC_CHANNEL"
    }

    inner class LocalBinder : Binder() {
        fun getService() : KTMusicService = this@KTMusicService
    }
    private val binder = LocalBinder()
    private var mediaPlayer: MediaPlayer? = null
    private var updateProgressJob : Job? = null
    private var noti : Notification? = null
    @Inject lateinit var useCase : MusicServiceUseCase
    private val mediaSession : MediaSession by lazy { MediaSession(this, TAG) }

    private val msCallback = object: MediaSession.Callback() {
        override fun onPlay() {
            super.onPlay()
            logd(TAG, "onPlay")
            restartMusic()
        }
        override fun onPause() {
            super.onPause()
            logd(TAG, "onPause")
            pauseMusic()
        }
        override fun onRewind() {
            super.onRewind()
            logd(TAG, "onRewind")
        }
    }

    override fun onCreate() {
        logd(TAG, "onCreate")
        super.onCreate()
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        logd(TAG, "onStartCommand")
        super.onStartCommand(intent, flags, startId)
        val channel = NotificationChannel(CHANNEL_ID, TAG, NotificationManager.IMPORTANCE_DEFAULT)
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)

        noti = Notification.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .addAction(Notification.Action(android.R.drawable.ic_media_previous, "previous", MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_REWIND)))
            .addAction(Notification.Action(android.R.drawable.ic_media_pause, "pause", MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_PAUSE)))
            .addAction(Notification.Action(android.R.drawable.ic_media_next, "next", MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_FAST_FORWARD)))
            .setStyle(Notification.MediaStyle()
                .setMediaSession(mediaSession.sessionToken)
                .setShowActionsInCompactView(0,1,2))
            .setContentTitle("Wonderful music")
            .setContentText("My Awesome Band")
            .build()
        startForeground(1, noti)
/*
 */
        mediaSession.setCallback(msCallback)
        return START_NOT_STICKY
    }
    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        logd(TAG, "onBind")
        return binder
    }
    override fun onDestroy() {
        logd(TAG, "onDestroy")
        stopMusic()
        super.onDestroy()
    }

    //API
    fun startMusic(i: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            val music = useCase.getMusicList()?.get(i)
            music?.let {
                logd(TAG, "music is not null, play")
                startMusic(it)
            }
        }
    }

    fun startMusic(music: Music) {
        logd(TAG, "startMusic")
        lifecycleScope.launch(Dispatchers.IO) {
            logd(TAG, "startMusic IO")
            mediaPlayer?.apply {
                logd(TAG, "stop before MediaPlayer")
                stopMusic()
            }
            logd(TAG, "create new MediaPlayer")
            mediaPlayer = MediaPlayer()
            mediaPlayer?.apply {
                //mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
                //mediaPlayer.setDataSource(this@KTMusicService, Uri.parse(music.url))
                setAudioAttributes(AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())
                setOnErrorListener(this@KTMusicService)
                setDataSource(music.url)
                logd(TAG, "startMusic now prepare")
                prepare()
                logd(TAG, "startMusic now start")
                start()
                logd(TAG, "start UpdateProgressJob")
                updateProgressJob = lifecycleScope.launch(Dispatchers.Main) {
                    delay(500)
                    while (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                        val progress = mediaPlayer?.currentPosition
                        logd(TAG, "Music Progress = $progress / ${mediaPlayer?.duration}")
                        delay(500)
                    }
                }
            }
        }
    }
    fun restartMusic() {
        mediaPlayer?.start()
    }
    fun pauseMusic() {
        mediaPlayer?.pause()
    }

    fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        lifecycleScope.launch {
            updateProgressJob?.cancelAndJoin()
            updateProgressJob = null
        }
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        logd(TAG, "error : $what, $extra")
        return true
    }
}
