package com.example.fcm

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R

object SmartWasteNotificationManager {
    private const val TAG = "SmartWasteNotifMgr"
    const val CHANNEL_ID_WASTE_UPDATES = "smart_waste_collection_channel"
    const val CHANNEL_NAME = "Waste Collection Updates"
    const val CHANNEL_DESC = "Real-time updates when reported waste is assigned for collection, truck is on the way, or collection is completed."

    const val EXTRA_ROUTE = "extra_route"
    const val EXTRA_REQUEST_ID = "extra_request_id"
    const val EXTRA_TRACKING_ID = "extra_tracking_id"
    const val EXTRA_ACTION = "extra_action"

    /**
     * Initializes the Android Notification Channel (Required for Android 8.0+ / API 26+).
     */
    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            if (notificationManager != null) {
                val channel = NotificationChannel(
                    CHANNEL_ID_WASTE_UPDATES,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = CHANNEL_DESC
                    enableLights(true)
                    lightColor = Color.parseColor("#059669")
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 300, 200, 300)
                    setShowBadge(true)
                }
                notificationManager.createNotificationChannel(channel)
                Log.d(TAG, "Notification channel created: $CHANNEL_ID_WASTE_UPDATES")
            }
        }
    }

    /**
     * Checks if the application currently has permission to post notifications.
     */
    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    /**
     * Displays a system notification informing the resident that their reported waste has been assigned to a driver.
     */
    fun showWasteAssignedNotification(
        context: Context,
        requestId: String,
        trackingId: String,
        driverName: String,
        vehicleReg: String,
        etaMinutes: Int = 14,
        wasteCategory: String = "General Waste",
        pickupAddress: String = "Kigamboni Ward"
    ) {
        if (!hasNotificationPermission(context)) {
            Log.w(TAG, "Cannot show notification: POST_NOTIFICATIONS permission not granted")
            return
        }

        createNotificationChannels(context)

        // Intent to launch app when notification tapped
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_ROUTE, "TRACK_REQUEST")
            putExtra(EXTRA_REQUEST_ID, requestId)
            putExtra(EXTRA_TRACKING_ID, trackingId)
            putExtra(EXTRA_ACTION, "VIEW_TRACKING")
        }

        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            requestId.hashCode(),
            intent,
            pendingIntentFlags
        )

        val title = "🚛 Waste Collection Assigned!"
        val shortBody = "Driver $driverName ($vehicleReg) assigned for $trackingId. ETA: ~$etaMinutes mins."
        val bigText = "Good news! Your reported waste collection ($trackingId - $wasteCategory) has been assigned.\n\n" +
                "• Assigned Driver: $driverName\n" +
                "• Vehicle Reg: $vehicleReg\n" +
                "• Estimated Arrival: ~$etaMinutes mins\n" +
                "• Pickup Location: $pickupAddress\n\n" +
                "Tap here to track your driver on the live map in real time."

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID_WASTE_UPDATES)
            .setSmallIcon(R.drawable.ic_stat_waste_truck)
            .setContentTitle(title)
            .setContentText(shortBody)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setColor(Color.parseColor("#059669"))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        val notificationId = ("assign_" + requestId).hashCode()
        try {
            NotificationManagerCompat.from(context).notify(notificationId, notificationBuilder.build())
            Log.i(TAG, "Displayed waste assigned notification for request: $trackingId ($requestId)")
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException posting notification: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Error posting notification: ${e.message}", e)
        }
    }

    /**
     * Displays a system notification for generic status transitions (e.g. driver nearby, collected).
     */
    fun showStatusUpdateNotification(
        context: Context,
        requestId: String,
        trackingId: String,
        title: String,
        message: String
    ) {
        if (!hasNotificationPermission(context)) return

        createNotificationChannels(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_ROUTE, "TRACK_REQUEST")
            putExtra(EXTRA_REQUEST_ID, requestId)
            putExtra(EXTRA_TRACKING_ID, trackingId)
        }

        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            requestId.hashCode() + 1,
            intent,
            pendingIntentFlags
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID_WASTE_UPDATES)
            .setSmallIcon(R.drawable.ic_stat_waste_truck)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setColor(Color.parseColor("#0284C7"))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationId = ("status_" + requestId).hashCode()
        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (e: Exception) {
            Log.e(TAG, "Error posting status notification: ${e.message}")
        }
    }
}
