package com.example.fcm

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.data.remote.FirestoreWasteDataSource
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

object FcmTokenManager {
    private const val TAG = "FcmTokenManager"
    private const val PREFS_NAME = "smart_waste_fcm_prefs"
    private const val KEY_FCM_TOKEN = "fcm_device_token"
    private const val KEY_REGISTERED_USER_ID = "fcm_registered_user_id"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Ensures FirebaseApp is safely initialized in the current process if google-services.json is present.
     */
    fun ensureFirebaseInitialized(context: Context): Boolean {
        return try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                try {
                    FirebaseApp.initializeApp(context.applicationContext)
                } catch (e: Exception) {
                    Log.d(TAG, "Firebase default initializeApp not available: ${e.message}")
                }
            }
            FirebaseApp.getApps(context).isNotEmpty()
        } catch (e: Throwable) {
            Log.d(TAG, "Handled Firebase initialization: ${e.message}")
            false
        }
    }

    /**
     * Validates whether Firebase is configured with genuine credentials rather than placeholder options.
     */
    fun isFirebaseConfigValid(context: Context): Boolean {
        return try {
            if (FirebaseApp.getApps(context).isEmpty()) return false
            val app = FirebaseApp.getInstance()
            val opts = app.options
            val appId = opts.applicationId
            val projId = opts.projectId
            val apiKey = opts.apiKey
            !appId.isNullOrBlank() &&
            !projId.isNullOrBlank() &&
            projId != "smartwaste-kigamboni" &&
            !apiKey.isNullOrBlank() &&
            !apiKey.contains("Dummy")
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Gets the locally cached FCM token, or null if not yet obtained.
     */
    fun getCachedToken(context: Context): String? {
        return getPrefs(context).getString(KEY_FCM_TOKEN, null)
    }

    /**
     * Stores the token in local preferences.
     */
    fun saveCachedToken(context: Context, token: String) {
        getPrefs(context).edit().putString(KEY_FCM_TOKEN, token).apply()
        Log.d(TAG, "Cached FCM token locally: ${token.take(15)}...")
    }

    /**
     * Fetches current FCM token from FirebaseMessaging if valid configuration is present,
     * or defaults gracefully to local device token without throwing FIS errors.
     */
    fun fetchAndRegisterToken(
        context: Context,
        userId: String? = null,
        role: String = "RESIDENT",
        firestore: FirestoreWasteDataSource? = null
    ) {
        val isFirebaseReady = ensureFirebaseInitialized(context)
        val hasValidConfig = isFirebaseReady && isFirebaseConfigValid(context)
        if (hasValidConfig) {
            try {
                FirebaseMessaging.getInstance().token
                    .addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.d(TAG, "FCM registration token fetching: ${task.exception?.message}")
                            fallbackToken(context, userId, role, firestore)
                            return@addOnCompleteListener
                        }
                        val token = task.result
                        if (token.isNullOrBlank()) {
                            fallbackToken(context, userId, role, firestore)
                            return@addOnCompleteListener
                        }
                        Log.i(TAG, "Retrieved FCM Token: ${token.take(20)}...")
                        saveCachedToken(context, token)

                        if (!userId.isNullOrBlank()) {
                            syncTokenWithBackend(context, userId, role, token, firestore)
                            subscribeResidentTopics(context, userId)
                        }
                    }
                return
            } catch (e: Throwable) {
                Log.d(TAG, "FirebaseMessaging service call skipped: ${e.message}")
            }
        }

        // Generate deterministic fallback token for simulated testing environment
        fallbackToken(context, userId, role, firestore)
    }

    private fun fallbackToken(
        context: Context,
        userId: String?,
        role: String,
        firestore: FirestoreWasteDataSource?
    ) {
        val fallback = getCachedToken(context) ?: "fcm_dev_${UUID.randomUUID().toString().take(12)}"
        saveCachedToken(context, fallback)
        if (!userId.isNullOrBlank()) {
            syncTokenWithBackend(context, userId, role, fallback, firestore)
        }
    }

    /**
     * Subscribes resident to FCM notification topics for broadcast and personal status updates.
     */
    fun subscribeResidentTopics(userId: String) {
        val cleanId = userId.replace("-", "_").replace(" ", "_")
        try {
            FirebaseMessaging.getInstance().subscribeToTopic("all_residents")
                .addOnSuccessListener { Log.d(TAG, "Subscribed to topic: all_residents") }
            FirebaseMessaging.getInstance().subscribeToTopic("resident_$cleanId")
                .addOnSuccessListener { Log.d(TAG, "Subscribed to topic: resident_$cleanId") }
            FirebaseMessaging.getInstance().subscribeToTopic("waste_updates")
                .addOnSuccessListener { Log.d(TAG, "Subscribed to topic: waste_updates") }
        } catch (e: Throwable) {
            Log.d(TAG, "Could not subscribe to topics: ${e.message}")
        }
    }

    fun subscribeResidentTopics(context: Context, userId: String) {
        if (!ensureFirebaseInitialized(context) || !isFirebaseConfigValid(context)) {
            Log.d(TAG, "Skipping remote FCM topic subscription (unprovisioned Firebase)")
            return
        }
        subscribeResidentTopics(userId)
    }

    /**
     * Synchronizes the FCM token to Firestore and marks user as registered.
     */
    fun syncTokenWithBackend(
        context: Context,
        userId: String,
        role: String,
        token: String,
        firestore: FirestoreWasteDataSource?
    ) {
        getPrefs(context).edit().putString(KEY_REGISTERED_USER_ID, userId).apply()
        val fs = firestore ?: FirestoreWasteDataSource()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                fs.saveFcmToken(userId, token, role)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to sync token to Firestore: ${e.message}")
            }
        }
    }
}
