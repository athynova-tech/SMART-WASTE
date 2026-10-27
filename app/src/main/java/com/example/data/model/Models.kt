package com.example.data.model

enum class UserRole(val displayName: String, val badgeColorHex: Long) {
    RESIDENT("Resident", 0xFF059669),
    BUSINESS("Commercial Business", 0xFF0284C7),
    DRIVER("Truck Driver", 0xFF2563EB),
    MUNICIPAL_COMMISSIONER("Municipal Commissioner", 0xFF7C3AED),
    DISPOSAL_OFFICER("Disposal Officer", 0xFF0D9488)
}

enum class BusinessVerificationStatus(val label: String, val colorHex: Long) {
    PENDING("Pending Submission", 0xFF64748B),
    UNDER_REVIEW("Under Municipal Review", 0xFF3B82F6),
    APPROVED("Approved & Active", 0xFF10B981),
    REJECTED("Rejected", 0xFFEF4444),
    SUSPENDED("Suspended", 0xFFB91C1C)
}

enum class AssignmentMethod {
    MANUAL,
    SMART_RECOMMENDATION,
    AUTOMATIC
}

data class GeoLocation(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float = 0f,
    val timestamp: Long = System.currentTimeMillis(),
    val address: String = ""
)

enum class WasteCategory(
    val title: String,
    val iconName: String,
    val basePricePerBag: Int, // TZS per standard bag/container
    val basePricePerKg: Int, // TZS (legacy / fleet capacity)
    val isRecyclable: Boolean,
    val description: String
) {
    GENERAL_HOUSEHOLD("Household Waste", "home", 2500, 300, false, "Kitchen waste, sweepings, mixed domestic refuse"),
    ORGANIC("Organic / Food Waste", "eco", 2000, 200, true, "Food leftovers, green garden trimmings, compostable items"),
    PLASTIC("Plastic & Polymers", "recycling", 2000, 250, true, "Bottles, containers, wrapping films, packaging"),
    GLASS("Glass & Bottles", "wine_bar", 2500, 350, true, "Glass bottles, broken tableware, jars (handled carefully)"),
    METAL("Metals & Scraps", "hardware", 3000, 400, true, "Cans, metal sheets, wires, old metallic equipment"),
    PAPER("Paper & Cardboard", "description", 1800, 180, true, "Boxes, newspapers, office papers, packaging cartons"),
    E_WASTE("Electronic Waste (E-Waste)", "devices", 4500, 800, true, "Old electronics, batteries, phones, broken appliances"),
    HAZARDOUS("Hazardous / Chemical", "warning", 5000, 1200, false, "Paints, chemicals, medical waste, safety gear required"),
    CONSTRUCTION("Construction Debris", "construction", 4000, 450, false, "Concrete, bricks, rubble, soil, timber fragments"),
    COMMERCIAL("Commercial / Event Waste", "storefront", 3500, 350, true, "Bulk restaurant, market, shop, hotel, or festival waste")
}

enum class RequestUrgency(val title: String, val priceMultiplier: Double, val badgeHex: Long) {
    NORMAL("Normal (Scheduled)", 1.0, 0xFF10B981),
    PRIORITY("Priority (Within 4 hrs)", 1.4, 0xFFF59E0B),
    EMERGENCY("Emergency (Immediate)", 2.0, 0xFFEF4444)
}

enum class CollectionStatus(val label: String, val progress: Float, val colorHex: Long) {
    REQUESTED("Requested", 0.1f, 0xFF6B7280),
    PAYMENT_PENDING("Payment Pending", 0.2f, 0xFFF59E0B),
    PAID("Payment Confirmed", 0.3f, 0xFF10B981),
    WAITING_ASSIGNMENT("Awaiting Municipal Dispatch", 0.4f, 0xFF8B5CF6),
    DRIVER_ASSIGNED("Driver Assigned", 0.5f, 0xFF3B82F6),
    ON_THE_WAY("Truck On Route", 0.65f, 0xFF0284C7),
    ARRIVED("Driver Arrived", 0.75f, 0xFF059669),
    COLLECTING("Collecting Waste", 0.85f, 0xFF10B981),
    COLLECTED("Collected & Proof Signed", 0.9f, 0xFF059669),
    DISPOSAL_PENDING("En Route to Disposal Hub", 0.95f, 0xFF0D9488),
    DISPOSED("Disposed & Recycled", 1.0f, 0xFF047857),
    CANCELLED("Cancelled", 0.0f, 0xFFDC2626)
}

enum class DriverApplicationStatus(val label: String, val colorHex: Long) {
    DRAFT("Draft", 0xFF64748B),
    SUBMITTED("Submitted", 0xFFF59E0B),
    UNDER_REVIEW("Under Review", 0xFF3B82F6),
    DOCUMENT_VERIFIED("Verified by Officer", 0xFF0284C7),
    APPROVED("Approved & Activated", 0xFF10B981),
    REJECTED("Rejected", 0xFFEF4444),
    SUSPENDED("Suspended", 0xFFB91C1C)
}

enum class PaymentMethod(val title: String, val feeTzs: Int) {
    MPESA("Vodacom M-Pesa", 0),
    TIGO_PESA("Mix by Yas / Tigo Pesa", 0),
    AIRTEL_MONEY("Airtel Money", 0),
    HALOPESA("HaloPesa", 0),
    CARD("Visa / MasterCard", 200),
    MUNICIPAL_WALLET("Municipal Resident Wallet", 0)
}

enum class CustomerType(val label: String, val isCommercial: Boolean) {
    RESIDENTIAL("Private Resident / Household", false),
    COMMERCIAL_RESTAURANT("Restaurant / Eatery", true),
    COMMERCIAL_HOTEL("Hotel / Lodge", true),
    COMMERCIAL_CAFE("Cafe & Coffee Shop", true),
    COMMERCIAL_HOSPITAL("Hospital / Medical Center", true),
    COMMERCIAL_SCHOOL("School / Educational Institution", true),
    COMMERCIAL_EVENT("Event / Festival Venue", true),
    COMMERCIAL_MARKET("Supermarket / Retail Shop", true),
    COMMERCIAL_OFFICE("Corporate Office & Firm", true),
    COMMERCIAL_WAREHOUSE("Warehouse & Light Manufacturing", true)
}

enum class BusinessType(
    val title: String,
    val categoryLabel: String,
    val iconName: String,
    val defaultBins: Int,
    val defaultWasteStream: String,
    val baseMonthlyRateTzs: Int
) {
    RESTAURANT("Restaurant & Eatery", "Food & Dining", "restaurant", 3, "Organic Kitchen Refuse & Cooking Oil", 180000),
    CAFE("Cafe & Bakery", "Beverage & Pastry", "local_cafe", 2, "Coffee Grounds, Dairy & Pastry Waste", 120000),
    HOTEL("Hotel, Lodge & Resort", "Hospitality & Tourism", "hotel", 6, "Mixed Guest Refuse, Bedding & Food Waste", 450000),
    RETAIL_SHOP("Shop & Supermarket", "Retail & Groceries", "storefront", 4, "Packaging Bales, Cartons & Plastic Wrap", 220000),
    OFFICE_CORPORATE("Corporate Office & Firm", "Commercial Office", "corporate_fare", 2, "Paper, Cartons, E-Waste & General Refuse", 150000),
    HOSPITAL_CLINIC("Hospital, Clinic & Pharmacy", "Healthcare & Medical", "local_hospital", 4, "Sanitary, Packaging & Regulated Medical Waste", 380000),
    EVENT_VENUE("Event, Conference & Banquet Venue", "Entertainment & Events", "festival", 5, "Bulk Beverage Bottles, Plastic & Food Waste", 280000),
    WAREHOUSE_LIGHT_INDUSTRY("Warehouse & Light Industry", "Logistics & Storage", "warehouse", 6, "Pallets, Strapping, Film & Industrial Scrap", 350000)
}

enum class ScheduleFrequency(val title: String, val description: String, val multiplier: Double) {
    DAILY("Daily (7 Days/Week)", "Morning & Evening Compactor Pickup", 2.2),
    MON_WED_FRI("3x Weekly (Mon / Wed / Fri)", "Standard Commercial Routine", 1.4),
    TUE_THU_SAT("3x Weekly (Tue / Thu / Sat)", "Alternate Days Routine", 1.4),
    WEEKLY("Weekly (Once a week)", "Light Commercial or Weekend Pickup", 1.0),
    BIWEEKLY("Bi-Weekly (Twice a month)", "Low volume / dry recyclables only", 0.6),
    MONTHLY("Monthly Contract", "Bulk container exchange", 0.35)
}

data class BusinessLocationInfo(
    val businessName: String,
    val businessType: BusinessType,
    val tinNumber: String,
    val contactPerson: String,
    val contactPhone: String,
    val contactEmail: String,
    val operatingHours: String,
    val streetAddress: String,
    val ward: String,
    val landmark: String,
    val latitude: Double,
    val longitude: Double,
    val binCount: Int = 3,
    val binType: String = "240L Heavy Wheelie Bin"
)

data class ScheduledCollectionPlan(
    val id: String,
    val businessId: String,
    val businessName: String,
    val businessType: BusinessType,
    val frequency: ScheduleFrequency,
    val selectedDays: List<String>,
    val timeSlot: String,
    val wasteCategory: WasteCategory,
    val binCount: Int,
    val containerType: String,
    val pickupAddress: String,
    val ward: String,
    val landmark: String,
    val latitude: Double,
    val longitude: Double,
    val monthlyRateTzs: Int,
    val isActive: Boolean = true,
    val nextPickupDate: String,
    val startDate: String,
    val contractRef: String
)

enum class AlertType {
    DRIVER_NEARBY,
    PICKUP_COMPLETED,
    DISPATCH_ASSIGNED
}

data class ResidentAlert(
    val id: String,
    val title: String,
    val message: String,
    val type: AlertType,
    val requestId: String,
    val driverName: String? = null,
    val vehicleReg: String? = null,
    val etaMinutes: Int? = null,
    val actualWeightKg: Int? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class InAppNotification(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: Long,
    val type: String, // DRIVER_NEARBY, PICKUP_COMPLETED, DISPATCH, PAYMENT, RECYCLING, COMPLIANCE, SYSTEM
    val requestId: String? = null,
    val isRead: Boolean = false,
    val actionType: String? = null
)

data class SavedAddress(
    val id: String,
    val label: String,
    val address: String,
    val ward: String,
    val district: String,
    val latitude: Double,
    val longitude: Double
)

sealed class AuthLoginResult {
    data class Success(
        val user: com.example.data.local.UserEntity,
        val verifiedRole: UserRole,
        val driver: com.example.data.local.DriverApplicationEntity? = null,
        val verificationSource: String = "Cloud Firestore"
    ) : AuthLoginResult()

    data class DriverPendingApproval(
        val status: DriverApplicationStatus,
        val fullName: String,
        val message: String
    ) : AuthLoginResult()

    data class DriverSuspended(
        val reason: String
    ) : AuthLoginResult()

    data class BusinessPendingApproval(
        val status: BusinessVerificationStatus,
        val companyName: String,
        val message: String
    ) : AuthLoginResult()

    data class BusinessSuspended(
        val reason: String
    ) : AuthLoginResult()

    data class UnauthorizedRole(
        val actualRole: UserRole,
        val requestedRole: UserRole,
        val message: String
    ) : AuthLoginResult()

    data class NotFound(
        val identifier: String
    ) : AuthLoginResult()

    data class Error(
        val message: String
    ) : AuthLoginResult()
}

sealed class DriverLoginResult {
    data class Success(val driver: com.example.data.local.DriverApplicationEntity, val user: com.example.data.local.UserEntity) : DriverLoginResult()
    data class PendingApproval(val status: DriverApplicationStatus, val fullName: String) : DriverLoginResult()
    data class Suspended(val reason: String) : DriverLoginResult()
    object NotFound : DriverLoginResult()
}

data class MatchedDriverRecommendation(
    val driverId: String,
    val driverName: String,
    val phone: String,
    val vehicleRegNumber: String,
    val vehicleType: String,
    val capacityKg: Int,
    val currentLoadKg: Int,
    val remainingCapacityKg: Int,
    val distanceKm: Double,
    val etaMinutes: Int,
    val matchScorePercent: Int,
    val currentZone: String,
    val ratingScore: Double,
    val recommendationReasons: List<String>
)

sealed class OtpSendResult {
    data class OtpSent(val email: String, val expiresAt: Long, val otpCode: String = "") : OtpSendResult()
    data class DirectlyRegistered(val user: com.example.data.local.UserEntity) : OtpSendResult()
    data class Error(val message: String) : OtpSendResult()
}

sealed class OtpVerifyResult {
    data class Success(val user: com.example.data.local.UserEntity) : OtpVerifyResult()
    data class DriverSuccess(val driver: com.example.data.local.DriverApplicationEntity) : OtpVerifyResult()
    data class Expired(val message: String = "Verification code has expired. Please request a new code.") : OtpVerifyResult()
    data class InvalidCode(val message: String = "Incorrect verification code. Please check and try again.") : OtpVerifyResult()
    data class Error(val message: String) : OtpVerifyResult()
}

