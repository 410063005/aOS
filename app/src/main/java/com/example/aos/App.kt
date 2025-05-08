package com.example.aos

import android.app.Application
import com.example.aos.service.GithubApiFactory

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        GithubApiFactory.application = this
    }
}