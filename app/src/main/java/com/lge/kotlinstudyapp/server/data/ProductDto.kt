package com.lge.kotlinstudyapp.server.data

data class ProductDto(
    val productId: Long,
    val productImgUrl: String? = null,
    val productName: String,
    val productPrice: Int,
    val productType: Int)
