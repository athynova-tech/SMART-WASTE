package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.AuditLogEntity
import com.example.data.local.ChatMessageEntity
import com.example.data.local.CollectionRequestEntity
import com.example.data.local.ComplaintEntity
import com.example.data.local.DisposalFacilityEntity
import com.example.data.local.DriverApplicationEntity
import com.example.data.local.ScheduledCollectionPlanEntity
import com.example.data.local.UserEntity
import com.example.data.location.GPSLocationHelper
import com.example.data.location.HighPrecisionLocation
import com.example.data.model.AlertType
import com.example.data.model.BusinessType
import com.example.data.model.CollectionStatus
import com.example.data.model.DriverApplicationStatus
import com.example.data.model.InAppNotification
import com.example.data.model.MatchedDriverRecommendation
import com.example.data.model.PaymentMethod
import com.example.data.model.RequestUrgency
import com.example.data.model.ResidentAlert
import com.example.data.model.ScheduleFrequency
import com.example.data.model.UserRole
import com.example.data.model.WasteCategory
import com.example.data.repository.WasteRepository
import com.example.ui.theme.AppThemePalette
import com.example.ui.theme.FontSizeScale
import com.example.ui.theme.ThemeMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WasteViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val repository = WasteRepository(db, context = application)

    val allRequests: StateFlow<List<CollectionRequestEntity>> = repository.allRequests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allApplications: StateFlow<List<DriverApplicationEntity>> = repository.allApplications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val approvedDrivers: StateFlow<List<DriverApplicationEntity>> = repository.approvedDrivers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allComplaints: StateFlow<List<ComplaintEntity>> = repository.allComplaints
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAuditLogs: StateFlow<List<AuditLogEntity>> = repository.allAuditLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFacilities: StateFlow<List<DisposalFacilityEntity>> = repository.allFacilities
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers: StateFlow<List<UserEntity>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBusinesses: StateFlow<List<com.example.data.local.BusinessEntity>> = repository.allBusinesses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allVehicles: StateFlow<List<com.example.data.local.VehicleEntity>> = repository.allVehicles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAssignments: StateFlow<List<com.example.data.local.CollectionAssignmentEntity>> = repository.allAssignments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDisposalRecords: StateFlow<List<com.example.data.local.DisposalRecordEntity>> = repository.allDisposalRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allScheduledPlans: StateFlow<List<ScheduledCollectionPlanEntity>> = repository.allScheduledPlans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // OTP Registration Flow States
    private val _pendingRegistrationEmail = MutableStateFlow<String?>(null)
    val pendingRegistrationEmail: StateFlow<String?> = _pendingRegistrationEmail.asStateFlow()

    private val _latestDispatchedOtpCode = MutableStateFlow<String?>(null)
    val latestDispatchedOtpCode: StateFlow<String?> = _latestDispatchedOtpCode.asStateFlow()

    private val _otpExpiresAt = MutableStateFlow<Long?>(null)
    val otpExpiresAt: StateFlow<Long?> = _otpExpiresAt.asStateFlow()

    private val _otpError = MutableStateFlow<String?>(null)
    val otpError: StateFlow<String?> = _otpError.asStateFlow()

    private val _isVerifyingOtp = MutableStateFlow(false)
    val isVerifyingOtp: StateFlow<Boolean> = _isVerifyingOtp.asStateFlow()

    private val _isResendingOtp = MutableStateFlow(false)
    val isResendingOtp: StateFlow<Boolean> = _isResendingOtp.asStateFlow()

    private val _showOtpScreen = MutableStateFlow(false)
    val showOtpScreen: StateFlow<Boolean> = _showOtpScreen.asStateFlow()

    init {
        restoreSessionOnStartup()
        observeFcmNotifications()
    }

    private fun observeFcmNotifications() {
        viewModelScope.launch {
            com.example.fcm.FcmNotificationEvents.events.collect { event ->
                val notif = InAppNotification(
                    id = "fcm_${System.currentTimeMillis()}_${event.requestId.takeLast(4)}",
                    title = event.title,
                    message = event.body,
                    timestamp = event.timestamp,
                    type = event.type,
                    requestId = event.requestId,
                    isRead = false,
                    actionType = "TRACK"
                )
                _notifications.value = listOf(notif) + _notifications.value

                // If user is resident (or previewing), display dynamic resident alert banner
                if (event.type == "WASTE_ASSIGNED" || event.type == "DRIVER_ASSIGNED") {
                    _activeResidentAlert.value = ResidentAlert(
                        id = "alert_${System.currentTimeMillis()}",
                        title = "🚛 Waste Collection Assigned!",
                        message = "Driver ${event.driverName} (${event.vehicleReg}) is assigned to collect your waste (${event.trackingId}). ETA ~${event.etaMinutes} mins.",
                        type = AlertType.DISPATCH_ASSIGNED,
                        requestId = event.requestId,
                        driverName = event.driverName,
                        vehicleReg = event.vehicleReg,
                        etaMinutes = event.etaMinutes
                    )
                }
            }
        }
    }

    // Location & Map Provider Abstraction
    val locationRepository = com.example.data.location.LocationRepository()
    private val _useOpenStreetMap = MutableStateFlow(false)
    val useOpenStreetMap: StateFlow<Boolean> = _useOpenStreetMap.asStateFlow()

    fun toggleMapProvider(useOsm: Boolean) {
        _useOpenStreetMap.value = useOsm
    }

    // Authentication & Active logged in user & role
    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _isAuthenticating = MutableStateFlow(false)
    val isAuthenticating: StateFlow<Boolean> = _isAuthenticating.asStateFlow()

    private val _authVerificationStatus = MutableStateFlow<String?>(null)
    val authVerificationStatus: StateFlow<String?> = _authVerificationStatus.asStateFlow()

    private val _currentUserRole = MutableStateFlow(UserRole.RESIDENT)
    val currentUserRole: StateFlow<UserRole> = _currentUserRole.asStateFlow()

    private val _currentResidentUser = MutableStateFlow(
        UserEntity(
            id = "usr_resident_1",
            fullName = "Athuman Kasele",
            email = "athumankasele9@gmail.com",
            phone = "+255 712 345 678",
            role = UserRole.RESIDENT,
            nidaMasked = "********4921",
            address = "Plot 42, Kibada Road, Kigamboni",
            ward = "Kibada",
            district = "Kigamboni District"
        )
    )
    val currentResidentUser: StateFlow<UserEntity> = _currentResidentUser.asStateFlow()

    private val _currentDriver = MutableStateFlow<DriverApplicationEntity?>(null)
    val currentDriver: StateFlow<DriverApplicationEntity?> = _currentDriver.asStateFlow()

    // Real-Time Resident <-> Driver Chat Coordination
    private val _activeChatRequestId = MutableStateFlow<String?>(null)
    val activeChatRequestId: StateFlow<String?> = _activeChatRequestId.asStateFlow()

    private val _activeChatRequest = MutableStateFlow<CollectionRequestEntity?>(null)
    val activeChatRequest: StateFlow<CollectionRequestEntity?> = _activeChatRequest.asStateFlow()

    val activeChatMessages: StateFlow<List<ChatMessageEntity>> = _activeChatRequestId
        .flatMapLatest { reqId ->
            if (reqId == null) flowOf(emptyList())
            else repository.getChatMessages(reqId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun openChatForRequest(requestId: String) {
        _activeChatRequestId.value = requestId
        val req = allRequests.value.find { it.id == requestId }
        _activeChatRequest.value = req
        viewModelScope.launch {
            repository.markChatAsRead(requestId, _currentUserRole.value)
        }
    }

    fun closeChat() {
        _activeChatRequestId.value = null
        _activeChatRequest.value = null
    }

    fun sendChatMessage(messageText: String, isQuickAction: Boolean = false) {
        val reqId = _activeChatRequestId.value ?: return
        if (messageText.isBlank()) return

        val user = _currentResidentUser.value
        val role = _currentUserRole.value

        val senderName = if (role == UserRole.DRIVER) {
            val drv = _currentDriver.value ?: approvedDrivers.value.firstOrNull()
            drv?.fullName ?: user.fullName
        } else {
            user.fullName
        }

        viewModelScope.launch {
            repository.sendChatMessage(
                requestId = reqId,
                senderId = user.id,
                senderName = senderName,
                senderRole = role,
                messageText = messageText.trim(),
                isQuickAction = isQuickAction
            )
        }
    }

    fun login(user: UserEntity, role: UserRole, driverEntity: DriverApplicationEntity? = null) {
        _currentResidentUser.value = user
        _currentUserRole.value = role
        _currentDriver.value = driverEntity
        _isAuthenticated.value = true
        _activeResidentAlert.value = null
        if (user.id != "usr_resident_1") {
            _notifications.value = listOf(
                InAppNotification(
                    id = "notif_welcome_${System.currentTimeMillis()}",
                    title = "Karibu SmartWaste, ${user.fullName.split(" ").first()}!",
                    message = "Welcome to Kigamboni Smart Municipal Waste Services. Tap 'Request' on your dashboard whenever you need a garbage or recyclables pickup.",
                    timestamp = System.currentTimeMillis(),
                    type = "WELCOME",
                    isRead = false
                )
            )
        }
        _feedbackSnackbarMessage.value = "Welcome ${user.fullName} (${role.displayName} Portal)"
        viewModelScope.launch {
            repository.saveResidentUser(user)
            com.example.fcm.FcmTokenManager.fetchAndRegisterToken(
                context = getApplication(),
                userId = user.id,
                role = role.name
            )
        }
    }

    /**
     * Authoritative login: Authenticates via Firebase Auth, verifies role in Firestore, and redirects to the matching portal.
     */
    fun loginWithFirestoreRoleCheck(
        identifier: String,
        password: String = "",
        requestedPortal: UserRole? = null,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            _isAuthenticating.value = true
            _authVerificationStatus.value = "Authenticating with Firebase Auth & verifying permissions in Firestore..."
            try {
                val result = repository.authenticateWithFirestore(identifier, password, requestedPortal)
                when (result) {
                    is com.example.data.model.AuthLoginResult.Success -> {
                        _currentResidentUser.value = result.user
                        _currentUserRole.value = result.verifiedRole
                        _currentDriver.value = result.driver
                        _isAuthenticated.value = true
                        _activeResidentAlert.value = null
                        if (result.user.id != "usr_resident_1") {
                            _notifications.value = listOf(
                                InAppNotification(
                                    id = "notif_welcome_${System.currentTimeMillis()}",
                                    title = "Karibu SmartWaste, ${result.user.fullName.split(" ").first()}!",
                                    message = "Welcome to Kigamboni Smart Municipal Waste Services. Tap 'Request' on your dashboard whenever you need a garbage or recyclables pickup.",
                                    timestamp = System.currentTimeMillis(),
                                    type = "WELCOME",
                                    isRead = false
                                )
                            )
                        }
                        _authVerificationStatus.value = "Role verified: ${result.verifiedRole.displayName} [${result.verificationSource}]"
                        _feedbackSnackbarMessage.value = "Access Granted: Logged into ${result.verifiedRole.displayName} Portal"
                        onResult(true, "Role verified (${result.verifiedRole.displayName}). Redirecting to your assigned portal.")
                    }
                    is com.example.data.model.AuthLoginResult.DriverPendingApproval -> {
                        _authVerificationStatus.value = null
                        onResult(false, result.message)
                    }
                    is com.example.data.model.AuthLoginResult.DriverSuspended -> {
                        _authVerificationStatus.value = null
                        onResult(false, "Access Suspended: ${result.reason}. Please contact the Municipal Commissioner.")
                    }
                    is com.example.data.model.AuthLoginResult.BusinessPendingApproval -> {
                        _authVerificationStatus.value = null
                        onResult(false, result.message)
                    }
                    is com.example.data.model.AuthLoginResult.BusinessSuspended -> {
                        _authVerificationStatus.value = null
                        onResult(false, "Access Suspended: ${result.reason}. Please contact the Municipal Commissioner.")
                    }
                    is com.example.data.model.AuthLoginResult.UnauthorizedRole -> {
                        _authVerificationStatus.value = null
                        onResult(false, result.message)
                    }
                    is com.example.data.model.AuthLoginResult.NotFound -> {
                        _authVerificationStatus.value = null
                        onResult(false, "Account not registered: No account found for '${result.identifier}'. Please register first.")
                    }
                    is com.example.data.model.AuthLoginResult.Error -> {
                        _authVerificationStatus.value = null
                        onResult(false, result.message)
                    }
                }
            } catch (e: Exception) {
                _authVerificationStatus.value = null
                onResult(false, "Authentication error: ${e.message}")
            } finally {
                _isAuthenticating.value = false
            }
        }
    }

    /**
     * Google Sign-In with Firestore role verification & portal routing.
     */
    fun loginWithGoogleFirestore(
        email: String,
        displayName: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            _isAuthenticating.value = true
            _authVerificationStatus.value = "Verifying Google Account role in Cloud Firestore..."
            try {
                val result = repository.authenticateGoogleUserWithFirestore(email, displayName)
                when (result) {
                    is com.example.data.model.AuthLoginResult.Success -> {
                        _currentResidentUser.value = result.user
                        _currentUserRole.value = result.verifiedRole
                        _currentDriver.value = result.driver
                        _isAuthenticated.value = true
                        _authVerificationStatus.value = "Google role verified: ${result.verifiedRole.displayName}"
                        _feedbackSnackbarMessage.value = "Welcome ${result.user.fullName} (${result.verifiedRole.displayName} Portal)"
                        onResult(true, "Authenticated as ${result.verifiedRole.displayName}")
                    }
                    is com.example.data.model.AuthLoginResult.NotFound -> {
                        _authVerificationStatus.value = null
                        onResult(false, "Account not registered: No account found for '${result.identifier}'. Please register your account first.")
                    }
                    is com.example.data.model.AuthLoginResult.Error -> {
                        _authVerificationStatus.value = null
                        onResult(false, result.message)
                    }
                    else -> {
                        _authVerificationStatus.value = null
                        onResult(false, "No registered account found for this Google account. Please register first.")
                    }
                }
            } catch (e: Exception) {
                _authVerificationStatus.value = null
                onResult(false, "Google Sign-In failed: ${e.message}")
            } finally {
                _isAuthenticating.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.clearSession()
        }
        _isAuthenticated.value = false
        _currentDriver.value = null
        _authVerificationStatus.value = null
        _activeChatRequestId.value = null
        _activeChatRequest.value = null
        _showNewRequestDialog.value = false
        _showDriverApplicationDialog.value = false
        _showOtpScreen.value = false
        _pendingRegistrationEmail.value = null
        _otpError.value = null
        _selectedRequestForDispatch.value = null
        _selectedApplicationForReview.value = null
        _selectedReceipt.value = null
        _selectedRequestForRating.value = null
        _selectedRequestForComplaint.value = null
        _showOverflowBinReportScreen.value = false
        _feedbackSnackbarMessage.value = "Signed out of Smart Waste portal"
    }

    fun initiateResidentRegistration(
        fullName: String,
        email: String,
        phone: String,
        password: String = "",
        nidaNumber: String,
        address: String,
        ward: String,
        district: String = "Kigamboni District",
        isCommercial: Boolean = false,
        businessName: String = "",
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            _isAuthenticating.value = true
            _otpError.value = null
            try {
                val result = repository.initiateResidentRegistration(
                    fullName = fullName,
                    email = email,
                    phone = phone,
                    password = password,
                    nidaNumber = nidaNumber,
                    address = address,
                    ward = ward,
                    district = district,
                    isCommercial = isCommercial,
                    businessName = businessName
                )
                when (result) {
                    is com.example.data.model.OtpSendResult.OtpSent -> {
                        _pendingRegistrationEmail.value = result.email
                        _otpExpiresAt.value = result.expiresAt
                        _latestDispatchedOtpCode.value = result.otpCode
                        _showOtpScreen.value = true
                        onResult(true, "Verification code sent to ${result.email}. Please enter the 6-digit code.")
                    }
                    is com.example.data.model.OtpSendResult.DirectlyRegistered -> {
                        _currentResidentUser.value = result.user
                        _currentUserRole.value = UserRole.RESIDENT
                        _isAuthenticated.value = true
                        _showOtpScreen.value = false
                        _latestDispatchedOtpCode.value = null
                        _feedbackSnackbarMessage.value = "Welcome ${result.user.fullName}!"
                        onResult(true, "Registration successful!")
                    }
                    is com.example.data.model.OtpSendResult.Error -> {
                        _otpError.value = result.message
                        onResult(false, result.message)
                    }
                }
            } catch (e: Exception) {
                _otpError.value = "Registration error: ${e.message}"
                onResult(false, e.message)
            } finally {
                _isAuthenticating.value = false
            }
        }
    }

    fun verifyResidentOtp(
        email: String,
        code: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isVerifyingOtp.value = true
            _otpError.value = null
            try {
                val result = repository.verifyResidentRegistrationOtp(email, code)
                when (result) {
                    is com.example.data.model.OtpVerifyResult.Success -> {
                        _currentResidentUser.value = result.user
                        _currentUserRole.value = UserRole.RESIDENT
                        _currentDriver.value = null
                        _isAuthenticated.value = true
                        _showOtpScreen.value = false
                        _pendingRegistrationEmail.value = null
                        _latestDispatchedOtpCode.value = null
                        _feedbackSnackbarMessage.value = "Email verified! Welcome to SmartWaste Kigamboni, ${result.user.fullName}."
                        onSuccess()
                    }
                    is com.example.data.model.OtpVerifyResult.DriverSuccess -> {
                        _currentResidentUser.value = UserEntity(
                            id = result.driver.userId,
                            fullName = result.driver.fullName,
                            email = result.driver.email,
                            phone = result.driver.phone,
                            role = UserRole.DRIVER
                        )
                        _currentDriver.value = result.driver
                        _currentUserRole.value = UserRole.DRIVER
                        _isAuthenticated.value = true
                        _showOtpScreen.value = false
                        _pendingRegistrationEmail.value = null
                        _latestDispatchedOtpCode.value = null
                        _feedbackSnackbarMessage.value = "Driver account activated! Welcome ${result.driver.fullName}."
                        onSuccess()
                    }
                    is com.example.data.model.OtpVerifyResult.Expired -> {
                        _otpError.value = result.message
                        onError(result.message)
                    }
                    is com.example.data.model.OtpVerifyResult.InvalidCode -> {
                        _otpError.value = result.message
                        onError(result.message)
                    }
                    is com.example.data.model.OtpVerifyResult.Error -> {
                        _otpError.value = result.message
                        onError(result.message)
                    }
                }
            } catch (e: Exception) {
                _otpError.value = "Verification failed: ${e.message}"
                onError("Verification failed: ${e.message}")
            } finally {
                _isVerifyingOtp.value = false
            }
        }
    }

    fun resendResidentOtp(email: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _isResendingOtp.value = true
            _otpError.value = null
            try {
                val result = repository.resendResidentRegistrationOtp(email)
                when (result) {
                    is com.example.data.model.OtpSendResult.OtpSent -> {
                        _otpExpiresAt.value = result.expiresAt
                        _latestDispatchedOtpCode.value = result.otpCode
                        onResult(true, "A new 6-digit verification code has been sent to $email.")
                    }
                    is com.example.data.model.OtpSendResult.DirectlyRegistered -> {
                        onResult(true, "Account is already verified.")
                    }
                    is com.example.data.model.OtpSendResult.Error -> {
                        _otpError.value = result.message
                        onResult(false, result.message)
                    }
                }
            } catch (e: Exception) {
                _otpError.value = "Error resending code: ${e.message}"
                onResult(false, "Error resending code: ${e.message}")
            } finally {
                _isResendingOtp.value = false
            }
        }
    }

    fun cancelOtpVerification() {
        _showOtpScreen.value = false
        _latestDispatchedOtpCode.value = null
        _otpError.value = null
    }

    fun restoreSessionOnStartup() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val session = repository.restoreSession()
                if (session != null) {
                    val user = repository.allUsers.first().find { it.id == session.userId || it.email == session.email }
                        ?: repository.getUserById(session.userId)
                        ?: repository.getUserByEmail(session.email)
                    if (user != null) {
                        val driver = if (session.role == UserRole.DRIVER) {
                            repository.allApplications.first().find { it.id == session.driverId || it.userId == session.userId }
                        } else null

                        _currentResidentUser.value = user
                        _currentUserRole.value = session.role
                        _currentDriver.value = driver
                        _isAuthenticated.value = true
                    } else {
                        repository.clearSession()
                        _isAuthenticated.value = false
                    }
                } else {
                    val pending = repository.getPendingRegistration()
                    if (pending != null && System.currentTimeMillis() < pending.expiresAt) {
                        _pendingRegistrationEmail.value = pending.email
                        _otpExpiresAt.value = pending.expiresAt
                        _showOtpScreen.value = true
                    }
                }
            }
        }
    }

    // Dialog & UI interaction states
    private val _showNewRequestDialog = MutableStateFlow(false)
    val showNewRequestDialog = _showNewRequestDialog.asStateFlow()

    private val _showDriverApplicationDialog = MutableStateFlow(false)
    val showDriverApplicationDialog = _showDriverApplicationDialog.asStateFlow()

    private val _selectedRequestForDispatch = MutableStateFlow<CollectionRequestEntity?>(null)
    val selectedRequestForDispatch = _selectedRequestForDispatch.asStateFlow()

    private val _driverRecommendations = MutableStateFlow<List<MatchedDriverRecommendation>>(emptyList())
    val driverRecommendations = _driverRecommendations.asStateFlow()

    private val _selectedApplicationForReview = MutableStateFlow<DriverApplicationEntity?>(null)
    val selectedApplicationForReview = _selectedApplicationForReview.asStateFlow()

    private val _selectedReceipt = MutableStateFlow<CollectionRequestEntity?>(null)
    val selectedReceipt = _selectedReceipt.asStateFlow()

    private val _selectedRequestForRating = MutableStateFlow<CollectionRequestEntity?>(null)
    val selectedRequestForRating = _selectedRequestForRating.asStateFlow()

    private val _selectedRequestForComplaint = MutableStateFlow<CollectionRequestEntity?>(null)
    val selectedRequestForComplaint = _selectedRequestForComplaint.asStateFlow()

    private val _showOverflowBinReportScreen = MutableStateFlow(false)
    val showOverflowBinReportScreen = _showOverflowBinReportScreen.asStateFlow()

    private val _showThemeSettingsDialog = MutableStateFlow(false)
    val showThemeSettingsDialog = _showThemeSettingsDialog.asStateFlow()

    private val _showSchedulePlanDialog = MutableStateFlow(false)
    val showSchedulePlanDialog = _showSchedulePlanDialog.asStateFlow()

    private val _showCommercialRequestDialog = MutableStateFlow(false)
    val showCommercialRequestDialog = _showCommercialRequestDialog.asStateFlow()

    private val _showBusinessRegistrationDialog = MutableStateFlow(false)
    val showBusinessRegistrationDialog = _showBusinessRegistrationDialog.asStateFlow()

    // Notification & Resident Alert System
    private val _notifications = MutableStateFlow<List<InAppNotification>>(
        listOf(
            InAppNotification(
                id = "notif_nearby_1",
                title = "Truck Approaching: WST-2026-000582",
                message = "Compactor Truck T 123 ABC (Driver Juma Ali Mussa) is 450m away in Kibada. ETA 2 mins.",
                timestamp = System.currentTimeMillis() - 1000L * 60 * 3,
                type = "DRIVER_NEARBY",
                requestId = "req_001",
                isRead = false,
                actionType = "TRACK"
            ),
            InAppNotification(
                id = "notif_pickup_completed_1",
                title = "Pickup Completed & Weighed",
                message = "120 kg of Household Waste collected from Kibada Street. Official municipal receipt issued.",
                timestamp = System.currentTimeMillis() - 1000L * 60 * 25,
                type = "PICKUP_COMPLETED",
                requestId = "req_001",
                isRead = false,
                actionType = "RECEIPT"
            ),
            InAppNotification(
                id = "notif_payment_1",
                title = "M-Pesa Payment Confirmed",
                message = "TZS 36,000 confirmed for Request WST-2026-000582. Ref: MP-TZ-774921.",
                timestamp = System.currentTimeMillis() - 1000L * 60 * 50,
                type = "PAYMENT",
                requestId = "req_001",
                isRead = true,
                actionType = "RECEIPT"
            ),
            InAppNotification(
                id = "notif_recycling_1",
                title = "Somangila Recycling Hub",
                message = "Your segregated recyclables divert carbon emissions by 48.6 kg CO2e.",
                timestamp = System.currentTimeMillis() - 1000L * 60 * 180,
                type = "RECYCLING",
                isRead = true
            )
        )
    )
    val notifications: StateFlow<List<InAppNotification>> = _notifications.asStateFlow()

    val unreadNotificationCount: StateFlow<Int> = _notifications
        .map { list -> list.count { !it.isRead } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 2)

    private val _activeResidentAlert = MutableStateFlow<ResidentAlert?>(null)
    val activeResidentAlert: StateFlow<ResidentAlert?> = _activeResidentAlert.asStateFlow()

    fun dismissActiveResidentAlert() {
        _activeResidentAlert.value = null
    }

    fun markNotificationAsRead(notificationId: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == notificationId) it.copy(isRead = true) else it
        }
    }

    fun markAllNotificationsAsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }

    fun clearAllNotifications() {
        _notifications.value = emptyList()
    }

    fun triggerDriverNearbyAlert(requestId: String) {
        val req = allRequests.value.find { it.id == requestId }
            ?: allRequests.value.firstOrNull { it.collectionStatus != CollectionStatus.DISPOSED }
            ?: allRequests.value.firstOrNull()

        val drvName = req?.assignedDriverName ?: "Juma Ali Mussa"
        val vehReg = req?.vehicleRegNumber ?: "T 123 ABC"
        val addr = req?.pickupAddress ?: "Kibada Main Gate"
        val trackId = req?.trackingId ?: "WST-2026-000582"
        val reqId = req?.id ?: requestId

        val alert = ResidentAlert(
            id = "alert_${System.currentTimeMillis()}",
            title = "🚛 Driver Nearby! (ETA ~2 Mins)",
            message = "Driver $drvName (Truck $vehReg) is turning onto your street at $addr. Please have your bags ready outside!",
            type = AlertType.DRIVER_NEARBY,
            requestId = reqId,
            driverName = drvName,
            vehicleReg = vehReg,
            etaMinutes = 2
        )
        _activeResidentAlert.value = alert

        val notif = InAppNotification(
            id = "notif_${System.currentTimeMillis()}",
            title = "Driver Nearby: $trackId",
            message = "Driver $drvName (Truck $vehReg) is 400m away from $addr. Prepare for pickup.",
            timestamp = System.currentTimeMillis(),
            type = "DRIVER_NEARBY",
            requestId = reqId,
            isRead = false,
            actionType = "TRACK"
        )
        _notifications.value = listOf(notif) + _notifications.value
        _feedbackSnackbarMessage.value = "🔔 Driver Nearby Alert: $drvName approaching $addr!"
    }

    fun triggerPickupCompletedAlert(requestId: String) {
        val req = allRequests.value.find { it.id == requestId }
            ?: allRequests.value.firstOrNull()

        val drvName = req?.assignedDriverName ?: "Juma Ali Mussa"
        val trackId = req?.trackingId ?: "WST-2026-000582"
        val weight = req?.actualWeightKg ?: req?.quantityKg ?: 120
        val cat = req?.wasteCategory?.title ?: "Household Waste"
        val reqId = req?.id ?: requestId

        val alert = ResidentAlert(
            id = "alert_${System.currentTimeMillis()}",
            title = "🎉 Waste Pickup Completed!",
            message = "Your scheduled pickup ($trackId) has been safely collected ($weight kg of $cat). Official digital receipt has been stamped.",
            type = AlertType.PICKUP_COMPLETED,
            requestId = reqId,
            driverName = drvName,
            actualWeightKg = weight
        )
        _activeResidentAlert.value = alert

        val notif = InAppNotification(
            id = "notif_${System.currentTimeMillis()}",
            title = "Pickup Completed: $trackId",
            message = "$weight kg of $cat successfully collected by Driver $drvName. Verified on municipal ledger.",
            timestamp = System.currentTimeMillis(),
            type = "PICKUP_COMPLETED",
            requestId = reqId,
            isRead = false,
            actionType = "RECEIPT"
        )
        _notifications.value = listOf(notif) + _notifications.value
        _feedbackSnackbarMessage.value = "✅ Waste pickup completed for $trackId!"
    }

    private val _themeMode = MutableStateFlow(ThemeMode.DARK)
    val themeMode = _themeMode.asStateFlow()

    private val _appThemePalette = MutableStateFlow(AppThemePalette.ECO_EMERALD)
    val appThemePalette = _appThemePalette.asStateFlow()

    private val _fontSizeScale = MutableStateFlow(FontSizeScale.STANDARD)
    val fontSizeScale = _fontSizeScale.asStateFlow()

    private val _feedbackSnackbarMessage = MutableStateFlow<String?>(null)
    val feedbackSnackbarMessage = _feedbackSnackbarMessage.asStateFlow()

    fun switchRole(newRole: UserRole) {
        _currentUserRole.value = newRole
        _feedbackSnackbarMessage.value = "Switched to ${newRole.displayName} Portal"
    }

    fun openThemeSettingsDialog() { _showThemeSettingsDialog.value = true }
    fun closeThemeSettingsDialog() { _showThemeSettingsDialog.value = false }

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
        _feedbackSnackbarMessage.value = "Theme switched to ${mode.title}"
    }

    fun toggleThemeMode() {
        val nextMode = if (_themeMode.value == ThemeMode.DARK) ThemeMode.LIGHT else ThemeMode.DARK
        _themeMode.value = nextMode
        _feedbackSnackbarMessage.value = "Theme switched to ${nextMode.title}"
    }

    fun setAppThemePalette(palette: AppThemePalette) {
        _appThemePalette.value = palette
        _feedbackSnackbarMessage.value = "Color palette: ${palette.title}"
    }

    fun setFontSizeScale(scale: FontSizeScale) {
        _fontSizeScale.value = scale
        _feedbackSnackbarMessage.value = "Text size: ${scale.title}"
    }

    fun openOverflowBinReportScreen() { _showOverflowBinReportScreen.value = true }
    fun closeOverflowBinReportScreen() { _showOverflowBinReportScreen.value = false }

    fun openNewRequestDialog() { _showNewRequestDialog.value = true }
    fun closeNewRequestDialog() { _showNewRequestDialog.value = false }

    fun openDriverApplicationDialog() { _showDriverApplicationDialog.value = true }
    fun closeDriverApplicationDialog() { _showDriverApplicationDialog.value = false }

    fun openDispatchDialog(request: CollectionRequestEntity) {
        _selectedRequestForDispatch.value = request
        viewModelScope.launch {
            val matches = repository.getDriverRecommendations(request)
            _driverRecommendations.value = matches
        }
    }
    fun closeDispatchDialog() {
        _selectedRequestForDispatch.value = null
        _driverRecommendations.value = emptyList()
    }

    fun openReviewApplicationDialog(app: DriverApplicationEntity) { _selectedApplicationForReview.value = app }
    fun closeReviewApplicationDialog() { _selectedApplicationForReview.value = null }

    fun openReceiptDialog(req: CollectionRequestEntity) { _selectedReceipt.value = req }
    fun closeReceiptDialog() { _selectedReceipt.value = null }

    fun openRatingDialog(req: CollectionRequestEntity) { _selectedRequestForRating.value = req }
    fun closeRatingDialog() { _selectedRequestForRating.value = null }

    fun openComplaintDialog(req: CollectionRequestEntity) { _selectedRequestForComplaint.value = req }
    fun closeComplaintDialog() { _selectedRequestForComplaint.value = null }

    fun openSchedulePlanDialog() { _showSchedulePlanDialog.value = true }
    fun closeSchedulePlanDialog() { _showSchedulePlanDialog.value = false }

    fun openCommercialRequestDialog() { _showCommercialRequestDialog.value = true }
    fun closeCommercialRequestDialog() { _showCommercialRequestDialog.value = false }

    fun openBusinessRegistrationDialog() { _showBusinessRegistrationDialog.value = true }
    fun closeBusinessRegistrationDialog() { _showBusinessRegistrationDialog.value = false }

    fun clearSnackbar() { _feedbackSnackbarMessage.value = null }

    fun createRequest(
        category: WasteCategory,
        quantityKg: Int,
        bagCount: Int,
        description: String,
        pickupAddress: String,
        ward: String,
        district: String,
        latitude: Double,
        longitude: Double,
        preferredDate: String,
        preferredTimeSlot: String,
        urgency: RequestUrgency,
        paymentMethod: PaymentMethod,
        isRecurring: Boolean = false,
        recurringInterval: String = "NONE"
    ) {
        viewModelScope.launch {
            val created = repository.createCollectionRequest(
                resident = _currentResidentUser.value,
                category = category,
                quantityKg = quantityKg,
                bagCount = bagCount,
                description = description,
                pickupAddress = pickupAddress,
                ward = ward,
                district = district,
                latitude = latitude,
                longitude = longitude,
                preferredDate = preferredDate,
                preferredTimeSlot = preferredTimeSlot,
                urgency = urgency,
                paymentMethod = paymentMethod,
                isRecurring = isRecurring,
                recurringInterval = recurringInterval
            )
            _showNewRequestDialog.value = false
            _feedbackSnackbarMessage.value = "Request ${created.trackingId} created & payment confirmed!"
        }
    }

    fun submitDriverApplication(
        fullName: String,
        phone: String,
        email: String,
        nidaNumber: String,
        licenseNumber: String,
        licenseClass: String,
        licenseExpiry: String,
        vehicleReg: String,
        vehicleType: String,
        capacityKg: Int,
        insuranceExpiry: String,
        preferredZone: String
    ) {
        viewModelScope.launch {
            val app = repository.submitDriverApplication(
                fullName = fullName,
                phone = phone,
                email = email,
                nidaNumber = nidaNumber,
                licenseNumber = licenseNumber,
                licenseClass = licenseClass,
                licenseExpiry = licenseExpiry,
                vehicleReg = vehicleReg,
                vehicleType = vehicleType,
                capacityKg = capacityKg,
                insuranceExpiry = insuranceExpiry,
                preferredZone = preferredZone
            )
            _showDriverApplicationDialog.value = false
            _feedbackSnackbarMessage.value = "Application submitted! Forwarded to Municipal Commissioner for review."
        }
    }

    fun approveDriverApplication(applicationId: String, notes: String) {
        viewModelScope.launch {
            repository.updateDriverApplicationStatus(
                applicationId = applicationId,
                newStatus = DriverApplicationStatus.APPROVED,
                officerNotes = notes.ifBlank { "Approved by Municipal Commissioner. Identity, NIDA, and vehicle capacity verified." }
            )
            _selectedApplicationForReview.value = null
            _feedbackSnackbarMessage.value = "Driver approved! Official Driver ID and route license issued."
        }
    }

    fun quickApproveDriver(applicationId: String, notes: String = "Approved by Municipal Commissioner via Quick Approval.") {
        viewModelScope.launch {
            repository.updateDriverApplicationStatus(
                applicationId = applicationId,
                newStatus = DriverApplicationStatus.APPROVED,
                officerNotes = notes
            )
            _feedbackSnackbarMessage.value = "Driver registration approved & issued official ID license."
        }
    }

    fun quickRejectDriver(applicationId: String, reason: String = "Rejected by Municipal Commissioner.") {
        viewModelScope.launch {
            repository.updateDriverApplicationStatus(
                applicationId = applicationId,
                newStatus = DriverApplicationStatus.REJECTED,
                officerNotes = reason
            )
            _feedbackSnackbarMessage.value = "Driver registration rejected."
        }
    }

    fun quickAutoAssignRequest(requestId: String) {
        viewModelScope.launch {
            val req = allRequests.value.find { it.id == requestId }
            if (req != null) {
                val recs = repository.getDriverRecommendations(req)
                val bestRec = recs.firstOrNull()
                val targetDriver = if (bestRec != null) {
                    approvedDrivers.value.find { it.id == bestRec.driverId || it.assignedDriverId == bestRec.driverId }
                        ?: approvedDrivers.value.firstOrNull()
                } else {
                    approvedDrivers.value.firstOrNull()
                }

                if (targetDriver != null) {
                    repository.assignDriverToRequest(requestId, targetDriver)
                    _feedbackSnackbarMessage.value = "Auto-assigned to optimal truck ${targetDriver.fullName} (${targetDriver.preferredZone})"
                } else {
                    _feedbackSnackbarMessage.value = "No approved drivers available for assignment."
                }
            }
        }
    }

    fun batchAutoAssignAllPending() {
        viewModelScope.launch {
            val pending = allRequests.value.filter { 
                it.collectionStatus in listOf(CollectionStatus.REQUESTED, CollectionStatus.PAID, CollectionStatus.WAITING_ASSIGNMENT) 
            }
            val approved = approvedDrivers.value
            if (approved.isEmpty()) {
                _feedbackSnackbarMessage.value = "Cannot batch assign: No approved drivers in system."
                return@launch
            }
            var assignedCount = 0
            pending.forEachIndexed { index, req ->
                val driver = approved[index % approved.size]
                repository.assignDriverToRequest(req.id, driver)
                assignedCount++
            }
            _feedbackSnackbarMessage.value = "Successfully dispatched $assignedCount pending waste collection requests!"
        }
    }

    fun batchApprovePendingApplications() {
        viewModelScope.launch {
            val pendingApps = allApplications.value.filter {
                it.status == DriverApplicationStatus.SUBMITTED || it.status == DriverApplicationStatus.UNDER_REVIEW
            }
            if (pendingApps.isEmpty()) {
                _feedbackSnackbarMessage.value = "No pending applications to approve."
                return@launch
            }
            pendingApps.forEach { app ->
                repository.updateDriverApplicationStatus(
                    applicationId = app.id,
                    newStatus = DriverApplicationStatus.APPROVED,
                    officerNotes = "Batch approved by Municipal Commissioner."
                )
            }
            _feedbackSnackbarMessage.value = "Batch approved ${pendingApps.size} driver registrations!"
        }
    }

    fun rejectDriverApplication(applicationId: String, notes: String) {
        viewModelScope.launch {
            repository.updateDriverApplicationStatus(
                applicationId = applicationId,
                newStatus = DriverApplicationStatus.REJECTED,
                officerNotes = notes.ifBlank { "Rejected due to incomplete or unverified documents." }
            )
            _selectedApplicationForReview.value = null
            _feedbackSnackbarMessage.value = "Driver application rejected."
        }
    }

    fun assignDriver(requestId: String, driver: DriverApplicationEntity) {
        viewModelScope.launch {
            repository.assignDriverToRequest(requestId, driver)
            closeDispatchDialog()
            _feedbackSnackbarMessage.value = "Assigned Driver ${driver.fullName} (${driver.vehicleRegNumber})"
        }
    }

    fun advanceDriverStatus(requestId: String, nextStatus: CollectionStatus, weightKg: Int? = null) {
        viewModelScope.launch {
            repository.advanceDriverCollectionWorkflow(
                requestId = requestId,
                nextStatus = nextStatus,
                actualWeightKg = weightKg
            )
            _feedbackSnackbarMessage.value = "Status updated to: ${nextStatus.label}"

            // Automatically dispatch resident notifications and alert banner based on status
            when (nextStatus) {
                CollectionStatus.ARRIVED -> {
                    triggerDriverNearbyAlert(requestId)
                }
                CollectionStatus.COLLECTED, CollectionStatus.DISPOSED -> {
                    triggerPickupCompletedAlert(requestId)
                }
                else -> { /* no-op */ }
            }
        }
    }

    fun checkInAtDisposalFacility(requestId: String, facilityId: String, facilityName: String, isRecycled: Boolean) {
        viewModelScope.launch {
            repository.verifyDisposalAndRecycling(
                requestId = requestId,
                facilityId = facilityId,
                facilityName = facilityName,
                isRecycled = isRecycled
            )
            _feedbackSnackbarMessage.value = "Waste disposal verified at $facilityName! Tracking closed."
        }
    }

    fun submitRating(requestId: String, score: Int, feedback: String) {
        viewModelScope.launch {
            repository.submitRating(requestId, score, feedback)
            _selectedRequestForRating.value = null
            _feedbackSnackbarMessage.value = "Thank you! Rating submitted."
        }
    }

    fun submitComplaint(requestId: String, driverId: String?, driverName: String?, category: String, description: String) {
        viewModelScope.launch {
            repository.submitComplaint(
                resident = _currentResidentUser.value,
                requestId = requestId,
                driverId = driverId,
                driverName = driverName,
                category = category,
                description = description
            )
            _selectedRequestForComplaint.value = null
            _feedbackSnackbarMessage.value = "Complaint lodged. Municipal inspection ticket created."
        }
    }

    fun claimRequestByDriver(requestId: String, driver: DriverApplicationEntity) {
        viewModelScope.launch {
            repository.assignDriverToRequest(
                requestId = requestId,
                driver = driver,
                commissionerName = "Driver Auto-Route Radar"
            )
            _feedbackSnackbarMessage.value = "Pickup added to your auto-optimized route!"
        }
    }

    fun reportOverflowingBin(
        ward: String,
        locationDescription: String,
        severity: String,
        notes: String,
        photoProof: String? = null,
        latitude: Double = -6.8650,
        longitude: Double = 39.3100
    ) {
        viewModelScope.launch {
            val complaint = repository.reportOverflowingBin(
                resident = _currentResidentUser.value,
                ward = ward,
                locationDescription = locationDescription,
                severity = severity,
                notes = notes,
                photoProof = photoProof,
                latitude = latitude,
                longitude = longitude
            )
            _showOverflowBinReportScreen.value = false
            _feedbackSnackbarMessage.value = "🚨 Incident ${complaint.requestId} lodged! Municipal sweep crew notified for $ward."
        }
    }

    fun resolveComplaint(complaintId: String, notes: String) {
        viewModelScope.launch {
            repository.resolveComplaint(
                complaintId = complaintId,
                resolutionNotes = notes.ifBlank { "Investigated and resolved by Municipal Health Officer." },
                officerName = "Hon. Commissioner David Malecela"
            )
            _feedbackSnackbarMessage.value = "Complaint marked as Resolved."
        }
    }

    fun suspendDriver(driverId: String, reason: String) {
        viewModelScope.launch {
            repository.suspendDriver(driverId, reason)
            _feedbackSnackbarMessage.value = "Driver $driverId suspended for compliance review."
        }
    }

    fun reactivateDriver(driverId: String) {
        viewModelScope.launch {
            repository.reactivateDriver(driverId)
            _feedbackSnackbarMessage.value = "Driver $driverId reactivated for dispatch."
        }
    }

    fun saveResidentUser(user: UserEntity) {
        viewModelScope.launch {
            _currentResidentUser.value = user
            repository.saveResidentUser(user)
            _feedbackSnackbarMessage.value = "Profile updated for ${user.fullName}."
        }
    }

    fun registerResidentUser(
        fullName: String,
        email: String,
        phone: String,
        password: String = "",
        nidaNumber: String,
        address: String,
        ward: String,
        district: String = "Kigamboni District",
        isCommercial: Boolean = false,
        businessName: String = "",
        businessType: BusinessType = BusinessType.RESTAURANT,
        businessTin: String = "",
        businessContactPerson: String = "",
        businessContactPhone: String = "",
        businessContactEmail: String = "",
        businessOperatingHours: String = "08:00 - 18:00",
        businessBranchName: String = "",
        businessLoadingDockNotes: String = "",
        businessBinCount: Int = 2,
        businessBinType: String = "240L Heavy Wheelie Bin",
        latitude: Double = -6.8520,
        longitude: Double = 39.3250
    ) {
        if (isCommercial) {
            registerCommercialBusiness(
                businessName = businessName.ifBlank { fullName },
                businessType = businessType,
                tinNumber = businessTin.ifBlank { "TIN-${(100000000..999999999).random()}" },
                contactPerson = businessContactPerson.ifBlank { fullName },
                contactPhone = businessContactPhone.ifBlank { phone },
                contactEmail = businessContactEmail.ifBlank { email },
                password = password,
                address = address,
                ward = ward,
                district = district,
                operatingHours = businessOperatingHours,
                branchName = businessBranchName,
                loadingDockNotes = businessLoadingDockNotes,
                binCount = businessBinCount,
                binType = businessBinType,
                latitude = latitude,
                longitude = longitude
            )
        } else {
            viewModelScope.launch {
                val newUser = repository.registerResidentUser(
                    fullName = fullName,
                    email = email,
                    phone = phone,
                    password = password,
                    nidaNumber = nidaNumber,
                    address = address,
                    ward = ward,
                    district = district,
                    isCommercial = false,
                    businessName = ""
                )
                login(newUser, UserRole.RESIDENT)
                _feedbackSnackbarMessage.value = "Welcome ${newUser.fullName}! Registered with secure Firebase Auth."
            }
        }
    }

    fun registerCommercialBusiness(
        businessName: String,
        businessType: BusinessType,
        tinNumber: String,
        contactPerson: String,
        contactPhone: String,
        contactEmail: String,
        password: String = "",
        address: String,
        ward: String,
        district: String = "Kigamboni District",
        operatingHours: String = "08:00 - 20:00",
        branchName: String = "",
        loadingDockNotes: String = "",
        binCount: Int = 2,
        binType: String = "240L Heavy Wheelie Bin",
        latitude: Double = -6.8520,
        longitude: Double = 39.3250
    ) {
        viewModelScope.launch {
            val user = repository.registerBusinessUser(
                businessName = businessName,
                businessType = businessType,
                businessTin = tinNumber,
                contactPerson = contactPerson,
                contactPhone = contactPhone,
                contactEmail = contactEmail,
                password = password,
                address = address,
                ward = ward,
                district = district,
                operatingHours = operatingHours,
                branchName = branchName,
                loadingDockNotes = loadingDockNotes,
                binCount = binCount,
                binType = binType,
                latitude = latitude,
                longitude = longitude
            )
            login(user, UserRole.RESIDENT)
            _showBusinessRegistrationDialog.value = false
            _feedbackSnackbarMessage.value = "🏢 Commercial Enterprise '${user.businessName}' registered! Accessing Commercial Portal."
        }
    }

    fun createCommercialRequest(
        category: WasteCategory,
        quantityKg: Int,
        bagOrBinCount: Int,
        containerType: String,
        description: String,
        pickupAddress: String,
        ward: String,
        district: String,
        landmarkOrDock: String,
        latitude: Double,
        longitude: Double,
        preferredDate: String,
        preferredTimeSlot: String,
        urgency: RequestUrgency,
        paymentMethod: PaymentMethod,
        isRecurring: Boolean = false,
        recurringFrequencyText: String = "NONE"
    ) {
        viewModelScope.launch {
            val req = repository.createCommercialCollectionRequest(
                user = _currentResidentUser.value,
                category = category,
                quantityKg = quantityKg,
                bagOrBinCount = bagOrBinCount,
                containerType = containerType,
                description = description,
                pickupAddress = pickupAddress,
                ward = ward,
                district = district,
                landmarkOrDock = landmarkOrDock,
                latitude = latitude,
                longitude = longitude,
                preferredDate = preferredDate,
                preferredTimeSlot = preferredTimeSlot,
                urgency = urgency,
                paymentMethod = paymentMethod,
                isRecurring = isRecurring,
                recurringFrequencyText = recurringFrequencyText
            )
            _showCommercialRequestDialog.value = false
            _showNewRequestDialog.value = false
            _feedbackSnackbarMessage.value = "🏢 Commercial Request ${req.trackingId} created! Assigned to municipal commercial pickup fleet."
        }
    }

    fun createScheduledCollectionPlan(
        frequency: ScheduleFrequency,
        selectedDays: String,
        timeSlot: String,
        category: WasteCategory,
        binCount: Int,
        containerType: String,
        pickupAddress: String,
        ward: String,
        landmark: String,
        latitude: Double,
        longitude: Double,
        monthlyRateTzs: Int
    ) {
        val user = _currentResidentUser.value
        val bizType = try {
            BusinessType.valueOf(user.businessType ?: BusinessType.RESTAURANT.name)
        } catch (e: Exception) {
            BusinessType.RESTAURANT
        }

        viewModelScope.launch {
            val plan = repository.createScheduledPlan(
                businessId = user.id,
                businessName = user.businessName ?: user.fullName,
                businessType = bizType,
                frequency = frequency,
                selectedDays = selectedDays,
                timeSlot = timeSlot,
                wasteCategory = category,
                binCount = binCount,
                containerType = containerType,
                pickupAddress = pickupAddress,
                ward = ward,
                landmark = landmark,
                latitude = latitude,
                longitude = longitude,
                monthlyRateTzs = monthlyRateTzs
            )
            _showSchedulePlanDialog.value = false
            _feedbackSnackbarMessage.value = "📅 Scheduled Collection Contract ${plan.contractRef} activated!"
        }
    }

    fun toggleScheduledPlanActive(planId: String, isActive: Boolean) {
        viewModelScope.launch {
            repository.togglePlanActive(planId, isActive)
            _feedbackSnackbarMessage.value = if (isActive) "Collection schedule resumed." else "Collection schedule paused."
        }
    }

    fun deleteScheduledPlan(planId: String) {
        viewModelScope.launch {
            repository.deleteScheduledPlan(planId)
            _feedbackSnackbarMessage.value = "Scheduled contract plan cancelled."
        }
    }

    // High-Precision GPS Telemetry & Synchronization
    private val _highPrecisionGpsLocation = MutableStateFlow(
        HighPrecisionLocation(
            latitude = -6.852341,
            longitude = 39.325120,
            accuracyMeters = 2.4f,
            speedKmh = 24.5f,
            provider = "High Precision GNSS/Fused"
        )
    )
    val highPrecisionGpsLocation: StateFlow<HighPrecisionLocation> = _highPrecisionGpsLocation.asStateFlow()

    private val _isGpsLiveTrackingActive = MutableStateFlow(true)
    val isGpsLiveTrackingActive: StateFlow<Boolean> = _isGpsLiveTrackingActive.asStateFlow()

    fun toggleGpsLiveTracking() {
        _isGpsLiveTrackingActive.value = !_isGpsLiveTrackingActive.value
        _feedbackSnackbarMessage.value = if (_isGpsLiveTrackingActive.value) "Live GPS telemetry active (High Precision)" else "GPS tracking paused"
    }

    fun updateHighPrecisionLocation(location: HighPrecisionLocation) {
        _highPrecisionGpsLocation.value = location
        if (_currentUserRole.value == UserRole.DRIVER) {
            val currentDrv = _currentDriver.value
            val drvId = currentDrv?.assignedDriverId ?: currentDrv?.id ?: "DRV-KGB-001"
            viewModelScope.launch {
                repository.updateDriverGPSLocation(drvId, location.latitude, location.longitude)
            }
        }
    }

    fun updateDriverGPSCoordinates(driverId: String, lat: Double, lng: Double) {
        viewModelScope.launch {
            repository.updateDriverGPSLocation(driverId, lat, lng)
        }
    }

    fun updateResidentPickupGPS(requestId: String, lat: Double, lng: Double, address: String? = null) {
        viewModelScope.launch {
            repository.updateResidentRequestGPSLocation(requestId, lat, lng, address)
            _feedbackSnackbarMessage.value = "📍 High-precision pickup coordinates updated (±1.5m accuracy)"
        }
    }

    fun updateResidentRequestGpsPin(requestId: String, lat: Double, lng: Double, address: String? = null) {
        updateResidentPickupGPS(requestId, lat, lng, address)
    }

    fun simulateDriverProgressTowardsPickup(requestId: String) {
        val req = allRequests.value.find { it.id == requestId } ?: return
        val currentLat = req.driverCurrentLat ?: -6.8410
        val currentLng = req.driverCurrentLng ?: 39.3180
        val targetLat = req.latitude
        val targetLng = req.longitude

        // Move 25% closer along the vector
        val nextLat = currentLat + (targetLat - currentLat) * 0.25
        val nextLng = currentLng + (targetLng - currentLng) * 0.25
        val driverId = req.assignedDriverId ?: "DRV-KGB-001"

        viewModelScope.launch {
            repository.updateDriverGPSLocation(driverId, nextLat, nextLng)
            val dLat = Math.abs(nextLat - targetLat)
            val dLng = Math.abs(nextLng - targetLng)
            if (dLat < 0.008 && dLng < 0.008) {
                // Driver is nearby (under ~800m)!
                triggerDriverNearbyAlert(requestId)
            }
        }
    }

    fun simulateDriverMove(requestId: String) {
        simulateDriverProgressTowardsPickup(requestId)
    }

    fun startLiveGpsTracking(application: Application) {
        viewModelScope.launch {
            GPSLocationHelper.getHighPrecisionLocationUpdates(application).collect { loc ->
                _highPrecisionGpsLocation.value = loc
                if (_currentUserRole.value == UserRole.DRIVER) {
                    val driverId = _currentDriver.value?.assignedDriverId ?: _currentDriver.value?.id ?: "DRV-KGB-001"
                    repository.updateDriverGPSLocation(driverId, loc.latitude, loc.longitude)
                }
            }
        }
    }

    fun verifyDriverLogin(identifier: String, secret: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = repository.verifyDriverLogin(identifier, secret)
            when (result) {
                is com.example.data.model.DriverLoginResult.Success -> {
                    _currentResidentUser.value = result.user
                    _currentDriver.value = result.driver
                    _currentUserRole.value = UserRole.DRIVER
                    _isAuthenticated.value = true
                    onResult(true, "Authentication successful! Welcome Driver ${result.driver.fullName} (${result.driver.assignedDriverId}).")
                    _feedbackSnackbarMessage.value = "Logged in as Driver: ${result.driver.fullName}"
                }
                is com.example.data.model.DriverLoginResult.Suspended -> {
                    onResult(false, "Access Suspended: ${result.reason}. Please contact the Municipal Commissioner.")
                }
                is com.example.data.model.DriverLoginResult.PendingApproval -> {
                    onResult(false, "Application Under Review: Your driver registration is currently pending Municipal Commissioner approval.")
                }
                is com.example.data.model.DriverLoginResult.NotFound -> {
                    onResult(false, "Driver account not found. Please register or verify your license number / phone.")
                }
            }
        }
    }

    fun approveBusiness(businessId: String, notes: String = "Approved by Municipal Commissioner") {
        viewModelScope.launch {
            val actor = _currentResidentUser.value.fullName
            repository.approveBusiness(businessId, actor, notes)
            _feedbackSnackbarMessage.value = "Commercial waste license approved."
        }
    }

    fun rejectBusiness(businessId: String, reason: String) {
        viewModelScope.launch {
            val actor = _currentResidentUser.value.fullName
            repository.rejectBusiness(businessId, actor, reason)
            _feedbackSnackbarMessage.value = "Commercial business application rejected."
        }
    }

    fun suspendBusiness(businessId: String, reason: String) {
        viewModelScope.launch {
            val actor = _currentResidentUser.value.fullName
            repository.suspendBusiness(businessId, actor, reason)
            _feedbackSnackbarMessage.value = "Commercial account suspended."
        }
    }

    fun assignDriverWithVehicle(
        requestId: String,
        driverId: String,
        vehicleId: String?,
        method: com.example.data.model.AssignmentMethod = com.example.data.model.AssignmentMethod.MANUAL
    ) {
        viewModelScope.launch {
            val actor = _currentResidentUser.value.fullName
            repository.assignDriverWithVehicle(requestId, driverId, vehicleId, actor, method)
            _feedbackSnackbarMessage.value = "Pickup assigned to crew ($driverId)."
        }
    }

    fun recordDisposal(record: com.example.data.local.DisposalRecordEntity) {
        viewModelScope.launch {
            repository.recordDisposal(record)
            _feedbackSnackbarMessage.value = "Facility disposal record logged & manifest certified."
        }
    }
}
