package com.lge.kotlinstudyapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.IntDef
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lge.kotlinstudyapp.R
import com.lge.kotlinstudyapp.databinding.AdvertiseListViewBind
import com.lge.kotlinstudyapp.databinding.ItemViewBind
import com.lge.kotlinstudyapp.databinding.ProductListViewBind
import com.lge.kotlinstudyapp.db.AdvertiseDto
import com.lge.kotlinstudyapp.server.data.ProductDto

class PLPMainAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val VIEW_TYPE_ADVERTISE_LIST = 0
        const val VIEW_TYPE_PRODUCT_LIST   = 1
        const val VIEW_TYPE_ITEM           = 2
        @IntDef(VIEW_TYPE_ADVERTISE_LIST, VIEW_TYPE_PRODUCT_LIST, VIEW_TYPE_ITEM)
        annotation class PLPViewType
    }

    data class PLPItem(
        @PLPViewType val viewType: Int,
        val advertiseList : List<AdvertiseDto>? = null,
        val productListTitle : String? = null,
        val productList : List<ProductDto>? = null,
        val desc: String? = null
    )

    private val plpList = arrayListOf<PLPItem>()

    inner class AdvertiseListViewHolder(private val bind: AdvertiseListViewBind) : RecyclerView.ViewHolder(bind.root) {
        fun setAdvertiseList(list : List<AdvertiseDto>) {
            (bind.viewPager.adapter as AdvertiseListAdapter).setAdvertiseList(list)
        }
    }

    inner class ProductListViewHolder(private val bind: ProductListViewBind) : RecyclerView.ViewHolder(bind.root) {
        fun setProductList(title: String?, list : List<ProductDto>) {
            (bind.recyclerView.adapter as ProductListAdapter).setProductList(list)
            bind.txtProductListTitle.text = title
        }
    }

    inner class ItemViewHolder(private val bind: ItemViewBind) : RecyclerView.ViewHolder(bind.root) {
        fun setItem(desc: String?) {
            bind.itemDesc.text = desc
            bind.itemLayout.setOnClickListener {
                Toast.makeText(bind.root.context, "$desc Layout Clicked", Toast.LENGTH_SHORT).show()
            }
            bind.itemImage.setOnClickListener {
                Toast.makeText(bind.root.context, "$desc Clicked", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ADVERTISE_LIST) {
            val bind: AdvertiseListViewBind = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.view_advertiselist, parent, false)
            bind.viewPager.adapter = AdvertiseListAdapter()
            AdvertiseListViewHolder(bind)
        } else if (viewType == VIEW_TYPE_PRODUCT_LIST) {
            val bind: ProductListViewBind = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.view_productlist, parent, false)
            bind.recyclerView.adapter = ProductListAdapter()
            ProductListViewHolder(bind)
        } else {
            val bind: ItemViewBind = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.view_item, parent, false)
            ItemViewHolder(bind)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == VIEW_TYPE_ADVERTISE_LIST) {
            (holder as AdvertiseListViewHolder).setAdvertiseList(plpList[position].advertiseList!!)
        } else if (holder.itemViewType == VIEW_TYPE_PRODUCT_LIST) {
            (holder as ProductListViewHolder).setProductList(plpList[position].productListTitle, plpList[position].productList!!)
        } else {
            (holder as ItemViewHolder).setItem(plpList[position].desc)
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