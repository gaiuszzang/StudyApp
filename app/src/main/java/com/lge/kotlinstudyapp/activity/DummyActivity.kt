package com.lge.kotlinstudyapp.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.lge.kotlinstudyapp.R
import com.lge.kotlinstudyapp.logi

class DummyActivity : AppCompatActivity() {
    companion object {
        private const val TAG: String = "DummyActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logi(TAG, "onCreate()")
        setContentView(R.layout.activity_dummy)
        val btn1 = findViewById<Button>(R.id.btn1)
        btn1.setOnClickListener {
            val intent = Intent(this, DummyActivity::class.java)
            startActivity(intent)
        }
        val btn2 = findViewById<Button>(R.id.btn2)
        btn2.setOnClickListener {
            val intent = Intent(this, DummyActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
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