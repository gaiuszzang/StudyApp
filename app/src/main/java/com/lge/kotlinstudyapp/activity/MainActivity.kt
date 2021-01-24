package com.lge.kotlinstudyapp.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.work.*
import com.lge.kotlinstudyapp.*
import com.lge.kotlinstudyapp.R
import com.lge.kotlinstudyapp.databinding.MainBind
import com.lge.kotlinstudyapp.service.TestBackgroundService
import com.lge.kotlinstudyapp.service.TestJobIntentService
import com.lge.kotlinstudyapp.service.TestJobIntentService.Companion.TEST_ACTION
import com.lge.kotlinstudyapp.worker.LogWorker
import com.lge.kotlinstudyapp.worker.TestWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG: String = "MainActivity"
    }

    private val vm : MainViewModel by viewModels()
    private val bind : MainBind by lazy { DataBindingUtil.setContentView(this, R.layout.activity_main) }

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logi(TAG, "onCreate()")
        bind.activity = this
        bind.vm = vm
        bind.lifecycleOwner = this
    }
    override fun onStart() {
        super.onStart()
        logi(TAG, "onStart()")
    }

    override fun onRestart() {
        super.onRestart()
        logi(TAG, "onRestart()")
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
    fun onButton1Click() {
        lifecycleScope.launch(Dispatchers.IO) {
            vm.updateText("Button 1")
        }
    }
    fun onButton2Click() {
        lifecycleScope.launch(Dispatchers.IO) {
            vm.updateText("Button 2")
        }
    }
    fun onButton3Click() {
        val intent = Intent(this, SingleTaskActivity::class.java)
        startActivity(intent)
    }
    fun onButton4Click() {
        val intent = Intent(this, SingleTaskActivity::class.java)
        startForResult.launch(intent)
    }
    fun onButton5Click() {
        val intent = Intent(this, SingleInstanceActivity::class.java)
        startActivity(intent)
    }
    fun onButton6Click() {
        val intent = Intent(this, SingleInstanceActivity::class.java)
        startForResult.launch(intent)
    }
    fun onButton7Click() {
        val intent = Intent(this, DummyActivity::class.java)
        startActivity(intent)
    }
    fun onButtonUploadClick() {
        vm.uploadFile()
    }

    fun onButton8Click() {
        val testWork = OneTimeWorkRequestBuilder<TestWorker>()
                        .setInputData(workDataOf("LOOP_TIME" to 30))
                        .build()
        WorkManager
            .getInstance(applicationContext)
            .enqueueUniqueWork(TestWorker.TAG, ExistingWorkPolicy.KEEP, testWork)
    }

    fun onButton9Click() {
        val workIntent = Intent(TEST_ACTION)
        workIntent.putExtra("LOOP_TIME", 30)
        TestJobIntentService.enqueueWork(this, workIntent)
    }

    fun onButton10Click() {
        val intent = Intent(this, ContractActivity::class.java)
        startActivity(intent)
    }

    fun onButton11Click() = lifecycleScope.launch {
       vm.putDeviceLog()
    }

    //PeriodicWork Start
    fun onButton12Click() {
        val logWork = PeriodicWorkRequestBuilder<LogWorker>(
            1, TimeUnit.HOURS,
            15, TimeUnit.MINUTES).build()
        WorkManager
            .getInstance(applicationContext)
            .enqueueUniquePeriodicWork(LogWorker.TAG, ExistingPeriodicWorkPolicy.KEEP, logWork)
    }
    //PeriodicWork Stop
    fun onButton13Click() {
        WorkManager.getInstance(applicationContext).cancelUniqueWork(LogWorker.TAG)
    }

    fun onButton14Click() {
        val intent = Intent(this, TestBackgroundService::class.java)
        logd(TAG, "start TestBackgroundService")
        startService(intent)
    }

    fun onButton15Click() {
        val intent = Intent(this, TestBackgroundService::class.java)
        logd(TAG, "start TestBackgroundService")
        startForegroundService(intent)
    }

    private val startForAuthResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result: ActivityResult ->
        when (result.resultCode) {
            0 -> {
                val intent = result.data
                val accessToken = intent?.getStringExtra("access_token") ?: "(not delivered)"
                Toast.makeText(this@MainActivity, "Login Succeed.\naccessToken=$accessToken", Toast.LENGTH_SHORT).show()
            }
            1 -> Toast.makeText(this@MainActivity, "Login Failed", Toast.LENGTH_SHORT).show()
            else -> Toast.makeText(this@MainActivity, "Unavailable to access ${result.resultCode}", Toast.LENGTH_SHORT).show()
        }
    }
    fun onButton16Click() {
        val intent = Intent()
        intent.setClassName("com.nookied.cidersso", "com.nookied.cidersso.activity.AuthActivity")
        startForAuthResult.launch(intent)
    }

    fun onButton17Click() {
        val intent = Intent()
        intent.setClassName("com.nookied.cidersso", "com.nookied.cidersso.activity.AuthActivity")
        intent.putExtra("FROM", "CiderModule")
        startForAuthResult.launch(intent)
    }
    fun onButton18Click() {
        val intent = Intent(this, PLPActivity::class.java)
        startActivity(intent)
    }
    fun onButton19Click() {
        val intent = Intent(this, MusicActivity::class.java)
        startActivity(intent)
    }
}
