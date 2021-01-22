package com.lge.kotlinstudyapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.IntDef
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lge.kotlinstudyapp.R
import com.lge.kotlinstudyapp.databinding.AdvertiseListViewBind
import com.lge.kotlinstudyapp.databinding.ProductListViewBind
import com.lge.kotlinstudyapp.db.AdvertiseDto
import com.lge.kotlinstudyapp.db.ProductDto

class PLPMainAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val VIEW_TYPE_ADVERTISE_LIST = 0
        const val VIEW_TYPE_PRODUCT_LIST   = 1
        @IntDef(VIEW_TYPE_ADVERTISE_LIST, VIEW_TYPE_PRODUCT_LIST)
        annotation class PLPViewType
    }

    data class PLPItem(@PLPViewType val viewType: Int,
                       val advertiseList : List<AdvertiseDto>? = null,
                       val productList : List<ProductDto>? = null)

    private val plpList = arrayListOf<PLPItem>()

    inner class AdvertiseListViewHolder(private val bind: AdvertiseListViewBind) : RecyclerView.ViewHolder(bind.root) {
        fun setAdvertiseList(list : List<AdvertiseDto>) {
            (bind.recyclerView.adapter as AdvertiseListAdapter).setAdvertiseList(list)
        }
    }

    inner class ProductListViewHolder(private val bind: ProductListViewBind) : RecyclerView.ViewHolder(bind.root) {
        fun setProductList(list : List<ProductDto>) {
            (bind.recyclerView.adapter as ProductListAdapter).setProductList(list)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ADVERTISE_LIST) {
            val bind: AdvertiseListViewBind = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.view_advertiselist, parent, false)
            bind.recyclerView.adapter = AdvertiseListAdapter()
            AdvertiseListViewHolder(bind)
        } else {// if (viewType == VIEW_TYPE_PRODUCT_LIST) {
            val bind: ProductListViewBind = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.view_productlist, parent, false)
            bind.recyclerView.adapter = ProductListAdapter()
            ProductListViewHolder(bind)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == VIEW_TYPE_ADVERTISE_LIST) {
            (holder as AdvertiseListViewHolder).setAdvertiseList(plpList[position].advertiseList!!)
        } else { //if (holder.itemViewType == VIEW_TYPE_PRODUCT_LIST) {
            (holder as ProductListViewHolder).setProductList(plpList[position].productList!!)
        }
    }

    override fun getItemCount(): Int = plpList.size
    override fun getItemViewType(position: Int): Int = plpList[position].viewType

    fun setPLPList(list : List<PLPItem>) {
        plpList.clear()
        plpList.addAll(list)
        notifyDataSetChanged()
    }
}