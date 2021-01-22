package com.lge.kotlinstudyapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lge.kotlinstudyapp.R
import com.lge.kotlinstudyapp.databinding.MusicViewBind
import com.lge.kotlinstudyapp.db.Music

class MusicListAdapter : RecyclerView.Adapter<MusicListAdapter.MusicViewHolder>() {
    inner class MusicViewHolder(private val bind: MusicViewBind) : RecyclerView.ViewHolder(bind.root) {
        fun updateView(idx: Int, item: Music) {
            bind.txtTitle.text = item.title
            bind.txtArtist.text = item.artist
            bind.lyMusicItem.setOnClickListener {
                musicItemClickListener?.invoke(idx, item)
            }
        }
    }

    private val musicList = arrayListOf<Music>()
    private var musicItemClickListener : ((Int, Music) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        return MusicViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.view_music, parent, false))
    }
    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        holder.updateView(position, musicList[position])
    }
    override fun getItemCount(): Int = musicList.size

    fun setMusicList(list: List<Music>) {
        musicList.clear()
        musicList.addAll(list)
        notifyDataSetChanged()
    }

    fun setMusicItemClickListener(listener : ((Int, Music) -> Unit)?) {
        musicItemClickListener = listener
    }
}