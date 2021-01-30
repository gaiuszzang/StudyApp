package com.lge.kotlinstudyapp.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.lge.kotlinstudyapp.R
import com.lge.kotlinstudyapp.adapter.PLPMainAdapter
import com.lge.kotlinstudyapp.databinding.PLPBind
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PLPActivity : AppCompatActivity() {

    private val viewModel : PLPViewModel by viewModels()
    private val bind : PLPBind by lazy { DataBindingUtil.setContentView(this, R.layout.activity_plp) }
    private val plpAdapter = PLPMainAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind.lifecycleOwner = this
        bind.activity = this
        bind.vm = viewModel
        bind.recyclerView.adapter = plpAdapter
        viewModel.plpList.observe(this) { plpList -> plpAdapter.setPLPList(plpList) }
    }

    override fun onResume() {
        viewModel.updatePLPList()
        super.onResume()
    }
}