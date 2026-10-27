package com.example.fcm

import android.content.Context
import android.util.Log
import com.example.data.local.CollectionRequestEntity
import com.example.data.local.DriverApplicationEntity
import com.example.data.remote.FirestoreWasteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object FcmNotificationDispatcher {
    private const val TAG = "FcmNotifDispatcher"

    /**
     * Dispatches a Firebase Cloud Messaging status update to the resident
     * whose reported waste has been assigned to a driver for collection.
     */
    suspend fun dispatchCollectionAssignedUpdate(
        context: Context,
        request: CollectionRequestEntity,
        driver: DriverApplicationEntity,
        firestore: FirestoreWasteDataSource = FirestoreWasteDataSource()
    ) = withContext(Dispatchers.IO) {
        try {
            val cleanResidentId = request.residentId.replace("-", "_").replace(" ", "_")
            val targetTopic = "resident_$cleanResidentId"

            // Look up remote FCM token for resident or fallback to local cached token
            val remoteToken = firestore.getFcmTokenForUser(request.residentId)
            val activeToken = remoteToken ?: FcmTokenManager.getCachedToken(context) ?: "fcm_token_${cleanResidentId}"

            val etaMinutes = 14
            val title = "🚛 Waste Collection Assigned!"
            val body = "Driver ${driver.fullName} (${driver.vehicleRegNumber}) has been assigned to collect your waste (${request.trackingId}). ETA: ~$etaMinutes mins."

            // 1. Build authoritative FCM payload record for Firestore Cloud Function / Extension queue
            val fcmRecord = hashMapOf<String, Any>(
                "recipientId" to request.residentId,
                "recipientName" to request.residentName,
                "recipientPhone" to request.residentPhone,
                "fcmToken" to activeToken,
                "topic" to targetTopic,
                "type" to "WASTE_ASSIGNED",
                "title" to title,
                "body" to body,
                "requestId" to request.id,
                "trackingId" to request.trackingId,
                "driverName" to driver.fullName,
                "vehicleReg" to driver.vehicleRegNumber,
                "etaMinutes" to etaMinutes,
                "wasteCategory" to request.wasteCategory.title,
                "pickupAddress" to request.pickupAddress,
                "status" to "ON_THE_WAY",
                "deliveryChannel" to "FIREBASE_CLOUD_MESSAGING",
                "timestamp" to System.currentTimeMillis()
            )

            // Save in Firestore fcm_notifications collection for server-side Cloud Function dispatch
            val savedToFirestore = firestore.saveFcmNotificationDispatch(fcmRecord)
            Log.i(TAG, "FCM status update recorded in Firestore queue (success=$savedToFirestore) for resident ${request.residentName}")

            // 2. Deliver native notification on the Android device so resident gets instant heads-up alert
            SmartWasteNotificationManager.showWasteAssignedNotification(
                context = context,
                requestId = request.id,
                trackingId = request.trackingId,
                driverName = driver.fullName,
                vehicleReg = driver.vehicleRegNumber,
                etaMinutes = etaMinutes,
                wasteCategory = request.wasteCategory.title,
                pickupAddress = request.pickupAddress
            )

            // 3. Emit in-memory event to update live In-App notification feed & banner immediately
            val event = FcmStatusUpdateEvent(
                type = "WASTE_ASSIGNED",
                requestId = request.id,
                trackingId = request.trackingId,
                driverName = driver.fullName,
                vehicleReg = driver.vehicleRegNumber,
                etaMinutes = etaMinutes,
                wasteCategory = request.wasteCategory.title,
                pickupAddress = request.pickupAddress,
                title = title,
                body = body
            )
            FcmNotificationEvents.emitEvent(event)

        } catch (e: Exception) {
            Log.e(TAG, "Error in dispatchCollectionAssignedUpdate: ${e.message}", e)
        }
    }
}
