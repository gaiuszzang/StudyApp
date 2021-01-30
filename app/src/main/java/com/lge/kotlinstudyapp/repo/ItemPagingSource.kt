package com.lge.kotlinstudyapp.repo

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.lge.kotlinstudyapp.logd
import com.lge.kotlinstudyapp.server.AcanelServerRetrofitService
import com.lge.kotlinstudyapp.server.data.ItemDto
import kotlinx.coroutines.delay

object ItemPagingSource : PagingSource<Int, ItemDto>() {
    private const val TAG = "ItemPagingSource"
    private val networkSource = AcanelServerRetrofitService.service
    override fun getRefreshKey(state: PagingState<Int, ItemDto>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ItemDto> {
        return try {
            val nextPageNumber = params.key ?: 0
            val pageSize = params.loadSize
            logd(TAG, "loading(pageNo = $nextPageNumber , pageSize = $pageSize)")
            val list = networkSource.getItemList(nextPageNumber)
            delay(1000)
            logd(TAG, "loaded(pageNo = $nextPageNumber , pageSize = $pageSize)")
            LoadResult.Page(
                data = list,
                prevKey = null,
                nextKey = if (list.isNotEmpty()) nextPageNumber + 1 else null
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}