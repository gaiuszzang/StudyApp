package com.lge.kotlinstudyapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lge.kotlinstudyapp.R
import com.lge.kotlinstudyapp.databinding.ItemLoadStateViewBind
import com.lge.kotlinstudyapp.databinding.ItemViewBind
import com.lge.kotlinstudyapp.logd
import com.lge.kotlinstudyapp.server.data.ItemDto

class ILPMainAdapter(diffCallback : ItemDiffCallback) : PagingDataAdapter<ItemDto, ILPMainAdapter.ItemViewHolder>(diffCallback) {
    inner class ItemViewHolder(private val bind: ItemViewBind) : RecyclerView.ViewHolder(bind.root) {
        fun setItem(item: ItemDto?) {
            item?.let {
                bind.itemName.text = it.itemName
                bind.itemDesc.text = it.itemDesc
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val bind: ItemViewBind = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.view_item, parent, false)
        return ItemViewHolder(bind)
    }
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.setItem(item)
    }


    class ItemLoadStateViewHolder(private val bind: ItemLoadStateViewBind) : RecyclerView.ViewHolder(bind.root) {
        fun setLoadState(state: LoadState) {
            if (state is LoadState.Error) {
                bind.txtError.text = state.error.localizedMessage
            }
            bind.progressLoad.visibility = if (state is LoadState.Loading) View.VISIBLE else View.GONE
            bind.txtError.visibility = if (state is LoadState.Error) View.VISIBLE else View.GONE
        }
    }
    class ItemLoadStateAdapter : LoadStateAdapter<ItemLoadStateViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ItemLoadStateViewHolder {
            return ItemLoadStateViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.view_itemloadstate, parent, false))
        }
        override fun onBindViewHolder(holder: ItemLoadStateViewHolder, loadState: LoadState) {
            holder.setLoadState(loadState)
        }
    }
}

class ItemDiffCallback : DiffUtil.ItemCallback<ItemDto>() {
    override fun areItemsTheSame(oldItem: ItemDto, newItem: ItemDto): Boolean {
        return (oldItem.itemId == oldItem.itemId)
    }
    override fun areContentsTheSame(oldItem: ItemDto, newItem: ItemDto): Boolean {
        return (oldItem.itemId == oldItem.itemId)
    }
}