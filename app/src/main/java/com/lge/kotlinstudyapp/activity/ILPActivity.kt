package com.lge.kotlinstudyapp.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.lge.kotlinstudyapp.R
import com.lge.kotlinstudyapp.adapter.ILPMainAdapter
import com.lge.kotlinstudyapp.adapter.ItemDiffCallback
import com.lge.kotlinstudyapp.databinding.ILPBind
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ILPActivity : AppCompatActivity() {
    companion object {
        private val TAG = javaClass.name
    }
    private val viewModel : ILPViewModel by viewModels()
    private val bind : ILPBind by lazy { DataBindingUtil.setContentView(this, R.layout.activity_ilp) }
    private val ilpAdapter = ILPMainAdapter(ItemDiffCallback())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind.lifecycleOwner = this
        bind.activity = this
        bind.vm = viewModel
        bind.recyclerView.adapter = ilpAdapter.withLoadStateFooter(ILPMainAdapter.ItemLoadStateAdapter())
        lifecycleScope.launch {
            viewModel.itemListFlow.collectLatest { pagingData ->
                ilpAdapter.submitData(pagingData)
            }
        }
    }

}