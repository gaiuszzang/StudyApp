package com.lge.kotlinstudyapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lge.kotlinstudyapp.R
import com.lge.kotlinstudyapp.databinding.AdvertiseViewBind
import com.lge.kotlinstudyapp.db.AdvertiseDto

class AdvertiseListAdapter : RecyclerView.Adapter<AdvertiseListAdapter.AdvertiseListViewHolder>() {
    private val advertiseList = arrayListOf<AdvertiseDto>()
    inner class AdvertiseListViewHolder(private val bind: AdvertiseViewBind) : RecyclerView.ViewHolder(bind.root) {
        fun setView(item: AdvertiseDto) {
            bind.advertiseName.text = item.advertiseTitle
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdvertiseListViewHolder {
        return AdvertiseListViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.view_advertise, parent, false))
    }
    override fun onBindViewHolder(holder: AdvertiseListViewHolder, position: Int) {
        holder.setView(advertiseList[position])
    }
    override fun getItemCount(): Int = advertiseList.size

    fun setAdvertiseList(list : List<AdvertiseDto>) {
        val diffCallback = AdvertiseDiffCallback(advertiseList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        advertiseList.clear()
        advertiseList.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }

    class AdvertiseDiffCallback(private val oldList: List<AdvertiseDto>, val newList: List<AdvertiseDto>) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].advertiseId == newList[newItemPosition].advertiseId
        }
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].advertiseId == newList[newItemPosition].advertiseId
        }

    }
}