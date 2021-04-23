package com.lge.kotlinstudyapp.activity

import android.Manifest
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.digitopath.studylibrary.TestModule
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.lge.kotlinstudyapp.R
import com.lge.kotlinstudyapp.logd
import com.lge.kotlinstudyapp.logi


class ContractActivity : AppCompatActivity() {
    companion object {
        private const val TAG: String = "ContractActivity"
    }

    private val listContract = arrayListOf<String>()
    private lateinit var listView : ListView
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logi(TAG, "onCreate(LAYOUT ID : ${R.layout.activity_contract})")
        setContentView(R.layout.activity_contract)
        val btn1 = findViewById<Button>(R.id.btn1)
        btn1.setOnClickListener {
            val module = TestModule()
            Toast.makeText(this, "TestModule Result = ${module.getVersion(this)}\n", Toast.LENGTH_SHORT).show()
            checkPermAndGetContract()
        }
        listView = findViewById(R.id.listContract)
        adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listContract)
        listView.adapter = adapter
    }

    private fun checkPermAndGetContract() {
        Dexter.withContext(this)
            .withPermission(Manifest.permission.READ_CONTACTS)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    getContract()
                }
                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Toast.makeText(this@ContractActivity, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?, p1: PermissionToken?) {
                    p1?.continuePermissionRequest()
                }
            }).check()
    }

    private fun getContract() {
        contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null, null, null, null
        )?.let { cursor ->
            listContract.clear()
            cursor.moveToFirst()
            while(!cursor.isAfterLast) {
                val name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                logd(TAG, "이름 : $name, 번호 : $phoneNumber")
                listContract.add("이름 : $name, 번호 : $phoneNumber")
                cursor.moveToNext()
            }
            adapter.notifyDataSetChanged()
            cursor.close()
        }
    }
}