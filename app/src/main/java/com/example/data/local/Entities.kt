package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.AssignmentMethod
import com.example.data.model.BusinessVerificationStatus
import com.example.data.model.CollectionStatus
import com.example.data.model.DriverApplicationStatus
import com.example.data.model.PaymentMethod
import com.example.data.model.RequestUrgency
import com.example.data.model.UserRole
import com.example.data.model.WasteCategory

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val role: UserRole,
    val nidaMasked: String = "********0000",
    val licenseId: String? = null,
    val address: String = "Kigamboni Ward, Dar es Salaam",
    val ward: String = "Kigamboni",
    val district: String = "Kigamboni District",
    val isActive: Boolean = true,
    val emailVerified: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    // Commercial / Business Entity fields
    val isBusinessAccount: Boolean = false,
    val businessName: String? = null,
    val businessType: String? = null,
    val businessTin: String? = null,
    val businessContactPerson: String? = null,
    val businessContactPhone: String? = null,
    val businessContactEmail: String? = null,
    val businessOperatingHours: String? = null,
    val businessBranchName: String? = null,
    val businessLoadingDockNotes: String? = null,
    val businessBinCount: Int = 2,
    val businessBinType: String = "240L Heavy Wheelie Bin",
    val businessLatitude: Double = -6.8280,
    val businessLongitude: Double = 39.3020,
    val fcmToken: String? = null
)

@Entity(tableName = "businesses")
data class BusinessEntity(
    @PrimaryKey val businessId: String,
    val userId: String,
    val companyName: String,
    val businessType: String,
    val tin: String,
    val registrationNumber: String,
    val businessLicenseNumber: String,
    val email: String,
    val phone: String,
    val physicalAddress: String,
    val ward: String = "Kigamboni",
    val district: String = "Kigamboni District",
    val latitude: Double = -6.8280,
    val longitude: Double = 39.3020,
    val verificationStatus: BusinessVerificationStatus = BusinessVerificationStatus.PENDING,
    val accountStatus: String = "ACTIVE",
    val contactPerson: String = "",
    val operatingHours: String = "08:00 - 18:00",
    val binCount: Int = 3,
    val binType: String = "240L Heavy Wheelie Bin",
    val loadingDockNotes: String? = null,
    val contractFrequency: String = "WEEKLY",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "vehicles")
data class VehicleEntity(
    @PrimaryKey val vehicleId: String,
    val registrationNumber: String,
    val vehicleType: String,
    val capacityKg: Int,
    val status: String = "ACTIVE", // ACTIVE, MAINTENANCE, OFF_DUTY
    val assignedDriverId: String? = null,
    val currentLatitude: Double = -6.8235,
    val currentLongitude: Double = 39.2695,
    val fuelType: String = "DIESEL",
    val modelYear: Int = 2024
)

@Entity(tableName = "collection_assignments")
data class CollectionAssignmentEntity(
    @PrimaryKey val assignmentId: String,
    val requestId: String,
    val driverId: String,
    val vehicleId: String? = null,
    val assignedBy: String,
    val assignedAt: Long = System.currentTimeMillis(),
    val acceptedAt: Long? = null,
    val completedAt: Long? = null,
    val status: String = "ASSIGNED", // ASSIGNED, ACCEPTED, IN_PROGRESS, COMPLETED, REASSIGNED, CANCELLED
    val assignmentMethod: AssignmentMethod = AssignmentMethod.MANUAL
)

@Entity(tableName = "disposal_records")
data class DisposalRecordEntity(
    @PrimaryKey val recordId: String,
    val facilityId: String,
    val facilityName: String,
    val driverId: String,
    val driverName: String,
    val vehicleRegNumber: String,
    val requestId: String? = null,
    val grossWeightKg: Int,
    val tareWeightKg: Int,
    val netWasteWeightKg: Int,
    val recycledKg: Int,
    val disposedKg: Int,
    val wasteCategory: WasteCategory,
    val certificateRef: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "driver_applications")
data class DriverApplicationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val fullName: String,
    val phone: String,
    val email: String,
    val nidaMasked: String,
    val licenseNumber: String,
    val licenseClass: String,
    val licenseExpiryDate: String,
    val vehicleRegNumber: String,
    val vehicleType: String,
    val vehicleCapacityKg: Int,
    val insuranceExpiryDate: String,
    val preferredZone: String,
    val status: DriverApplicationStatus = DriverApplicationStatus.SUBMITTED,
    val assignedDriverId: String? = null,
    val commissionerNotes: String? = null,
    val emailVerified: Boolean = false,
    val currentLoadKg: Int = 0,
    val currentLat: Double = -6.8235,
    val currentLng: Double = 39.2695,
    val rating: Double = 4.9,
    val completedTrips: Int = 0,
    val appliedAt: Long = System.currentTimeMillis(),
    val approvedAt: Long? = null
)

@Entity(tableName = "collection_requests")
data class CollectionRequestEntity(
    @PrimaryKey val id: String,
    val trackingId: String, // e.g. WST-2026-000582
    val collectionId: String? = null, // e.g. COL-2026-000582
    val residentId: String,
    val residentName: String,
    val residentPhone: String,
    val wasteCategory: WasteCategory,
    val quantityKg: Int,
    val bagCount: Int,
    val description: String,
    val pickupAddress: String,
    val ward: String,
    val district: String,
    val latitude: Double,
    val longitude: Double,
    val preferredDate: String,
    val preferredTimeSlot: String,
    val urgency: RequestUrgency,
    val estimatedPriceTzs: Int,
    val paymentMethod: PaymentMethod? = null,
    val paymentStatus: String = "PENDING", // PENDING, PAID, REFUNDED
    val paymentRef: String? = null,
    val collectionStatus: CollectionStatus = CollectionStatus.REQUESTED,
    val assignedDriverId: String? = null,
    val assignedDriverName: String? = null,
    val vehicleRegNumber: String? = null,
    val collectionProofPhoto: String? = null,
    val actualWeightKg: Int? = null,
    val collectionTimestamp: Long? = null,
    val disposalFacilityId: String? = null,
    val disposalFacilityName: String? = null,
    val disposalTimestamp: Long? = null,
    val isRecycled: Boolean = false,
    val ratingScore: Int? = null,
    val ratingFeedback: String? = null,
    val isRecurring: Boolean = false,
    val recurringInterval: String = "NONE", // WEEKLY, BIWEEKLY, MONTHLY, DAILY, MON_WED_FRI
    val createdAt: Long = System.currentTimeMillis(),
    val driverCurrentLat: Double? = null,
    val driverCurrentLng: Double? = null,
    val etaMinutes: Int? = null,
    // Business / Commercial fields
    val isCommercialRequest: Boolean = false,
    val businessName: String? = null,
    val businessType: String? = null,
    val containerType: String? = null,
    val landmarkOrDock: String? = null,
    val scheduledDaysText: String? = null
)

@Entity(tableName = "scheduled_collection_plans")
data class ScheduledCollectionPlanEntity(
    @PrimaryKey val id: String,
    val businessId: String,
    val businessName: String,
    val businessType: String,
    val frequency: String, // DAILY, MON_WED_FRI, TUE_THU_SAT, WEEKLY, BIWEEKLY, MONTHLY
    val selectedDays: String, // "Monday, Wednesday, Friday"
    val timeSlot: String, // "Early Morning (05:30 - 07:30)"
    val wasteCategory: WasteCategory,
    val binCount: Int,
    val containerType: String, // "240L Heavy Wheelie Bin", "1100L Dumpster"
    val pickupAddress: String,
    val ward: String,
    val landmark: String,
    val latitude: Double,
    val longitude: Double,
    val monthlyRateTzs: Int,
    val isActive: Boolean = true,
    val nextPickupDate: String,
    val contractRef: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "complaints")
data class ComplaintEntity(
    @PrimaryKey val id: String,
    val residentId: String,
    val residentName: String,
    val requestId: String,
    val driverId: String?,
    val driverName: String?,
    val category: String,
    val description: String,
    val status: String = "SUBMITTED", // SUBMITTED, UNDER_INVESTIGATION, RESOLVED
    val officerNotes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey val id: String,
    val action: String,
    val actorName: String,
    val actorRole: String,
    val details: String,
    val entityType: String,
    val entityId: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "disposal_facilities")
data class DisposalFacilityEntity(
    @PrimaryKey val id: String,
    val name: String,
    val zone: String,
    val latitude: Double,
    val longitude: Double,
    val totalCapacityTons: Double,
    val currentReceivedKg: Int,
    val totalRecycledKg: Int,
    val contactPhone: String
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val requestId: String,
    val senderId: String,
    val senderName: String,
    val senderRole: UserRole,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isQuickAction: Boolean = false,
    val isRead: Boolean = false
)

@Entity(tableName = "pending_registrations")
data class PendingRegistrationEntity(
    @PrimaryKey val email: String,
    val fullName: String,
    val phone: String,
    val password: String = "",
    val nidaMasked: String = "********0000",
    val address: String = "Kigamboni",
    val ward: String = "Kigamboni",
    val district: String = "Kigamboni District",
    val isCommercial: Boolean = false,
    val businessName: String = "",
    val otpCode: String,
    val expiresAt: Long,
    val createdAt: Long = System.currentTimeMillis(),
    val isDriverRegistration: Boolean = false,
    val driverApplicationId: String? = null
)

@Entity(tableName = "user_sessions")
data class UserSessionEntity(
    @PrimaryKey val userId: String,
    val email: String,
    val fullName: String,
    val role: UserRole,
    val driverId: String? = null,
    val driverStatus: DriverApplicationStatus? = null,
    val isEmailVerified: Boolean = true,
    val lastActiveTimestamp: Long = System.currentTimeMillis()
)

