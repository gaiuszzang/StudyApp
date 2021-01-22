package com.lge.kotlinstudyapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.lge.kotlinstudyapp.activity.MainViewModel
import com.lge.kotlinstudyapp.repo.Repo
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class MainViewModelTest {
    //본 룰이 적용되어야 MutableLiveData set/postValue 시 MainThread 체크 하지 않는다.
    @Rule @JvmField
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun getsetTest() {
        val mockRepo = mock<Repo> {
            val lvData = MutableLiveData("this is test data")
            on { getStoredText() }.thenReturn(lvData)
            on { runBlocking { setStoredText(any()) } }.then {
                val arg0 = it.arguments[0]
                if (arg0 is String) {
                    lvData.value = arg0.toString()
                }
            }
        }
        runBlocking(Dispatchers.Main) {
            val vm = MainViewModel(mockRepo)
            println("before value = " + vm.text.value)
            vm.updateText("hello, world")
            println("after value = " + vm.text.value)
        }
    }
}
