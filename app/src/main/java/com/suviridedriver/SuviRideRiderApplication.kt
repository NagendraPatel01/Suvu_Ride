package com.suviridedriver

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SuviRideRiderApplication : Application(){
 private var instance: SuviRideRiderApplication? = null

    fun getInstance(): SuviRideRiderApplication? {
        return instance
    }

    fun getContext(): Context? {
        return instance
    }

    override fun onCreate() {
        instance = this
        super.onCreate()
    }
}