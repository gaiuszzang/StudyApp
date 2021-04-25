package com.lge.kotlinstudyapp.activity

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.lge.kotlinstudyapp.R
import com.lge.kotlinstudyapp.adapter.PLPMainAdapter
import com.lge.kotlinstudyapp.databinding.PLPBind
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PLPActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "PLPActivity"
    }

    private val viewModel : PLPViewModel by viewModels()
    private val bind : PLPBind by lazy { DataBindingUtil.setContentView(this, R.layout.activity_plp) }
    private val plpAdapter = PLPMainAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind.lifecycleOwner = this
        bind.activity = this
        bind.vm = viewModel
        bind.recyclerView.adapter = plpAdapter
        bind.recyclerView.addOnItemTouchListener(SmoothCrossTouchListener())
        viewModel.plpList.observe(this) { plpList -> plpAdapter.setPLPList(plpList) }
    }

    override fun onResume() {
        viewModel.updatePLPList()
        super.onResume()
    }

    //---
    class SmoothCrossTouchListener : RecyclerView.OnItemTouchListener {
        private val viewClickEventBlocker = ViewClickEventBlocker()
        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            if (e.action == MotionEvent.ACTION_DOWN  && rv.scrollState == RecyclerView.SCROLL_STATE_SETTLING) {
                viewClickEventBlocker.blockChildViewClick(rv.findChildViewUnder(e.x, e.y))
                rv.stopScroll()
            }
            return false
        }

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
            //nothing to do
        }

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
            //nothing to do
        }


        inner class ViewClickEventBlocker {
            private val blockChildViewList = mutableListOf<View>()

            @MainThread
            fun blockChildViewClick(view: View?) {
                if (view != null && blockChildViewList.isEmpty()) {
                    collectAndBlockChildViewClick(view)
                    //Need to release the block immediately afterwards, that's why postAtFrontOfQueue
                    Handler(view.context.mainLooper).postAtFrontOfQueue {
                        allowChildViewClick()
                    }
                }
            }

            @MainThread
            fun allowChildViewClick() {
                if (blockChildViewList.isNotEmpty()) {
                    for (view in blockChildViewList) {
                        view.isEnabled = true
                    }
                    blockChildViewList.clear()
                }
            }

            @MainThread
            fun collectAndBlockChildViewClick(view: View) {
                if (view.isClickable && view.isEnabled) {
                    blockChildViewList.add(view)
                    view.isEnabled = false
                }
                if (view is ViewGroup) {
                    view.forEach { collectAndBlockChildViewClick(it) }
                }
            }
        }
    }
}