package com.example.fcm

import android.util.Log
import com.example.data.model.InAppNotification
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

data class FcmStatusUpdateEvent(
    val type: String,
    val requestId: String,
    val trackingId: String,
    val driverName: String,
    val vehicleReg: String,
    val etaMinutes: Int,
    val wasteCategory: String,
    val pickupAddress: String,
    val title: String,
    val body: String,
    val timestamp: Long = System.currentTimeMillis()
)

object FcmNotificationEvents {
    private val _events = MutableSharedFlow<FcmStatusUpdateEvent>(replay = 1, extraBufferCapacity = 10)
    val events: SharedFlow<FcmStatusUpdateEvent> = _events.asSharedFlow()

    fun emitEvent(event: FcmStatusUpdateEvent) {
        _events.tryEmit(event)
    }
}

class SmartWasteMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "SmartWasteMessagingSvc"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i(TAG, "Refreshed FCM registration token: ${token.take(20)}...")
        FcmTokenManager.saveCachedToken(applicationContext, token)

        // Resubscribe to core broadcast topics
        FcmTokenManager.subscribeResidentTopics("global")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.i(TAG, "FCM Message Received from: ${remoteMessage.from}")

        val data = remoteMessage.data
        val notif = remoteMessage.notification

        val type = data["type"] ?: "WASTE_ASSIGNED"
        val requestId = data["requestId"] ?: ""
        val trackingId = data["trackingId"] ?: (notif?.title?.substringAfter(":")?.trim() ?: "WST-2026")
        val driverName = data["driverName"] ?: "Municipal Driver"
        val vehicleReg = data["vehicleReg"] ?: "T 123 ABC"
        val etaMinutes = data["etaMinutes"]?.toIntOrNull() ?: 14
        val wasteCategory = data["wasteCategory"] ?: "Household Waste"
        val pickupAddress = data["pickupAddress"] ?: "Kigamboni Ward"

        val title = notif?.title ?: data["title"] ?: "🚛 Waste Collection Assigned!"
        val body = notif?.body ?: data["body"] ?: "Driver $driverName ($vehicleReg) has been assigned to collect your waste."

        Log.d(TAG, "Processing FCM push payload: type=$type, request=$requestId, tracking=$trackingId")

        // 1. Post native Android system notification
        if (type == "WASTE_ASSIGNED" || type == "DRIVER_ASSIGNED") {
            SmartWasteNotificationManager.showWasteAssignedNotification(
                context = applicationContext,
                requestId = requestId,
                trackingId = trackingId,
                driverName = driverName,
                vehicleReg = vehicleReg,
                etaMinutes = etaMinutes,
                wasteCategory = wasteCategory,
                pickupAddress = pickupAddress
            )
        } else {
            SmartWasteNotificationManager.showStatusUpdateNotification(
                context = applicationContext,
                requestId = requestId,
                trackingId = trackingId,
                title = title,
                message = body
            )
        }

        // 2. Broadcast event to live ViewModels / In-App notification list
        val event = FcmStatusUpdateEvent(
            type = type,
            requestId = requestId,
            trackingId = trackingId,
            driverName = driverName,
            vehicleReg = vehicleReg,
            etaMinutes = etaMinutes,
            wasteCategory = wasteCategory,
            pickupAddress = pickupAddress,
            title = title,
            body = body
        )
        FcmNotificationEvents.emitEvent(event)
    }
}
