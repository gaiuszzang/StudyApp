package com.lge.kotlinstudyapp.activity

import android.os.Bundle
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
        //bind.recyclerView.addOnItemTouchListener(SmoothCrossTouchListener(bind.recyclerView))
        bind.recyclerView.addOnItemTouchListener(SmoothCrossTouchListener())
        viewModel.plpList.observe(this) { plpList -> plpAdapter.setPLPList(plpList) }
    }

    override fun onResume() {
        viewModel.updatePLPList()
        super.onResume()
    }

    class SmoothCrossTouchListener : RecyclerView.OnItemTouchListener {
        private val blockChildViewList = mutableListOf<View>()
        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            if (e.action == MotionEvent.ACTION_DOWN  && rv.scrollState == RecyclerView.SCROLL_STATE_SETTLING) {
                blockChildViewClick(rv.findChildViewUnder(e.x, e.y))
                rv.stopScroll()
            }
            if (e. action == MotionEvent.ACTION_UP) {
                allowChildViewClick()
            }
            return false
        }

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
            Log.d(TAG, "onTouchEvent() : ${e.action}")
        }

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
            Log.d(TAG, "onRequestDisallowInterceptTouchEvent : $disallowIntercept")
            allowChildViewClick()
        }

        @MainThread
        private fun blockChildViewClick(view: View?) {
            if (view != null && blockChildViewList.isEmpty()) {
                Log.d(TAG, "childView Disable")
                collectAndBlockChildViewClick(view)
            } else {
                Log.e(TAG, "childView Disable - failed. already disabled")
            }
        }

        @MainThread
        private fun allowChildViewClick() {
            if (blockChildViewList.isNotEmpty()) {
                Log.d(TAG, "childView Enable")
                for (view in blockChildViewList) {
                    view.isClickable = true
                }
                blockChildViewList.clear()
            } else {
                Log.w(TAG, "childView Enable - failed. already cleared")
            }
        }

        @MainThread
        private fun collectAndBlockChildViewClick(view: View) {
            if (view.isClickable) {
                blockChildViewList.add(view)
                view.isClickable = false
            }
            if (view is ViewGroup) {
                view.forEach { collectAndBlockChildViewClick(it) }
            }
        }
    }
    /*
    class SmoothCrossTouchListener(recyclerView: RecyclerView) : RecyclerView.OnItemTouchListener {
        var scrollSpeed = 0
        val scrollSpeedListener = object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                scrollSpeed = Math.abs(dy)
                Log.d(TAG, "scrollSpeeed = $scrollSpeed")
                super.onScrolled(recyclerView, dx, dy)
            }
        }
        init {
            recyclerView.addOnScrollListener(scrollSpeedListener)
        }
        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            if (e.action == MotionEvent.ACTION_DOWN  && rv.scrollState == RecyclerView.SCROLL_STATE_SETTLING && scrollSpeed < 60) {
                Log.d(TAG, "stopSettingScroll Cancel")
                rv.stopScroll()
            }
            return false
        }

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
            Log.d(TAG, "onTouchEvent() : ${e.action}")
        }

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
            Log.d(TAG, "onRequestDisallowInterceptTouchEvent : $disallowIntercept")
        }
    }*/
}