package com.lge.kotlinstudyapp.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.lge.kotlinstudyapp.db.Music

class KTMusicServiceProxy {
    private lateinit var ktMusicService : KTMusicService
    private var isBind = false

    private val ktMusicServiceConnection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as KTMusicService.LocalBinder
            ktMusicService = binder.getService()
            isBind = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBind = false
        }
    }

    fun startService(context: Context) {
        val intent = Intent(context, KTMusicService::class.java)
        context.startService(intent)
    }

    fun stopService(context: Context) {
        val intent = Intent(context, KTMusicService::class.java)
        context.stopService(intent)
    }

    fun bindService(context: Context) {
        if (!isBind) {
            val intent = Intent(context, KTMusicService::class.java)
            context.bindService(intent, ktMusicServiceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    fun unbindService(context: Context) {
        if (isBind) {
            context.unbindService(ktMusicServiceConnection)
        }
    }
    fun startMusic(idx: Int) {
        if (isBind) ktMusicService.startMusic(idx)
    }
    fun startMusic(music: Music) {
        if (isBind) ktMusicService.startMusic(music)
    }

    fun stopMusic() {
        if (isBind) ktMusicService.pauseMusic()
    }
}