package com.example

import android.app.Application
import android.util.Log
import com.example.fcm.FcmTokenManager
import com.example.fcm.SmartWasteNotificationManager

class SmartWasteApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "SmartWasteApplication initialized")
        SmartWasteNotificationManager.createNotificationChannels(this)
        FcmTokenManager.ensureFirebaseInitialized(this)
    }

    companion object {
        private const val TAG = "SmartWasteApplication"
    }
}
