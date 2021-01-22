package com.lge.kotlinstudyapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lge.kotlinstudyapp.R
import com.lge.kotlinstudyapp.databinding.ProductViewBind
import com.lge.kotlinstudyapp.db.ProductDto

class ProductListAdapter : RecyclerView.Adapter<ProductListAdapter.ProductListViewHolder>() {
    private val productList = arrayListOf<ProductDto>()
    inner class ProductListViewHolder(private val bind: ProductViewBind) : RecyclerView.ViewHolder(bind.root) {
        fun setView(item: ProductDto) {
            bind.productPrice.text = item.productPrice.toString()
            bind.productName.text = item.productName
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductListViewHolder {
        return ProductListViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.view_product, parent, false))
    }
    override fun onBindViewHolder(holder: ProductListViewHolder, position: Int) {
        holder.setView(productList[position])
    }
    override fun getItemCount(): Int = productList.size

    fun setProductList(list : List<ProductDto>) {
        productList.clear()
        productList.addAll(list)
        notifyDataSetChanged()
    }
}