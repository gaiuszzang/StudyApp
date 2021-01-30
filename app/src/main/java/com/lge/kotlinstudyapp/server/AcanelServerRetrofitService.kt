package com.lge.kotlinstudyapp.server

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AcanelServerRetrofitService {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://acanel.xyz:9105/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service : AcanelServer by lazy {retrofit.create(AcanelServer::class.java)}
}