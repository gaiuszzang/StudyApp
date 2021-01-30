package com.lge.kotlinstudyapp.usecase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.lge.kotlinstudyapp.adapter.PLPMainAdapter
import com.lge.kotlinstudyapp.repo.Repo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PLPUseCase @Inject constructor(private val repo: Repo) {
    private val plpListLiveData = MutableLiveData<List<PLPMainAdapter.PLPItem>>()

    fun getPLPListLiveData(): LiveData<List<PLPMainAdapter.PLPItem>> = plpListLiveData
    suspend fun updatePLPList() = withContext(Dispatchers.Main) {
        val list = arrayListOf<PLPMainAdapter.PLPItem>()
        val adv1Defer = async(Dispatchers.IO) { return@async repo.getAdvertise1List() }
        val adv2Defer = async(Dispatchers.IO) { return@async repo.getAdvertise2List() }
        val prd1Defer = async(Dispatchers.IO) { return@async repo.getMainProduct1List() }
        val prd2Defer = async(Dispatchers.IO) { return@async repo.getMainProduct2List() }
        list.add(PLPMainAdapter.PLPItem(PLPMainAdapter.VIEW_TYPE_ADVERTISE_LIST, advertiseList = adv1Defer.await()))
        list.add(PLPMainAdapter.PLPItem(PLPMainAdapter.VIEW_TYPE_PRODUCT_LIST, productListTitle = "ProductList 1", productList = prd1Defer.await()))
        list.add(PLPMainAdapter.PLPItem(PLPMainAdapter.VIEW_TYPE_PRODUCT_LIST, productListTitle = "ProductList 2", productList = prd2Defer.await()))
        list.add(PLPMainAdapter.PLPItem(PLPMainAdapter.VIEW_TYPE_ADVERTISE_LIST, advertiseList = adv2Defer.await()))
        plpListLiveData.value = list
    }
}