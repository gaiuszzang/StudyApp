package com.digitopath.studylibrary

import android.content.Context

class TestModule {

    fun sum(a: Int, b: Int): Int {
        return a + b
    }
    fun concat(s: String, t: String): String {
        return s + t
    }

    fun getVersion(context: Context): String {
        return context.getString(R.string.test_module_version)
    }
}