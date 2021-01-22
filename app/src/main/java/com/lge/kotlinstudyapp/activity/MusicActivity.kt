package com.lge.kotlinstudyapp.activity

import android.content.ComponentName
import android.media.AudioManager
import android.media.browse.MediaBrowser
import android.media.session.MediaController
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaControllerCompat.setMediaController
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.lge.kotlinstudyapp.R
import com.lge.kotlinstudyapp.adapter.MusicListAdapter
import com.lge.kotlinstudyapp.databinding.MusicBind
import com.lge.kotlinstudyapp.db.Music
import com.lge.kotlinstudyapp.logd
import com.lge.kotlinstudyapp.logi
import com.lge.kotlinstudyapp.service.KTBwMusicService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MusicActivity : AppCompatActivity() {
    companion object {
        private const val TAG: String = "MusicActivity"
    }
    private val vm : MusicViewModel by viewModels()
    private val bind : MusicBind by lazy { DataBindingUtil.setContentView(this, R.layout.activity_music) }
    private val musicListAdapter = MusicListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logi(TAG, "onCreate()")
        bind.activity = this
        bind.vm = vm
        bind.lifecycleOwner = this
        bind.musicRecyclerView.adapter = musicListAdapter
        vm.musicList.observe(this, { musicListAdapter.setMusicList(it) })
        vm.playMusic.observe(this, {
            bind.seekBar.min = 0
            bind.seekBar.max = it.duration
            logd(TAG, "PlayMusic Changed, duration = 0 ~ ${it.duration}")
        })
        vm.playPosValue.observe(this, {
            bind.seekBar.progress = it.toInt()
        })
        musicListAdapter.setMusicItemClickListener { idx, music -> vm.play(idx) }
    }

    override fun onStart() {
        super.onStart()
        vm.connect()
    }

    override fun onResume() {
        super.onResume()
        volumeControlStream = AudioManager.STREAM_MUSIC
    }

    override fun onStop() {
        super.onStop()
        vm.disconnect()
    }
}
