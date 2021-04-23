package com.lge.kotlinstudyapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lge.kotlinstudyapp.R
import com.lge.kotlinstudyapp.databinding.ProductViewBind
import com.lge.kotlinstudyapp.server.data.ProductDto

class ProductListAdapter : RecyclerView.Adapter<ProductListAdapter.ProductListViewHolder>() {
    private val productList = arrayListOf<ProductDto>()
    inner class ProductListViewHolder(private val bind: ProductViewBind) : RecyclerView.ViewHolder(bind.root) {
        fun setView(item: ProductDto) {
            bind.productPrice.text = item.productPrice.toString()
            bind.productName.text = item.productName
            bind.productImage.setOnClickListener {
                Toast.makeText(bind.root.context, "Url : ${item.productImgUrl ?: "null"}", Toast.LENGTH_SHORT).show()
            }
            item.productImgUrl?.let {
                Glide.with(bind.root).load(item.productImgUrl).into(bind.productImage)
            }
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