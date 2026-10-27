package com.example.data.remote

import android.util.Log
import com.example.data.local.CollectionRequestEntity
import com.example.data.local.ComplaintEntity
import com.example.data.local.UserEntity
import com.example.data.model.CollectionStatus
import com.example.data.model.PaymentMethod
import com.example.data.model.RequestUrgency
import com.example.data.model.UserRole
import com.example.data.model.WasteCategory
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class FirestoreWasteDataSource(
    private val firestore: FirebaseFirestore? = try {
        FirebaseFirestore.getInstance()
    } catch (e: Throwable) {
        null
    }
) {
    companion object {
        private const val TAG = "FirestoreWasteDataSource"
        const val COLLECTION_USERS = "users"
        const val COLLECTION_DRIVER_APPLICATIONS = "driver_applications"
        const val COLLECTION_REPORTS = "waste_collection_reports"
        const val COLLECTION_OVERFLOW_BINS = "overflow_bin_reports"
        const val COLLECTION_OTP_VERIFICATIONS = "otp_verifications"
        const val COLLECTION_MAIL = "mail"
        const val COLLECTION_FCM_TOKENS = "fcm_tokens"
        const val COLLECTION_FCM_NOTIFICATIONS = "fcm_notifications"
    }

    /**
     * Stores or updates a user profile and role in Firestore.
     */
    suspend fun saveUserRole(user: UserEntity): Boolean {
        val fs = firestore ?: run {
            Log.d(TAG, "Firestore offline: skipping remote user save for ${user.id}")
            return false
        }
        return try {
            val userMap = hashMapOf(
                "id" to user.id,
                "fullName" to user.fullName,
                "email" to user.email,
                "phone" to user.phone,
                "role" to user.role.name,
                "nidaMasked" to user.nidaMasked,
                "address" to user.address,
                "ward" to user.ward,
                "district" to user.district,
                "licenseId" to (user.licenseId ?: ""),
                "isActive" to user.isActive,
                "emailVerified" to user.emailVerified,
                "fcmToken" to (user.fcmToken ?: ""),
                "createdAt" to user.createdAt,
                "updatedAt" to System.currentTimeMillis()
            )
            fs.collection(COLLECTION_USERS)
                .document(user.id)
                .set(userMap, SetOptions.merge())
                .await()
            Log.i(TAG, "Successfully stored user ${user.fullName} [Role: ${user.role.name}] in Firestore")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error saving user role to Firestore: ${e.message}", e)
            false
        }
    }

    /**
     * Fetches user profile from Firestore by user ID.
     */
    suspend fun getUserById(userId: String): UserEntity? {
        val fs = firestore ?: return null
        return try {
            val doc = fs.collection(COLLECTION_USERS).document(userId).get().await()
            if (doc.exists()) {
                mapDocToUserEntity(doc.id, doc.data ?: emptyMap())
            } else null
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching user from Firestore by id: ${e.message}", e)
            null
        }
    }

    /**
     * Fetches user profile from Firestore by email address.
     */
    suspend fun getUserByEmail(email: String): UserEntity? {
        val fs = firestore ?: return null
        return try {
            val normalized = email.trim().lowercase()
            val querySnapshot = fs.collection(COLLECTION_USERS)
                .whereEqualTo("email", normalized)
                .limit(1)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val doc = querySnapshot.documents.first()
                mapDocToUserEntity(doc.id, doc.data ?: emptyMap())
            } else {
                // Fallback scan if case-sensitive indexing differs
                val allDocs = fs.collection(COLLECTION_USERS).limit(50).get().await()
                val match = allDocs.documents.firstOrNull {
                    it.getString("email")?.trim()?.equals(normalized, ignoreCase = true) == true
                }
                match?.let { mapDocToUserEntity(it.id, it.data ?: emptyMap()) }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching user by email from Firestore: ${e.message}", e)
            null
        }
    }

    /**
     * Fetches user profile by identifier (ID, email, or phone).
     */
    suspend fun getUserByIdentifier(identifier: String): UserEntity? {
        val clean = identifier.trim()
        if (clean.isBlank()) return null

        // 1. Direct Document ID
        val direct = getUserById(clean)
        if (direct != null) return direct

        // 2. Query by Email
        if (clean.contains("@")) {
            val byEmail = getUserByEmail(clean)
            if (byEmail != null) return byEmail
        }

        // 3. Query by Phone or License ID
        val fs = firestore ?: return null
        return try {
            val cleanPhone = clean.replace(" ", "")
            val allUsers = fs.collection(COLLECTION_USERS).limit(50).get().await()
            val doc = allUsers.documents.firstOrNull { d ->
                val p = d.getString("phone")?.replace(" ", "") ?: ""
                val l = d.getString("licenseId") ?: ""
                val e = d.getString("email") ?: ""
                val id = d.getString("id") ?: d.id
                id.equals(clean, ignoreCase = true) ||
                e.equals(clean, ignoreCase = true) ||
                (p.isNotBlank() && (p.contains(cleanPhone) || cleanPhone.contains(p))) ||
                (l.isNotBlank() && l.equals(clean, ignoreCase = true))
            }
            doc?.let { mapDocToUserEntity(it.id, it.data ?: emptyMap()) }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching user by identifier: ${e.message}", e)
            null
        }
    }

    /**
     * Queries the authoritative UserRole from Firestore for access control.
     */
    suspend fun getUserRoleInFirestore(identifier: String): UserRole? {
        val user = getUserByIdentifier(identifier)
        return user?.role
    }

    /**
     * Stores or updates a driver application in Firestore.
     */
    suspend fun saveDriverApplication(app: com.example.data.local.DriverApplicationEntity): Boolean {
        val fs = firestore ?: return false
        return try {
            val appMap = hashMapOf(
                "id" to app.id,
                "userId" to app.userId,
                "fullName" to app.fullName,
                "phone" to app.phone,
                "email" to app.email,
                "nidaMasked" to app.nidaMasked,
                "licenseNumber" to app.licenseNumber,
                "licenseClass" to app.licenseClass,
                "licenseExpiryDate" to app.licenseExpiryDate,
                "vehicleRegNumber" to app.vehicleRegNumber,
                "vehicleType" to app.vehicleType,
                "vehicleCapacityKg" to app.vehicleCapacityKg,
                "insuranceExpiryDate" to app.insuranceExpiryDate,
                "preferredZone" to app.preferredZone,
                "status" to app.status.name,
                "emailVerified" to app.emailVerified,
                "assignedDriverId" to (app.assignedDriverId ?: ""),
                "commissionerNotes" to (app.commissionerNotes ?: ""),
                "currentLoadKg" to app.currentLoadKg,
                "currentLat" to app.currentLat,
                "currentLng" to app.currentLng,
                "rating" to app.rating,
                "completedTrips" to app.completedTrips,
                "appliedAt" to app.appliedAt,
                "approvedAt" to (app.approvedAt ?: 0L),
                "updatedAt" to System.currentTimeMillis()
            )
            fs.collection(COLLECTION_DRIVER_APPLICATIONS)
                .document(app.id)
                .set(appMap, SetOptions.merge())
                .await()
            Log.i(TAG, "Successfully stored driver application ${app.id} (${app.fullName}) in Firestore")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error saving driver application to Firestore: ${e.message}", e)
            false
        }
    }

    /**
     * Fetches driver application by identifier (driver ID, license, email, or phone) from Firestore.
     */
    suspend fun getDriverApplicationByIdentifier(identifier: String): com.example.data.local.DriverApplicationEntity? {
        val fs = firestore ?: return null
        val clean = identifier.trim()
        return try {
            val snapshot = fs.collection(COLLECTION_DRIVER_APPLICATIONS).get().await()
            val matchDoc = snapshot.documents.firstOrNull { doc ->
                val drvId = doc.getString("assignedDriverId") ?: ""
                val lic = doc.getString("licenseNumber") ?: ""
                val em = doc.getString("email") ?: ""
                val ph = (doc.getString("phone") ?: "").replace(" ", "")
                val cleanPh = clean.replace(" ", "")
                val id = doc.getString("id") ?: doc.id

                id.equals(clean, ignoreCase = true) ||
                drvId.equals(clean, ignoreCase = true) ||
                lic.equals(clean, ignoreCase = true) ||
                em.equals(clean, ignoreCase = true) ||
                (ph.isNotBlank() && (ph.contains(cleanPh) || cleanPh.contains(ph)))
            }
            matchDoc?.let { doc ->
                val statusStr = doc.getString("status") ?: com.example.data.model.DriverApplicationStatus.SUBMITTED.name
                val status = try { com.example.data.model.DriverApplicationStatus.valueOf(statusStr) } catch (_: Exception) { com.example.data.model.DriverApplicationStatus.SUBMITTED }
                com.example.data.local.DriverApplicationEntity(
                    id = doc.getString("id") ?: doc.id,
                    userId = doc.getString("userId") ?: "",
                    fullName = doc.getString("fullName") ?: "",
                    phone = doc.getString("phone") ?: "",
                    email = doc.getString("email") ?: "",
                    nidaMasked = doc.getString("nidaMasked") ?: "********0000",
                    licenseNumber = doc.getString("licenseNumber") ?: "",
                    licenseClass = doc.getString("licenseClass") ?: "",
                    licenseExpiryDate = doc.getString("licenseExpiryDate") ?: "",
                    vehicleRegNumber = doc.getString("vehicleRegNumber") ?: "",
                    vehicleType = doc.getString("vehicleType") ?: "",
                    vehicleCapacityKg = doc.getLong("vehicleCapacityKg")?.toInt() ?: 2500,
                    insuranceExpiryDate = doc.getString("insuranceExpiryDate") ?: "",
                    preferredZone = doc.getString("preferredZone") ?: "",
                    status = status,
                    emailVerified = doc.getBoolean("emailVerified") ?: false,
                    assignedDriverId = doc.getString("assignedDriverId")?.ifBlank { null },
                    commissionerNotes = doc.getString("commissionerNotes")?.ifBlank { null },
                    currentLoadKg = doc.getLong("currentLoadKg")?.toInt() ?: 0,
                    currentLat = doc.getDouble("currentLat") ?: -6.8235,
                    currentLng = doc.getDouble("currentLng") ?: 39.2695,
                    rating = doc.getDouble("rating") ?: 4.9,
                    completedTrips = doc.getLong("completedTrips")?.toInt() ?: 0,
                    appliedAt = doc.getLong("appliedAt") ?: System.currentTimeMillis(),
                    approvedAt = doc.getLong("approvedAt")
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching driver application from Firestore: ${e.message}", e)
            null
        }
    }

    private fun mapDocToUserEntity(docId: String, data: Map<String, Any>): UserEntity {
        val roleStr = (data["role"] as? String) ?: UserRole.RESIDENT.name
        val role = try {
            UserRole.valueOf(roleStr)
        } catch (_: Exception) {
            UserRole.RESIDENT
        }
        return UserEntity(
            id = (data["id"] as? String) ?: docId,
            fullName = (data["fullName"] as? String) ?: "",
            email = (data["email"] as? String) ?: "",
            phone = (data["phone"] as? String) ?: "",
            role = role,
            nidaMasked = (data["nidaMasked"] as? String) ?: "********0000",
            address = (data["address"] as? String) ?: "",
            ward = (data["ward"] as? String) ?: "",
            district = (data["district"] as? String) ?: "",
            licenseId = data["licenseId"] as? String,
            isActive = (data["isActive"] as? Boolean) ?: true,
            emailVerified = (data["emailVerified"] as? Boolean) ?: true,
            fcmToken = data["fcmToken"] as? String,
            createdAt = (data["createdAt"] as? Long) ?: System.currentTimeMillis()
        )
    }

    /**
     * Stores a waste collection report in Firestore.
     */
    suspend fun saveCollectionReport(report: CollectionRequestEntity): Boolean {
        val fs = firestore ?: run {
            Log.d(TAG, "Firestore offline: skipping remote save for report ${report.id}")
            return false
        }
        return try {
            val reportMap = hashMapOf(
                "id" to report.id,
                "trackingId" to report.trackingId,
                "collectionId" to (report.collectionId ?: ""),
                "residentId" to report.residentId,
                "residentName" to report.residentName,
                "residentPhone" to report.residentPhone,
                "wasteCategory" to report.wasteCategory.name,
                "quantityKg" to report.quantityKg,
                "bagCount" to report.bagCount,
                "description" to report.description,
                "pickupAddress" to report.pickupAddress,
                "ward" to report.ward,
                "district" to report.district,
                "latitude" to report.latitude,
                "longitude" to report.longitude,
                "preferredDate" to report.preferredDate,
                "preferredTimeSlot" to report.preferredTimeSlot,
                "urgency" to report.urgency.name,
                "estimatedPriceTzs" to report.estimatedPriceTzs,
                "paymentMethod" to (report.paymentMethod?.name ?: PaymentMethod.MPESA.name),
                "paymentStatus" to report.paymentStatus,
                "paymentRef" to (report.paymentRef ?: ""),
                "collectionStatus" to report.collectionStatus.name,
                "assignedDriverId" to (report.assignedDriverId ?: ""),
                "assignedDriverName" to (report.assignedDriverName ?: ""),
                "vehicleRegNumber" to (report.vehicleRegNumber ?: ""),
                "driverCurrentLat" to (report.driverCurrentLat ?: 0.0),
                "driverCurrentLng" to (report.driverCurrentLng ?: 0.0),
                "etaMinutes" to (report.etaMinutes ?: 0),
                "actualWeightKg" to (report.actualWeightKg ?: 0),
                "collectionProofPhoto" to (report.collectionProofPhoto ?: ""),
                "disposalFacilityId" to (report.disposalFacilityId ?: ""),
                "disposalFacilityName" to (report.disposalFacilityName ?: ""),
                "disposalTimestamp" to (report.disposalTimestamp ?: 0L),
                "isRecycled" to report.isRecycled,
                "collectionTimestamp" to (report.collectionTimestamp ?: 0L),
                "isRecurring" to report.isRecurring,
                "recurringInterval" to report.recurringInterval,
                "ratingScore" to (report.ratingScore ?: 0),
                "ratingFeedback" to (report.ratingFeedback ?: ""),
                "createdAt" to report.createdAt,
                "updatedAt" to System.currentTimeMillis()
            )

            fs.collection(COLLECTION_REPORTS)
                .document(report.id)
                .set(reportMap, SetOptions.merge())
                .await()

            Log.i(TAG, "Successfully stored waste collection report ${report.trackingId} in Firestore")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error saving collection report to Firestore: ${e.message}", e)
            false
        }
    }

    /**
     * Stores overflowing bin hazard complaint in Firestore.
     */
    suspend fun saveOverflowBinReport(complaint: ComplaintEntity): Boolean {
        val fs = firestore ?: return false
        return try {
            val complaintMap = hashMapOf(
                "id" to complaint.id,
                "residentId" to complaint.residentId,
                "residentName" to complaint.residentName,
                "requestId" to complaint.requestId,
                "driverId" to (complaint.driverId ?: ""),
                "driverName" to (complaint.driverName ?: ""),
                "category" to complaint.category,
                "description" to complaint.description,
                "status" to complaint.status,
                "officerNotes" to (complaint.officerNotes ?: ""),
                "createdAt" to complaint.createdAt,
                "updatedAt" to System.currentTimeMillis()
            )
            fs.collection(COLLECTION_OVERFLOW_BINS)
                .document(complaint.id)
                .set(complaintMap, SetOptions.merge())
                .await()
            Log.i(TAG, "Successfully stored overflowing bin incident ${complaint.id} in Firestore")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error saving overflow bin report to Firestore: ${e.message}", e)
            false
        }
    }

    /**
     * Fetches all collection reports from Firestore.
     */
    suspend fun fetchCollectionReports(): List<CollectionRequestEntity> {
        val fs = firestore ?: return emptyList()
        return try {
            val snapshot = fs.collection(COLLECTION_REPORTS).get().await()
            snapshot.documents.mapNotNull { doc ->
                try {
                    val categoryStr = doc.getString("wasteCategory") ?: WasteCategory.GENERAL_HOUSEHOLD.name
                    val category = try { WasteCategory.valueOf(categoryStr) } catch (_: Exception) { WasteCategory.GENERAL_HOUSEHOLD }
                    val urgencyStr = doc.getString("urgency") ?: RequestUrgency.NORMAL.name
                    val urgency = try { RequestUrgency.valueOf(urgencyStr) } catch (_: Exception) { RequestUrgency.NORMAL }
                    val payMethodStr = doc.getString("paymentMethod")
                    val payMethod = payMethodStr?.let {
                        try { PaymentMethod.valueOf(it) } catch (_: Exception) { PaymentMethod.MPESA }
                    } ?: PaymentMethod.MPESA
                    val statusStr = doc.getString("collectionStatus") ?: CollectionStatus.WAITING_ASSIGNMENT.name
                    val status = try { CollectionStatus.valueOf(statusStr) } catch (_: Exception) { CollectionStatus.WAITING_ASSIGNMENT }

                    CollectionRequestEntity(
                        id = doc.getString("id") ?: doc.id,
                        trackingId = doc.getString("trackingId") ?: doc.id,
                        collectionId = doc.getString("collectionId")?.ifBlank { null },
                        residentId = doc.getString("residentId") ?: "",
                        residentName = doc.getString("residentName") ?: "",
                        residentPhone = doc.getString("residentPhone") ?: "",
                        wasteCategory = category,
                        quantityKg = doc.getLong("quantityKg")?.toInt() ?: 10,
                        bagCount = doc.getLong("bagCount")?.toInt() ?: 1,
                        description = doc.getString("description") ?: "",
                        pickupAddress = doc.getString("pickupAddress") ?: "",
                        ward = doc.getString("ward") ?: "",
                        district = doc.getString("district") ?: "",
                        latitude = doc.getDouble("latitude") ?: -6.8650,
                        longitude = doc.getDouble("longitude") ?: 39.3100,
                        preferredDate = doc.getString("preferredDate") ?: "Today",
                        preferredTimeSlot = doc.getString("preferredTimeSlot") ?: "08:00 AM - 11:00 AM",
                        urgency = urgency,
                        estimatedPriceTzs = doc.getLong("estimatedPriceTzs")?.toInt() ?: 10000,
                        paymentMethod = payMethod,
                        paymentStatus = doc.getString("paymentStatus") ?: "PAID",
                        paymentRef = doc.getString("paymentRef") ?: "PAY-REF",
                        collectionStatus = status,
                        assignedDriverId = doc.getString("assignedDriverId")?.ifBlank { null },
                        assignedDriverName = doc.getString("assignedDriverName")?.ifBlank { null },
                        vehicleRegNumber = doc.getString("vehicleRegNumber")?.ifBlank { null },
                        driverCurrentLat = doc.getDouble("driverCurrentLat"),
                        driverCurrentLng = doc.getDouble("driverCurrentLng"),
                        etaMinutes = doc.getLong("etaMinutes")?.toInt(),
                        actualWeightKg = doc.getLong("actualWeightKg")?.toInt(),
                        collectionProofPhoto = doc.getString("collectionProofPhoto")?.ifBlank { null },
                        disposalFacilityId = doc.getString("disposalFacilityId")?.ifBlank { null },
                        disposalFacilityName = doc.getString("disposalFacilityName")?.ifBlank { null },
                        disposalTimestamp = doc.getLong("disposalTimestamp"),
                        isRecycled = doc.getBoolean("isRecycled") ?: false,
                        collectionTimestamp = doc.getLong("collectionTimestamp"),
                        isRecurring = doc.getBoolean("isRecurring") ?: false,
                        recurringInterval = doc.getString("recurringInterval") ?: "NONE",
                        ratingScore = doc.getLong("ratingScore")?.toInt(),
                        ratingFeedback = doc.getString("ratingFeedback"),
                        createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                    )
                } catch (e: Exception) {
                    Log.w(TAG, "Error parsing doc ${doc.id}: ${e.message}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching collection reports from Firestore: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * Stores an OTP verification record in Firestore (COLLECTION_OTP_VERIFICATIONS).
     */
    suspend fun saveOtpVerification(
        email: String,
        code: String,
        expiresAt: Long,
        purpose: String = "RESIDENT_REGISTRATION"
    ): Boolean {
        val fs = firestore ?: return false
        val cleanEmail = email.trim().lowercase()
        return try {
            val data = hashMapOf(
                "email" to cleanEmail,
                "code" to code,
                "expiresAt" to expiresAt,
                "createdAt" to System.currentTimeMillis(),
                "verified" to false,
                "purpose" to purpose
            )
            fs.collection(COLLECTION_OTP_VERIFICATIONS)
                .document(cleanEmail)
                .set(data, SetOptions.merge())
                .await()
            Log.i(TAG, "Saved OTP verification for $cleanEmail (purpose: $purpose)")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error saving OTP verification to Firestore: ${e.message}", e)
            false
        }
    }

    /**
     * Retrieves OTP verification record for an email.
     */
    suspend fun getOtpVerification(email: String): Map<String, Any>? {
        val fs = firestore ?: return null
        val cleanEmail = email.trim().lowercase()
        return try {
            val doc = fs.collection(COLLECTION_OTP_VERIFICATIONS).document(cleanEmail).get().await()
            if (doc.exists()) doc.data else null
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving OTP verification for $cleanEmail: ${e.message}", e)
            null
        }
    }

    /**
     * Marks an OTP record as verified in Firestore.
     */
    suspend fun markOtpVerified(email: String): Boolean {
        val fs = firestore ?: return false
        val cleanEmail = email.trim().lowercase()
        return try {
            fs.collection(COLLECTION_OTP_VERIFICATIONS)
                .document(cleanEmail)
                .update("verified", true, "verifiedAt", System.currentTimeMillis())
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error marking OTP as verified for $cleanEmail: ${e.message}", e)
            false
        }
    }

    private val emailDispatcher = RealtimeEmailDispatcher(firestore)

    /**
     * Dispatches an OTP verification email in real-time across multiple channels.
     */
    suspend fun sendOtpEmailRealtime(
        toEmail: String,
        recipientName: String,
        code: String,
        expiresMinutes: Int = 10,
        purpose: String = "RESIDENT_REGISTRATION"
    ): Boolean {
        return emailDispatcher.dispatchOtpEmail(
            toEmail = toEmail,
            recipientName = recipientName,
            otpCode = code,
            expiresMinutes = expiresMinutes,
            purpose = purpose
        )
    }

    /**
     * Enqueues an email dispatch in Firestore (for Firebase Extensions "Trigger Email" / Cloud Functions).
     */
    suspend fun sendTriggerEmail(
        toEmail: String,
        subject: String,
        textBody: String,
        htmlBody: String? = null
    ): Boolean {
        val fs = firestore ?: return false
        val cleanEmail = toEmail.trim().lowercase()
        return try {
            val mailDoc = hashMapOf(
                "to" to listOf(cleanEmail),
                "message" to hashMapOf(
                    "subject" to subject,
                    "text" to textBody,
                    "html" to (htmlBody ?: "<p>$textBody</p>")
                ),
                "createdAt" to System.currentTimeMillis()
            )
            fs.collection(COLLECTION_MAIL).add(mailDoc).await()
            Log.i(TAG, "Trigger email successfully queued in Firestore for $cleanEmail")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error queueing trigger email in Firestore: ${e.message}", e)
            false
        }
    }

    /**
     * Stores or updates the resident's FCM token in Firestore (both users and fcm_tokens collection).
     */
    suspend fun saveFcmToken(userId: String, token: String, role: String = "RESIDENT"): Boolean {
        val fs = firestore ?: run {
            Log.d(TAG, "Firestore offline: skipping remote FCM token save for $userId")
            return false
        }
        return try {
            val tokenData = hashMapOf(
                "userId" to userId,
                "fcmToken" to token,
                "role" to role,
                "platform" to "ANDROID",
                "updatedAt" to System.currentTimeMillis()
            )
            fs.collection(COLLECTION_FCM_TOKENS)
                .document(userId)
                .set(tokenData, SetOptions.merge())
                .await()

            // Also update in users document
            fs.collection(COLLECTION_USERS)
                .document(userId)
                .set(mapOf("fcmToken" to token, "fcmUpdatedAt" to System.currentTimeMillis()), SetOptions.merge())
                .await()

            Log.i(TAG, "Successfully registered FCM token in Firestore for user $userId")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error saving FCM token to Firestore for $userId: ${e.message}", e)
            false
        }
    }

    /**
     * Retrieves an FCM token for a user from Firestore.
     */
    suspend fun getFcmTokenForUser(userId: String): String? {
        val fs = firestore ?: return null
        return try {
            val doc = fs.collection(COLLECTION_FCM_TOKENS).document(userId).get().await()
            if (doc.exists()) {
                doc.getString("fcmToken")
            } else {
                val userDoc = fs.collection(COLLECTION_USERS).document(userId).get().await()
                userDoc.getString("fcmToken")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Could not fetch FCM token for $userId from Firestore: ${e.message}")
            null
        }
    }

    /**
     * Records an FCM notification dispatch in Firestore (COLLECTION_FCM_NOTIFICATIONS).
     * This acts as the serverless queue for Cloud Functions to dispatch FCM pushes to physical devices.
     */
    suspend fun saveFcmNotificationDispatch(notificationPayload: Map<String, Any>): Boolean {
        val fs = firestore ?: run {
            Log.d(TAG, "Firestore offline: skipping FCM notification queue save")
            return false
        }
        return try {
            val docId = "fcm_notif_${System.currentTimeMillis()}_${(notificationPayload["requestId"] as? String)?.takeLast(4) ?: "gen"}"
            fs.collection(COLLECTION_FCM_NOTIFICATIONS)
                .document(docId)
                .set(notificationPayload, SetOptions.merge())
                .await()
            Log.i(TAG, "Queued FCM status update in Firestore: $docId (Recipient: ${notificationPayload["recipientId"]})")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error saving FCM notification queue in Firestore: ${e.message}", e)
            false
        }
    }
}

