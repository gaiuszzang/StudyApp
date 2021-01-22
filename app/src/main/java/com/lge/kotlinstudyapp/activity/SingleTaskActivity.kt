package com.lge.kotlinstudyapp.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.lge.kotlinstudyapp.R
import com.lge.kotlinstudyapp.logi

class SingleTaskActivity  : AppCompatActivity() {
    companion object {
        private const val TAG = "SingleTaskActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logi(TAG, "onCreate()")
        setContentView(R.layout.activity_singletask)
        val btnSelf = findViewById<Button>(R.id.btnSingleTask)
        btnSelf.setOnClickListener {
            val intent = Intent(this, SingleTaskActivity::class.java)
            startActivity(intent)
        }
        val btnDummy = findViewById<Button>(R.id.btnDummy)
        btnDummy.setOnClickListener {
            val intent = Intent(this, DummyActivity::class.java)
            startActivity(intent)
        }
    }
    override fun onRestart() {
        super.onRestart()
        logi(TAG, "onRestart()")
    }
    override fun onStart() {
        super.onStart()
        logi(TAG, "onStart()")
    }
    override fun onResume() {
        super.onResume()
        logi(TAG, "onResume()")
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        logi(TAG, "onCreateOptionsMenu()")
        return super.onCreateOptionsMenu(menu)
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        logi(TAG, "onSaveInstanceState()")
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        logi(TAG, "onRestoreInstanceState")
    }
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        logi(TAG, "onNewIntent()")
    }
    override fun onPause() {
        super.onPause()
        logi(TAG, "onPause()")
    }
    override fun onStop() {
        super.onStop()
        logi(TAG, "onStop()")
    }
    override fun onDestroy() {
        super.onDestroy()
        logi(TAG, "onDestroy()")
    }
}