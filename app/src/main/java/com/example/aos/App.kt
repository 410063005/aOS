package com.example.aos

import android.app.Application

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        _appContext = this
    }

    companion object {
        private lateinit var _appContext: Application
        val application: Application
            get() = _appContext
    }
}