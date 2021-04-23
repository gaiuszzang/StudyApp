package com.digitopath.studylibrary.activity

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.digitopath.studylibrary.R
import com.digitopath.studylibrary.TestModule


class TestContractActivity : AppCompatActivity() {
    companion object {
        private const val TAG: String = "TestContractActivity"
    }

    private val listContract = arrayListOf<String>()
    private lateinit var listView : ListView
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(LAYOUT ID : ${R.layout.activity_contract})")
        setContentView(R.layout.activity_contract)
        val btn1 = findViewById<Button>(R.id.btnBust)
        btn1?.setOnClickListener {
            val module = TestModule()
            Toast.makeText(this, "TestModule Result = ${module.getVersion(this)}\n", Toast.LENGTH_SHORT).show()
        }
        listView = findViewById(R.id.listContract)
        adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listContract)
        listView.adapter = adapter
    }
}