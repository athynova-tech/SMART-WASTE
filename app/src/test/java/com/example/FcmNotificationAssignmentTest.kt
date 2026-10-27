package com.example

import android.app.NotificationManager
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.CollectionRequestEntity
import com.example.data.local.DriverApplicationEntity
import com.example.data.local.UserEntity
import com.example.data.model.CollectionStatus
import com.example.data.model.DriverApplicationStatus
import com.example.data.model.PaymentMethod
import com.example.data.model.RequestUrgency
import com.example.data.model.UserRole
import com.example.data.model.WasteCategory
import com.example.data.remote.FirestoreWasteDataSource
import com.example.data.repository.WasteRepository
import com.example.fcm.FcmNotificationEvents
import com.example.fcm.FcmTokenManager
import com.example.fcm.SmartWasteNotificationManager
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class FcmNotificationAssignmentTest {

    private lateinit var context: Context
    private lateinit var db: AppDatabase
    private lateinit var repository: WasteRepository

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = WasteRepository(db = db, firestoreDataSource = FirestoreWasteDataSource(firestore = null), context = context)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testNotificationChannelCreation() {
        SmartWasteNotificationManager.createNotificationChannels(context)
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = nm.getNotificationChannel(SmartWasteNotificationManager.CHANNEL_ID_WASTE_UPDATES)
        assertNotNull("Notification channel should be created", channel)
        assertEquals(SmartWasteNotificationManager.CHANNEL_NAME, channel.name.toString())
    }

    @Test
    fun testFcmTokenManagerCaching() {
        val testToken = "fcm_mock_token_resident_test_${System.currentTimeMillis()}"
        FcmTokenManager.saveCachedToken(context, testToken)
        val retrieved = FcmTokenManager.getCachedToken(context)
        assertEquals(testToken, retrieved)
    }

    @Test
    fun testDriverAssignmentTriggersFcmUpdate() = runBlocking {
        val resident = UserEntity(
            id = "resident_fcm_test",
            fullName = "Halima Mwinyi",
            email = "halima.mwinyi@example.com",
            phone = "+255 714 888 999",
            role = UserRole.RESIDENT,
            address = "Kigamboni Ferry Road",
            fcmToken = "fcm_token_halima_123"
        )
        db.userDao().insertUser(resident)

        val requestId = "req_fcm_assign_1"
        val request = CollectionRequestEntity(
            id = requestId,
            trackingId = "KGB-PL-9021",
            residentId = resident.id,
            residentName = resident.fullName,
            residentPhone = resident.phone,
            wasteCategory = WasteCategory.PLASTIC,
            quantityKg = 35,
            bagCount = 2,
            description = "Plastic bottles and containers",
            pickupAddress = "Kigamboni Ferry Terminal",
            ward = "Kigamboni",
            district = "Kigamboni District",
            latitude = -6.8280,
            longitude = 39.3020,
            preferredDate = "2026-09-05",
            preferredTimeSlot = "Morning",
            urgency = RequestUrgency.NORMAL,
            estimatedPriceTzs = 7000,
            paymentMethod = PaymentMethod.MPESA,
            collectionStatus = CollectionStatus.WAITING_ASSIGNMENT
        )
        db.collectionRequestDao().insertRequest(request)

        val driver = DriverApplicationEntity(
            id = "drv_app_fcm_1",
            userId = "usr_drv_1",
            fullName = "Juma Bakari",
            phone = "+255 713 000 111",
            email = "juma.driver@example.com",
            nidaMasked = "********1234",
            licenseNumber = "TZ-DL-998877",
            licenseClass = "Class C",
            licenseExpiryDate = "2028-10-10",
            vehicleRegNumber = "T 982 DKK",
            vehicleType = "Medium Tipper Truck (4 Ton)",
            vehicleCapacityKg = 4000,
            insuranceExpiryDate = "2027-01-01",
            preferredZone = "Kigamboni Ward",
            status = DriverApplicationStatus.APPROVED,
            assignedDriverId = "DRV-KGB-007"
        )
        db.driverApplicationDao().insertApplication(driver)

        // Listen for FCM notification event
        coroutineScope {
            val fcmEventJob = async {
                FcmNotificationEvents.events.first()
            }

            // Perform assignment
            repository.assignDriverToRequest(
                requestId = requestId,
                driver = driver,
                commissionerName = "Hon. Waste Commissioner"
            )

            // 1. Verify database state updated
            val updatedRequest = db.collectionRequestDao().getRequestById(requestId)
            assertNotNull(updatedRequest)
            assertEquals(CollectionStatus.ON_THE_WAY, updatedRequest!!.collectionStatus)
            assertEquals("DRV-KGB-007", updatedRequest.assignedDriverId)
            assertEquals("Juma Bakari", updatedRequest.assignedDriverName)
            assertEquals("T 982 DKK", updatedRequest.vehicleRegNumber)
            assertEquals(14, updatedRequest.etaMinutes)

            // 2. Verify FCM event emitted
            val fcmEvent = fcmEventJob.await()
            assertNotNull("FCM status update event should be dispatched", fcmEvent)
            assertEquals("WASTE_ASSIGNED", fcmEvent.type)
            assertEquals(requestId, fcmEvent.requestId)
            assertEquals("KGB-PL-9021", fcmEvent.trackingId)
            assertEquals("Juma Bakari", fcmEvent.driverName)
            assertEquals("T 982 DKK", fcmEvent.vehicleReg)
            assertTrue(fcmEvent.body.contains("Juma Bakari"))
            assertTrue(fcmEvent.body.contains("T 982 DKK"))
        }
    }
}
