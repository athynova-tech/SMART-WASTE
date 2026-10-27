package com.example.ui.screens

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.example.data.local.DriverApplicationEntity
import com.example.data.local.UserEntity
import com.example.data.model.CustomerType
import com.example.data.model.DriverApplicationStatus
import com.example.data.model.UserRole
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CharcoalOutline
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.CharcoalSurfaceVariant
import com.example.ui.theme.EcoGreenDark
import com.example.ui.theme.EcoGreenLight
import com.example.ui.theme.EcoGreenPrimary
import com.example.ui.theme.MunicipalBlue
import com.example.ui.theme.ObsidianDarkBg
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (UserEntity, UserRole, DriverApplicationEntity?) -> Unit,
    onRegisterResident: (fullName: String, email: String, phone: String, password: String, nida: String, address: String, ward: String, district: String, isCommercial: Boolean, businessName: String) -> Unit,
    onSubmitDriverApplication: (fullName: String, phone: String, email: String, nida: String, licNo: String, licClass: String, licExp: String, vehReg: String, vehType: String, cap: Int, insExp: String, zone: String) -> Unit,
    onVerifyDriverLogin: (identifier: String, secret: String, onResult: (Boolean, String?) -> Unit) -> Unit,
    onRegisterCommercial: ((businessName: String, businessType: com.example.data.model.BusinessType, tinNumber: String, contactPerson: String, contactPhone: String, contactEmail: String, password: String, address: String, ward: String, district: String, operatingHours: String, loadingDockNotes: String, binCount: Int, binType: String, latitude: Double, longitude: Double) -> Unit)? = null,
    onVerifyFirestoreLogin: ((identifier: String, password: String, requestedRole: UserRole?, onResult: (Boolean, String?) -> Unit) -> Unit)? = null,
    onGoogleSignInFirestore: ((email: String, name: String, onResult: (Boolean, String?) -> Unit) -> Unit)? = null,
    onInitiateResidentRegistration: ((fullName: String, email: String, phone: String, password: String, nida: String, address: String, ward: String, district: String, isCommercial: Boolean, businessName: String, onResult: (Boolean, String?) -> Unit) -> Unit)? = null,
    showOtpScreen: Boolean = false,
    pendingRegistrationEmail: String? = null,
    latestDispatchedOtpCode: String? = null,
    otpExpiresAt: Long? = null,
    otpError: String? = null,
    isVerifyingOtp: Boolean = false,
    isResendingOtp: Boolean = false,
    onVerifyResidentOtp: ((email: String, code: String, onSuccess: () -> Unit, onError: (String) -> Unit) -> Unit)? = null,
    onResendResidentOtp: ((email: String, onResult: (Boolean, String) -> Unit) -> Unit)? = null,
    onCancelOtpVerification: (() -> Unit)? = null,
    isAuthenticating: Boolean = false,
    authVerificationStatus: String? = null,
    driverApplications: List<DriverApplicationEntity> = emptyList(),
    onToggleThemeMode: (() -> Unit)? = null,
    onOpenThemeSettings: (() -> Unit)? = null,
    isDarkMode: Boolean = true,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // 0: Resident / Citizen, 1: Truck Driver (Register & Gated Login), 2: Municipal Commissioner
    var selectedPortalTab by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(false) }

    // --- Resident States ---
    var isResidentRegisterMode by remember { mutableStateOf(false) }
    var residentLoginIdentifier by remember { mutableStateOf("athumankasele9@gmail.com") }
    var residentLoginPassword by remember { mutableStateOf("") }
    var isResidentPasswordVisible by remember { mutableStateOf(false) }
    var residentLoginFeedback by remember { mutableStateOf<String?>(null) }

    // Resident Register Fields
    var regFullName by remember { mutableStateOf("Athuman Kasele") }
    var regEmail by remember { mutableStateOf("athumankasele9@gmail.com") }
    var regPhone by remember { mutableStateOf("+255 712 345 678") }
    var regNida by remember { mutableStateOf("19940820-23114-00001-28") }
    var regAddress by remember { mutableStateOf("Plot 42, Kibada Road, Kigamboni") }
    var regWard by remember { mutableStateOf("Kibada") }
    var regIsCommercial by remember { mutableStateOf(false) }
    var regBusinessName by remember { mutableStateOf("") }
    var regBusinessType by remember { mutableStateOf(com.example.data.model.BusinessType.HOTEL) }
    var regTinNumber by remember { mutableStateOf("") }
    var regContactPerson by remember { mutableStateOf("") }
    var regOperatingHours by remember { mutableStateOf("06:00 AM - 23:00 PM") }
    var regLoadingDockNotes by remember { mutableStateOf("") }
    var regContainerType by remember { mutableStateOf("1100L Four-Wheel Industrial Dumpster") }
    var regBinCount by remember { mutableIntStateOf(2) }
    var regPassword by remember { mutableStateOf("") }

    // --- Driver States ---
    var isDriverRegisterMode by remember { mutableStateOf(false) }
    var driverLoginId by remember { mutableStateOf("") }
    var driverLoginPhoneOrPass by remember { mutableStateOf("") }
    var driverLoginFeedback by remember { mutableStateOf<String?>(null) }
    var driverLoginIsSuccess by remember { mutableStateOf(false) }

    // Driver Registration Request Fields
    var drvRegFullName by remember { mutableStateOf("") }
    var drvRegPhone by remember { mutableStateOf("+255 ") }
    var drvRegEmail by remember { mutableStateOf("") }
    var drvRegNida by remember { mutableStateOf("") }
    var drvRegLicNo by remember { mutableStateOf("TZ-DL-") }
    var drvRegLicClass by remember { mutableStateOf("Class C (Heavy Commercial)") }
    var drvRegLicExp by remember { mutableStateOf("2028-12-31") }
    var drvRegVehReg by remember { mutableStateOf("T ") }
    var drvRegVehType by remember { mutableStateOf("Rear Loader Compactor Truck") }
    var drvRegCapacityKg by remember { mutableStateOf("2500") }
    var drvRegInsExp by remember { mutableStateOf("2027-06-30") }
    var drvRegZone by remember { mutableStateOf("Kigamboni & Kibada") }
    var drvApplicationSubmittedInfo by remember { mutableStateOf<String?>(null) }

    // --- Municipal Commissioner States ---
    var commishEmailOrId by remember { mutableStateOf("") }
    var commishPassword by remember { mutableStateOf("") }
    var isCommishPasswordVisible by remember { mutableStateOf(false) }
    var commishLoginFeedback by remember { mutableStateOf<String?>(null) }

    fun executeGoogleSignIn() {
        isLoading = true
        coroutineScope.launch {
            try {
                val credentialManager = CredentialManager.create(context)
                val googleIdOption = GetSignInWithGoogleOption.Builder("smartwaste-kigamboni-app")
                    .setNonce("smartwaste_nonce_${System.currentTimeMillis()}")
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(
                    request = request,
                    context = context as Activity
                )

                val credential = result.credential
                if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val idToken = googleIdTokenCredential.idToken
                    val email = googleIdTokenCredential.id
                    val displayName = googleIdTokenCredential.displayName ?: "Citizen"

                    try {
                        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                        FirebaseAuth.getInstance().signInWithCredential(firebaseCredential)
                    } catch (e: Exception) {
                        Log.w("LoginScreen", "FirebaseAuth signInWithCredential bypassed: ${e.message}")
                    }

                    if (onGoogleSignInFirestore != null) {
                        onGoogleSignInFirestore(email, displayName) { success, msg ->
                            if (!success) {
                                residentLoginFeedback = msg ?: "Account '$email' is not registered. Please register first."
                                Toast.makeText(context, residentLoginFeedback, Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        residentLoginFeedback = "Account '$email' is not registered. Please register your account first."
                        Toast.makeText(context, residentLoginFeedback, Toast.LENGTH_LONG).show()
                    }
                } else {
                    fallbackGoogleLogin(context, onLoginSuccess, onGoogleSignInFirestore) { err ->
                        residentLoginFeedback = err
                    }
                }
            } catch (e: Exception) {
                Log.w("GoogleSignIn", "Credential Manager exception: ${e.message}, using fallback")
                fallbackGoogleLogin(context, onLoginSuccess, onGoogleSignInFirestore) { err ->
                    residentLoginFeedback = err
                }
            } finally {
                isLoading = false
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar Quick Actions for Theme & Display Customization
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onToggleThemeMode != null) {
                    androidx.compose.material3.IconButton(
                        onClick = onToggleThemeMode,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Light/Dark Theme",
                            tint = if (isDarkMode) Color(0xFFFBBF24) else Color(0xFF475569),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                if (onOpenThemeSettings != null) {
                    androidx.compose.material3.IconButton(
                        onClick = onOpenThemeSettings,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Theme and Font Size Settings",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Municipal Logo & Emblem
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(EcoGreenPrimary.copy(alpha = 0.3f), Color.Transparent)
                        )
                    )
                    .border(2.dp, EcoGreenPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Recycling,
                    contentDescription = "Kigamboni Smart Waste",
                    tint = EcoGreenPrimary,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "KIGAMBONI SMART WASTE",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.2.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Manispaa ya Kigamboni • Municipal Waste Portal",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Cloud Firestore Role Verification Status Banner
            if (isAuthenticating || authVerificationStatus != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = CharcoalSurfaceVariant
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EcoGreenPrimary.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isAuthenticating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = EcoGreenPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = EcoGreenPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Cloud Firestore Role Verification",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EcoGreenPrimary
                            )
                            Text(
                                text = authVerificationStatus ?: "Querying Cloud Firestore to verify user role and authorize portal access...",
                                fontSize = 10.sp,
                                color = Slate200
                            )
                        }
                    }
                }
            }

            if (showOtpScreen && pendingRegistrationEmail != null) {
                ResidentOtpVerificationCard(
                    email = pendingRegistrationEmail,
                    dispatchedCode = latestDispatchedOtpCode,
                    expiresAt = otpExpiresAt,
                    error = otpError,
                    isVerifying = isVerifyingOtp,
                    isResending = isResendingOtp,
                    onVerify = { code ->
                        if (onVerifyResidentOtp != null) {
                            onVerifyResidentOtp(pendingRegistrationEmail, code, {
                                Toast.makeText(context, "Verification Successful! Accessing Portal...", Toast.LENGTH_SHORT).show()
                            }, { errorMsg ->
                                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                            })
                        }
                    },
                    onResend = {
                        if (onResendResidentOtp != null) {
                            onResendResidentOtp(pendingRegistrationEmail) { success, msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    onCancel = {
                        onCancelOtpVerification?.invoke()
                    }
                )
            } else {
            // Quick Demo Persona Credentials Shortcut Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .testTag("demo_personas_quick_select_card"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CharcoalSurfaceVariant.copy(alpha = 0.5f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalOutline)
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = EcoGreenPrimary, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("1-Tap Demo Credentials", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Slate50)
                        }
                        Text("Instant Auto-Fill", fontSize = 10.sp, color = Slate400)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = selectedPortalTab == 0 && residentLoginIdentifier == "athumankasele9@gmail.com",
                            onClick = {
                                selectedPortalTab = 0
                                isResidentRegisterMode = false
                                residentLoginIdentifier = "athumankasele9@gmail.com"
                                residentLoginPassword = "SmartWaste2026!"
                            },
                            label = { Text("🏠 Resident", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EcoGreenPrimary,
                                selectedLabelColor = Color.Black
                            )
                        )
                        FilterChip(
                            selected = selectedPortalTab == 0 && residentLoginIdentifier == "operations@serenacoastal.co.tz",
                            onClick = {
                                selectedPortalTab = 0
                                isResidentRegisterMode = false
                                residentLoginIdentifier = "operations@serenacoastal.co.tz"
                                residentLoginPassword = "Business2026!"
                            },
                            label = { Text("🏢 Business", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MunicipalBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                        FilterChip(
                            selected = selectedPortalTab == 1,
                            onClick = {
                                selectedPortalTab = 1
                                isDriverRegisterMode = false
                                driverLoginId = "drv_001"
                                driverLoginPhoneOrPass = "+255 754 112 233"
                            },
                            label = { Text("🚚 Driver", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF10B981),
                                selectedLabelColor = Color.Black
                            )
                        )
                        FilterChip(
                            selected = selectedPortalTab == 2,
                            onClick = {
                                selectedPortalTab = 2
                                commishEmailOrId = "commissioner@kigamboni.go.tz"
                                commishPassword = "Commissioner2026!"
                            },
                            label = { Text("🏛️ Commissioner", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AmberWarning,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }
            }

            // 3 Stakeholder Role Tabs
            TabRow(
                selectedTabIndex = selectedPortalTab,
                containerColor = CharcoalSurface,
                contentColor = EcoGreenPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedPortalTab]),
                        color = when (selectedPortalTab) {
                            0 -> EcoGreenPrimary
                            1 -> MunicipalBlue
                            else -> AmberWarning
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, CharcoalOutline, RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedPortalTab == 0,
                    onClick = { selectedPortalTab = 0 },
                    text = {
                        Text(
                            text = "Resident",
                            fontSize = 11.sp,
                            fontWeight = if (selectedPortalTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedPortalTab == 1,
                    onClick = { selectedPortalTab = 1 },
                    text = {
                        Text(
                            text = "Truck Driver",
                            fontSize = 11.sp,
                            fontWeight = if (selectedPortalTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedPortalTab == 2,
                    onClick = { selectedPortalTab = 2 },
                    text = {
                        Text(
                            text = "Commissioner",
                            fontSize = 11.sp,
                            fontWeight = if (selectedPortalTab == 2) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            when (selectedPortalTab) {
                0 -> {
                    // ==========================================
                    // RESIDENT & CITIZEN PORTAL (LOGIN / REGISTER)
                    // ==========================================
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalOutline)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            // Sub-header toggle
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = EcoGreenPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isResidentRegisterMode) "Register Citizen Account" else "Resident & Citizen Login",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Slate50
                                    )
                                }

                                Text(
                                    text = if (isResidentRegisterMode) "Have account? Sign In" else "New? Register here",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EcoGreenPrimary,
                                    modifier = Modifier
                                        .clickable { isResidentRegisterMode = !isResidentRegisterMode }
                                        .padding(4.dp)
                                        .testTag("resident_mode_toggle")
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (isResidentRegisterMode)
                                    "Create your municipal resident or commercial account to schedule pickups and track recycling."
                                else
                                    "Access your waste collection schedule, digital payments, and service tracking.",
                                fontSize = 12.sp,
                                color = Slate400
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            if (!isResidentRegisterMode) {
                                // SIGN IN MODE
                                OutlinedTextField(
                                    value = residentLoginIdentifier,
                                    onValueChange = { residentLoginIdentifier = it },
                                    label = { Text("Email or Phone Number", fontSize = 12.sp) },
                                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = EcoGreenPrimary) },
                                    placeholder = { Text("e.g. athumankasele9@gmail.com") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("resident_login_input"),
                                    colors = outlinedTextFieldColors(EcoGreenPrimary)
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = residentLoginPassword,
                                    onValueChange = { 
                                        residentLoginPassword = it 
                                        residentLoginFeedback = null
                                    },
                                    label = { Text("Password", fontSize = 12.sp) },
                                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Slate500) },
                                    trailingIcon = {
                                        IconButton(onClick = { isResidentPasswordVisible = !isResidentPasswordVisible }) {
                                            Icon(
                                                imageVector = if (isResidentPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                contentDescription = "Toggle Password",
                                                tint = Slate500
                                            )
                                        }
                                    },
                                    visualTransformation = if (isResidentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("resident_password_input"),
                                    colors = outlinedTextFieldColors(EcoGreenPrimary)
                                )

                                // Feedback Alert Box if login failed or account not registered
                                residentLoginFeedback?.let { feedbackMsg ->
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFFEF4444).copy(alpha = 0.15f))
                                            .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                            .padding(12.dp)
                                            .testTag("resident_login_error_box")
                                    ) {
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Warning,
                                                    contentDescription = null,
                                                    tint = Color(0xFFEF4444),
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "Access Restricted: Not Registered",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFFCA5A5)
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = feedbackMsg,
                                                fontSize = 11.sp,
                                                color = Slate200,
                                                lineHeight = 15.sp
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.End
                                            ) {
                                                OutlinedButton(
                                                    onClick = {
                                                        isResidentRegisterMode = true
                                                        if (residentLoginIdentifier.contains("@")) {
                                                            regEmail = residentLoginIdentifier
                                                        } else if (residentLoginIdentifier.isNotBlank()) {
                                                            regPhone = residentLoginIdentifier
                                                        }
                                                        residentLoginFeedback = null
                                                    },
                                                    shape = RoundedCornerShape(8.dp),
                                                    border = androidx.compose.foundation.BorderStroke(1.dp, EcoGreenPrimary),
                                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                                ) {
                                                    Icon(Icons.Default.Person, contentDescription = null, tint = EcoGreenPrimary, modifier = Modifier.size(14.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Register Now", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EcoGreenPrimary)
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                 Button(
                                    onClick = {
                                        if (residentLoginPassword.isBlank()) {
                                            Toast.makeText(context, "Please enter your password", Toast.LENGTH_SHORT).show()
                                        } else if (onVerifyFirestoreLogin != null) {
                                            onVerifyFirestoreLogin(residentLoginIdentifier, residentLoginPassword, UserRole.RESIDENT) { success, msg ->
                                                if (!success) {
                                                    residentLoginFeedback = msg ?: "Account '$residentLoginIdentifier' is not registered. Please register first."
                                                    Toast.makeText(context, residentLoginFeedback, Toast.LENGTH_LONG).show()
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("resident_sign_in_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = EcoGreenPrimary),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Sign In to Resident Portal", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedButton(
                                    onClick = {
                                        val targetEmail = if (residentLoginIdentifier.contains("@")) residentLoginIdentifier.trim() else "athumankasele9@gmail.com"
                                        val passToUse = if (residentLoginPassword.isNotBlank()) residentLoginPassword else "SmartWaste2026!"
                                        if (onInitiateResidentRegistration != null) {
                                            Toast.makeText(context, "Dispatching 6-digit verification code to $targetEmail...", Toast.LENGTH_SHORT).show()
                                            onInitiateResidentRegistration(
                                                "Athuman Kasele",
                                                targetEmail,
                                                "+255 712 345 678",
                                                passToUse,
                                                "19940820-23114-00001-28",
                                                "Plot 42, Kibada Road, Kigamboni",
                                                "Kibada",
                                                "Kigamboni District",
                                                false,
                                                ""
                                            ) { success, msg ->
                                                if (msg != null) {
                                                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(46.dp)
                                        .testTag("send_email_code_button"),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MunicipalBlue),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Email, contentDescription = null, tint = MunicipalBlue, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Send Email Verification Code to Login", color = MunicipalBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Divider(modifier = Modifier.weight(1f), color = CharcoalOutline)
                                    Text("  OR GOOGLE SIGN-IN  ", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Slate500)
                                    Divider(modifier = Modifier.weight(1f), color = CharcoalOutline)
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Button(
                                    onClick = { executeGoogleSignIn() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("resident_google_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF1F2937)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    if (isLoading) {
                                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black, strokeWidth = 2.dp)
                                    } else {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFF4285F4)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text("G", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Continue with Google", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                        }
                                    }
                                }
                            } else {
                                // REGISTER RESIDENT MODE
                                Text("Customer Type", fontSize = 11.sp, color = Slate400)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(ObsidianDarkBg)
                                        .padding(4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (!regIsCommercial) EcoGreenPrimary else Color.Transparent)
                                            .clickable { regIsCommercial = false }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "Household Resident",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (!regIsCommercial) Color.Black else Slate500
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (regIsCommercial) MunicipalBlue else Color.Transparent)
                                            .clickable { regIsCommercial = true }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "Commercial / Business",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (regIsCommercial) Color.Black else Slate500
                                        )
                                    }
                                }

                                if (regIsCommercial) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text("Business Sector / Category", fontSize = 11.sp, color = Slate400)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    val businessTypes = listOf(
                                        com.example.data.model.BusinessType.HOTEL to "🏨 Hotel / Resort",
                                        com.example.data.model.BusinessType.CAFE to "☕ Cafe / Bakery",
                                        com.example.data.model.BusinessType.RESTAURANT to "🍽️ Restaurant / Bar",
                                        com.example.data.model.BusinessType.RETAIL_SHOP to "🛒 Supermarket",
                                        com.example.data.model.BusinessType.OFFICE_CORPORATE to "🏢 Office / Corp",
                                        com.example.data.model.BusinessType.HOSPITAL_CLINIC to "🏥 Health Clinic",
                                        com.example.data.model.BusinessType.WAREHOUSE_LIGHT_INDUSTRY to "🏭 Light Industry",
                                        com.example.data.model.BusinessType.EVENT_VENUE to "🎉 Event Venue"
                                    )
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 2.dp),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        businessTypes.take(3).forEach { (bType, label) ->
                                            FilterChip(
                                                selected = regBusinessType == bType,
                                                onClick = { regBusinessType = bType },
                                                label = { Text(label, fontSize = 10.sp) },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = MunicipalBlue,
                                                    selectedLabelColor = Color.Black
                                                )
                                            )
                                        }
                                    }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 2.dp),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        businessTypes.drop(3).take(3).forEach { (bType, label) ->
                                            FilterChip(
                                                selected = regBusinessType == bType,
                                                onClick = { regBusinessType = bType },
                                                label = { Text(label, fontSize = 10.sp) },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = MunicipalBlue,
                                                    selectedLabelColor = Color.Black
                                                )
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                    OutlinedTextField(
                                        value = regBusinessName,
                                        onValueChange = { regBusinessName = it },
                                        label = { Text("Business / Enterprise Name", fontSize = 12.sp) },
                                        placeholder = { Text("e.g. South Beach Resort & Spa") },
                                        leadingIcon = { Icon(Icons.Default.Business, contentDescription = null, tint = MunicipalBlue) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("reg_commercial_bizname"),
                                        colors = outlinedTextFieldColors(MunicipalBlue)
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))
                                    OutlinedTextField(
                                        value = regTinNumber,
                                        onValueChange = { regTinNumber = it },
                                        label = { Text("TRA Taxpayer TIN Number (9 Digits)", fontSize = 12.sp) },
                                        placeholder = { Text("142-889-301") },
                                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = MunicipalBlue) },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = outlinedTextFieldColors(MunicipalBlue)
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))
                                    OutlinedTextField(
                                        value = if (regContactPerson.isBlank()) regFullName else regContactPerson,
                                        onValueChange = { 
                                            regContactPerson = it
                                            regFullName = it
                                        },
                                        label = { Text("Managing Director / Contact Person", fontSize = 12.sp) },
                                        placeholder = { Text("e.g. Fatma Rashidi") },
                                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MunicipalBlue) },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = outlinedTextFieldColors(MunicipalBlue)
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))
                                    OutlinedTextField(
                                        value = regOperatingHours,
                                        onValueChange = { regOperatingHours = it },
                                        label = { Text("Operating Hours / Receiving Window", fontSize = 12.sp) },
                                        placeholder = { Text("06:00 AM - 23:00 PM") },
                                        leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = MunicipalBlue) },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = outlinedTextFieldColors(MunicipalBlue)
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))
                                    OutlinedTextField(
                                        value = regLoadingDockNotes,
                                        onValueChange = { regLoadingDockNotes = it },
                                        label = { Text("Loading Dock / Service Gate Instructions", fontSize = 12.sp) },
                                        placeholder = { Text("Rear kitchen service ramp, gate 2") },
                                        leadingIcon = { Icon(Icons.Default.LocalShipping, contentDescription = null, tint = MunicipalBlue) },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = outlinedTextFieldColors(MunicipalBlue)
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text("Primary Waste Container Configuration", fontSize = 11.sp, color = Slate400)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    val containerTypes = listOf(
                                        "240L Heavy Wheelie Bin",
                                        "660L Municipal Bin",
                                        "1100L Industrial Dumpster"
                                    )
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 2.dp),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        containerTypes.forEach { cType ->
                                            FilterChip(
                                                selected = regContainerType == cType,
                                                onClick = { regContainerType = cType },
                                                label = { Text(cType.substringBefore(" "), fontSize = 10.sp) },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = MunicipalBlue,
                                                    selectedLabelColor = Color.Black
                                                )
                                            )
                                        }
                                    }
                                } else {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    OutlinedTextField(
                                        value = regFullName,
                                        onValueChange = { regFullName = it },
                                        label = { Text("Full Legal Name", fontSize = 12.sp) },
                                        placeholder = { Text("e.g. Athuman Kasele") },
                                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = EcoGreenPrimary) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("reg_resident_fullname"),
                                        colors = outlinedTextFieldColors(EcoGreenPrimary)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = regEmail,
                                    onValueChange = { regEmail = it },
                                    label = { Text(if (regIsCommercial) "Official Business Email" else "Email Address", fontSize = 12.sp) },
                                    placeholder = { Text(if (regIsCommercial) "info@southbeachhotel.co.tz" else "resident@gmail.com") },
                                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = if (regIsCommercial) MunicipalBlue else EcoGreenPrimary) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("reg_resident_email"),
                                    colors = outlinedTextFieldColors(if (regIsCommercial) MunicipalBlue else EcoGreenPrimary)
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = regPhone,
                                    onValueChange = { regPhone = it },
                                    label = { Text("Mobile Phone (M-Pesa / Tigo Pesa)", fontSize = 12.sp) },
                                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = if (regIsCommercial) MunicipalBlue else EcoGreenPrimary) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("reg_resident_phone"),
                                    colors = outlinedTextFieldColors(if (regIsCommercial) MunicipalBlue else EcoGreenPrimary)
                                )

                                if (!regIsCommercial) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    OutlinedTextField(
                                        value = regNida,
                                        onValueChange = { regNida = it },
                                        label = { Text("National ID (NIDA) - 20 Digits", fontSize = 12.sp) },
                                        placeholder = { Text("19940820-23114-00001-28") },
                                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = EcoGreenPrimary) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("reg_resident_nida"),
                                        colors = outlinedTextFieldColors(EcoGreenPrimary)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = regAddress,
                                    onValueChange = { regAddress = it },
                                    label = { Text(if (regIsCommercial) "Physical Business Address / Plot" else "Street Address / Plot Number", fontSize = 12.sp) },
                                    placeholder = { Text(if (regIsCommercial) "Plot 88 Ocean Drive, Mjimwema" else "Plot 42, Kibada Road") },
                                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = if (regIsCommercial) MunicipalBlue else EcoGreenPrimary) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("reg_resident_address"),
                                    colors = outlinedTextFieldColors(if (regIsCommercial) MunicipalBlue else EcoGreenPrimary)
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Text("Municipal Ward (Kigamboni)", fontSize = 11.sp, color = Slate400)
                                Spacer(modifier = Modifier.height(4.dp))
                                val wards = listOf("Kibada", "Kigamboni", "Mjimwema", "Somangila", "Pembamnazi", "Vijibweni", "Kimbiji", "Kisarawe II")
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    wards.take(4).forEach { ward ->
                                        FilterChip(
                                            selected = regWard == ward,
                                            onClick = { regWard = ward },
                                            label = { Text(ward, fontSize = 10.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = if (regIsCommercial) MunicipalBlue else EcoGreenPrimary,
                                                selectedLabelColor = Color.Black
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = regPassword,
                                    onValueChange = { regPassword = it },
                                    label = { Text("Create Password", fontSize = 12.sp) },
                                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Slate500) },
                                    visualTransformation = PasswordVisualTransformation(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("reg_resident_password"),
                                    colors = outlinedTextFieldColors(if (regIsCommercial) MunicipalBlue else EcoGreenPrimary)
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = {
                                        val validName = if (regFullName.isNotBlank()) regFullName else if (regContactPerson.isNotBlank()) regContactPerson else if (regIsCommercial) "South Beach Resort" else "Athuman Kasele"
                                        val validEmail = if (regEmail.isNotBlank()) regEmail.trim() else "athumankasele9@gmail.com"
                                        val validPhone = if (regPhone.length < 5) "+255 712 345 678" else regPhone
                                        val validNida = if (regNida.isBlank()) "19940820-23114-00001-28" else regNida
                                        val validAddr = if (regAddress.isBlank()) (if (regIsCommercial) "Plot 88 Ocean Drive, Kigamboni" else "Plot 42, Kibada Road, Kigamboni") else regAddress
                                        val validBizName = if (regBusinessName.isBlank()) "South Beach Resort" else regBusinessName

                                        val passToUse = if (regPassword.isNotBlank()) regPassword else "SmartWaste2026!"
                                        if (onInitiateResidentRegistration != null) {
                                            onInitiateResidentRegistration(
                                                validName,
                                                validEmail,
                                                validPhone,
                                                passToUse,
                                                validNida,
                                                validAddr,
                                                regWard,
                                                "Kigamboni District",
                                                regIsCommercial,
                                                validBizName
                                            ) { success, msg ->
                                                if (msg != null) {
                                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        } else if (regIsCommercial && onRegisterCommercial != null) {
                                            onRegisterCommercial(
                                                validBizName,
                                                regBusinessType,
                                                if (regTinNumber.isBlank()) "142-889-301" else regTinNumber,
                                                validName,
                                                validPhone,
                                                validEmail,
                                                passToUse,
                                                validAddr,
                                                regWard,
                                                "Kigamboni District",
                                                if (regOperatingHours.isBlank()) "06:00 AM - 23:00 PM" else regOperatingHours,
                                                regLoadingDockNotes,
                                                regBinCount,
                                                regContainerType,
                                                -6.8280,
                                                39.3020
                                            )
                                            Toast.makeText(context, "Commercial Registration Complete! Welcome $validBizName", Toast.LENGTH_SHORT).show()
                                        } else {
                                            onRegisterResident(
                                                validName,
                                                validEmail,
                                                validPhone,
                                                passToUse,
                                                validNida,
                                                validAddr,
                                                regWard,
                                                "Kigamboni District",
                                                regIsCommercial,
                                                validBizName
                                            )
                                            Toast.makeText(context, "Registration Complete! Welcome $validName", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("register_resident_submit_btn"),
                                    colors = ButtonDefaults.buttonColors(containerColor = if (regIsCommercial) MunicipalBlue else EcoGreenPrimary),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        if (regIsCommercial) "Register Commercial Enterprise" else "Complete Registration & Enter",
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // =======================================================
                    // TRUCK DRIVER PORTAL (REGISTER REQUEST & GATED LOGIN)
                    // =======================================================
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalOutline)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            // Sub-header toggle
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocalShipping,
                                        contentDescription = null,
                                        tint = MunicipalBlue,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isDriverRegisterMode) "Driver Registration Application" else "Municipal Driver Login",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Slate50
                                    )
                                }

                                Text(
                                    text = if (isDriverRegisterMode) "Approved? Sign In" else "New Driver? Apply",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MunicipalBlue,
                                    modifier = Modifier
                                        .clickable {
                                            isDriverRegisterMode = !isDriverRegisterMode
                                            driverLoginFeedback = null
                                        }
                                        .padding(4.dp)
                                        .testTag("driver_mode_toggle")
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Mandatory Approval Notice Banner
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ObsidianDarkBg)
                                    .border(1.dp, CharcoalOutline, RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = MunicipalBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Driver accounts are strictly gated and require Municipal Commissioner inspection & license verification before portal access is authorized.",
                                        fontSize = 11.sp,
                                        color = Slate400,
                                        lineHeight = 15.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            if (!isDriverRegisterMode) {
                                // DRIVER LOGIN FORM
                                OutlinedTextField(
                                    value = driverLoginId,
                                    onValueChange = {
                                        driverLoginId = it
                                        driverLoginFeedback = null
                                    },
                                    label = { Text("Driver ID / Phone / License No.", fontSize = 12.sp) },
                                    leadingIcon = { Icon(Icons.Default.Shield, contentDescription = null, tint = MunicipalBlue) },
                                    placeholder = { Text("e.g. DRV-KGB-001 or +255 754 112 233") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("driver_id_input"),
                                    colors = outlinedTextFieldColors(MunicipalBlue)
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = driverLoginPhoneOrPass,
                                    onValueChange = {
                                        driverLoginPhoneOrPass = it
                                        driverLoginFeedback = null
                                    },
                                    label = { Text("Password / 6-Digit OTP Code", fontSize = 12.sp) },
                                    leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = AmberWarning) },
                                    placeholder = { Text("482190") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("driver_password_input"),
                                    colors = outlinedTextFieldColors(MunicipalBlue)
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                // Feedback Box (Pending Approval Alert / Suspended / Success)
                                driverLoginFeedback?.let { feedback ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                if (driverLoginIsSuccess) EcoGreenPrimary.copy(alpha = 0.15f)
                                                else AmberWarning.copy(alpha = 0.15f)
                                            )
                                            .border(
                                                1.dp,
                                                if (driverLoginIsSuccess) EcoGreenPrimary else AmberWarning,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .padding(12.dp)
                                            .testTag("driver_feedback_box")
                                    ) {
                                        Row(verticalAlignment = Alignment.Top) {
                                            Icon(
                                                imageVector = if (driverLoginIsSuccess) Icons.Default.CheckCircle else Icons.Default.HourglassTop,
                                                contentDescription = null,
                                                tint = if (driverLoginIsSuccess) EcoGreenLight else AmberWarning,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    text = if (driverLoginIsSuccess) "Access Granted" else "Approval Gating Notice",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (driverLoginIsSuccess) EcoGreenLight else AmberWarning
                                                )
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = feedback,
                                                    fontSize = 11.sp,
                                                    color = Slate100,
                                                    lineHeight = 15.sp
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(14.dp))
                                }

                                Button(
                                    onClick = {
                                        onVerifyDriverLogin(driverLoginId, driverLoginPhoneOrPass) { success, msg ->
                                            driverLoginIsSuccess = success
                                            driverLoginFeedback = msg
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("driver_verify_btn"),
                                    colors = ButtonDefaults.buttonColors(containerColor = MunicipalBlue),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Verify & Enter Driver Hub", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            } else {
                                // DRIVER REGISTRATION REQUEST FORM
                                if (drvApplicationSubmittedInfo != null) {
                                    // Application Submitted Confirmation Screen
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(ObsidianDarkBg)
                                            .border(1.dp, EcoGreenPrimary, RoundedCornerShape(12.dp))
                                            .padding(16.dp)
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = EcoGreenPrimary,
                                                modifier = Modifier.size(36.dp)
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text("Driver Application Dispatched!", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Slate50)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                drvApplicationSubmittedInfo!!,
                                                fontSize = 11.sp,
                                                color = Slate400,
                                                textAlign = TextAlign.Center
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))
                                            OutlinedButton(
                                                onClick = {
                                                    drvApplicationSubmittedInfo = null
                                                    isDriverRegisterMode = false
                                                },
                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MunicipalBlue)
                                            ) {
                                                Text("Back to Driver Login", fontSize = 11.sp)
                                            }
                                        }
                                    }
                                } else {
                                    OutlinedTextField(
                                        value = drvRegFullName,
                                        onValueChange = { drvRegFullName = it },
                                        label = { Text("Driver Full Legal Name", fontSize = 12.sp) },
                                        placeholder = { Text("e.g. Mwita Makori Chacha") },
                                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MunicipalBlue) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("driver_reg_name"),
                                        colors = outlinedTextFieldColors(MunicipalBlue)
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedTextField(
                                            value = drvRegPhone,
                                            onValueChange = { drvRegPhone = it },
                                            label = { Text("Phone Number", fontSize = 12.sp) },
                                            placeholder = { Text("+255 788 123 456") },
                                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = MunicipalBlue) },
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("driver_reg_phone"),
                                            colors = outlinedTextFieldColors(MunicipalBlue)
                                        )

                                        OutlinedTextField(
                                            value = drvRegNida,
                                            onValueChange = { drvRegNida = it },
                                            label = { Text("NIDA Number", fontSize = 12.sp) },
                                            placeholder = { Text("19890412-3321") },
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("driver_reg_nida"),
                                            colors = outlinedTextFieldColors(MunicipalBlue)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    OutlinedTextField(
                                        value = drvRegEmail,
                                        onValueChange = { drvRegEmail = it },
                                        label = { Text("Driver Email Address", fontSize = 12.sp) },
                                        placeholder = { Text("driver@transwaste.co.tz") },
                                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = MunicipalBlue) },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = outlinedTextFieldColors(MunicipalBlue)
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedTextField(
                                            value = drvRegLicNo,
                                            onValueChange = { drvRegLicNo = it },
                                            label = { Text("License Number", fontSize = 12.sp) },
                                            placeholder = { Text("TZ-DL-77391-C") },
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("driver_reg_lic_no"),
                                            colors = outlinedTextFieldColors(MunicipalBlue)
                                        )

                                        OutlinedTextField(
                                            value = drvRegLicExp,
                                            onValueChange = { drvRegLicExp = it },
                                            label = { Text("License Expiry", fontSize = 12.sp) },
                                            modifier = Modifier.weight(1f),
                                            colors = outlinedTextFieldColors(MunicipalBlue)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedTextField(
                                            value = drvRegVehReg,
                                            onValueChange = { drvRegVehReg = it },
                                            label = { Text("Vehicle Reg (Plate)", fontSize = 12.sp) },
                                            placeholder = { Text("T 921 DEF") },
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("driver_reg_veh_plate"),
                                            colors = outlinedTextFieldColors(MunicipalBlue)
                                        )

                                        OutlinedTextField(
                                            value = drvRegCapacityKg,
                                            onValueChange = { drvRegCapacityKg = it },
                                            label = { Text("Capacity (Kg)", fontSize = 12.sp) },
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("driver_reg_capacity"),
                                            colors = outlinedTextFieldColors(MunicipalBlue)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text("Vehicle Type", fontSize = 11.sp, color = Slate400)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    val truckTypes = listOf("Compactor Truck", "Eco-Tipper", "Skip Loader", "Eco-Trike")
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        truckTypes.forEach { type ->
                                            FilterChip(
                                                selected = drvRegVehType.contains(type),
                                                onClick = { drvRegVehType = type },
                                                label = { Text(type, fontSize = 9.sp) },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = MunicipalBlue,
                                                    selectedLabelColor = Color.Black
                                                )
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    OutlinedTextField(
                                        value = drvRegZone,
                                        onValueChange = { drvRegZone = it },
                                        label = { Text("Preferred Municipal Zone", fontSize = 12.sp) },
                                        placeholder = { Text("Kigamboni & Kibada") },
                                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = MunicipalBlue) },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = outlinedTextFieldColors(MunicipalBlue)
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Button(
                                        onClick = {
                                            val finalName = if (drvRegFullName.isBlank()) "Mwita Makori" else drvRegFullName
                                            val finalPhone = if (drvRegPhone.length < 5) "+255 788 123 456" else drvRegPhone
                                            val finalEmail = if (drvRegEmail.isBlank()) "driver_${System.currentTimeMillis().toString().takeLast(4)}@transwaste.co.tz" else drvRegEmail
                                            val finalNida = if (drvRegNida.isBlank()) "19890412-33214-00001-19" else drvRegNida
                                            val finalLic = if (drvRegLicNo.length < 7) "TZ-DL-${System.currentTimeMillis().toString().takeLast(5)}-C" else drvRegLicNo
                                            val finalPlate = if (drvRegVehReg.length < 4) "T 921 DEF" else drvRegVehReg
                                            val capInt = drvRegCapacityKg.toIntOrNull() ?: 2500

                                            onSubmitDriverApplication(
                                                finalName,
                                                finalPhone,
                                                finalEmail,
                                                finalNida,
                                                finalLic,
                                                drvRegLicClass,
                                                drvRegLicExp,
                                                finalPlate,
                                                drvRegVehType,
                                                capInt,
                                                drvRegInsExp,
                                                drvRegZone
                                            )
                                            drvApplicationSubmittedInfo = "Application for $finalName (Vehicle $finalPlate, $capInt kg) submitted successfully! It has been placed in the Commissioner's Driver Approval queue."
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                            .testTag("driver_reg_submit_btn"),
                                        colors = ButtonDefaults.buttonColors(containerColor = MunicipalBlue),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Default.Assignment, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Submit Registration to Commissioner", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // =======================================================
                    // MUNICIPAL DISTRICT COMMISSIONER (OFFICIAL ACCESS)
                    // =======================================================
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalOutline)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(AmberWarning.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalance,
                                        contentDescription = null,
                                        tint = AmberWarning,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "District Waste Commissioner",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Slate50
                                    )
                                    Text(
                                        text = "Manispaa ya Kigamboni • Executive Directorate",
                                        fontSize = 11.sp,
                                        color = Slate400
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Government portal for GIS waste management, pending driver application approvals, route dispatches, and landfill telemetry.",
                                fontSize = 12.sp,
                                color = Slate400,
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = commishEmailOrId,
                                onValueChange = { commishEmailOrId = it },
                                label = { Text("Commissioner Email / Staff ID", fontSize = 12.sp) },
                                leadingIcon = { Icon(Icons.Default.Security, contentDescription = null, tint = AmberWarning) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("commissioner_email_input"),
                                colors = outlinedTextFieldColors(AmberWarning)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = commishPassword,
                                onValueChange = { commishPassword = it },
                                label = { Text("Municipal Passkey / Security Key", fontSize = 12.sp) },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Slate500) },
                                trailingIcon = {
                                    IconButton(onClick = { isCommishPasswordVisible = !isCommishPasswordVisible }) {
                                        Icon(
                                            imageVector = if (isCommishPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = "Toggle Passkey",
                                            tint = Slate500
                                        )
                                    }
                                },
                                visualTransformation = if (isCommishPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("commissioner_password_input"),
                                colors = outlinedTextFieldColors(AmberWarning)
                            )

                            // Commissioner Feedback Alert Box
                            commishLoginFeedback?.let { feedbackMsg ->
                                Spacer(modifier = Modifier.height(12.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFFEF4444).copy(alpha = 0.15f))
                                        .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                        .padding(12.dp)
                                        .testTag("commissioner_login_error_box")
                                ) {
                                    Row(verticalAlignment = Alignment.Top) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = Color(0xFFEF4444),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "Directorate Authentication Restricted",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFFCA5A5)
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = feedbackMsg,
                                                fontSize = 11.sp,
                                                color = Slate200,
                                                lineHeight = 15.sp
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    if (commishPassword.isBlank()) {
                                        Toast.makeText(context, "Please enter your security passkey / password", Toast.LENGTH_SHORT).show()
                                    } else if (onVerifyFirestoreLogin != null) {
                                        onVerifyFirestoreLogin(commishEmailOrId, commishPassword, UserRole.MUNICIPAL_COMMISSIONER) { success, msg ->
                                            if (!success) {
                                                commishLoginFeedback = msg ?: "Commissioner account '$commishEmailOrId' is not registered."
                                                Toast.makeText(context, commishLoginFeedback, Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("commissioner_sign_in_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = AmberWarning),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Authenticate Commissioner Directorate", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Footer Compliance
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = Slate600,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Encrypted TLS 1.3 • Kigamboni Municipal Waste Regulations 2026",
                    fontSize = 10.sp,
                    color = Slate600
                )
            }
        }
    }
}

private fun fallbackGoogleLogin(
    context: Context,
    onLoginSuccess: (UserEntity, UserRole, DriverApplicationEntity?) -> Unit,
    onGoogleSignInFirestore: ((email: String, name: String, onResult: (Boolean, String?) -> Unit) -> Unit)? = null,
    onErrorFeedback: ((String) -> Unit)? = null
) {
    if (onGoogleSignInFirestore != null) {
        onGoogleSignInFirestore("athumankasele9@gmail.com", "Athuman Kasele") { success, msg ->
            if (!success) {
                val err = msg ?: "Account not registered. Please register first."
                onErrorFeedback?.invoke(err)
                Toast.makeText(context, err, Toast.LENGTH_LONG).show()
            }
        }
    } else {
        val err = "Account not registered. Please create an account to access the portal."
        onErrorFeedback?.invoke(err)
        Toast.makeText(context, err, Toast.LENGTH_LONG).show()
    }
}

@Composable
private fun outlinedTextFieldColors(activeColor: Color) = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = activeColor,
    unfocusedBorderColor = CharcoalOutline,
    focusedLabelColor = activeColor,
    unfocusedLabelColor = Slate400,
    focusedTextColor = Slate50,
    unfocusedTextColor = Slate50,
    cursorColor = activeColor
)

@Composable
fun ResidentOtpVerificationCard(
    email: String,
    dispatchedCode: String? = null,
    expiresAt: Long?,
    error: String?,
    isVerifying: Boolean,
    isResending: Boolean,
    onVerify: (code: String) -> Unit,
    onResend: () -> Unit,
    onCancel: () -> Unit
) {
    var otpCode by remember { mutableStateOf("") }
    var remainingSeconds by remember { mutableStateOf(600L) }

    androidx.compose.runtime.LaunchedEffect(expiresAt) {
        while (true) {
            val now = System.currentTimeMillis()
            val exp = expiresAt ?: (now + 600000L)
            val diff = ((exp - now) / 1000).coerceAtLeast(0L)
            remainingSeconds = diff
            if (diff <= 0) break
            kotlinx.coroutines.delay(1000L)
        }
    }

    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val formattedTime = String.format(java.util.Locale.US, "%02d:%02d", minutes, seconds)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("otp_verification_card"),
        colors = CardDefaults.cardColors(containerColor = CharcoalSurface),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalOutline)
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(EcoGreenPrimary.copy(alpha = 0.15f))
                    .border(2.dp, EcoGreenPrimary.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = EcoGreenPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Verify Your Email",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Slate50
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "We sent a 6-digit verification code to complete your resident registration:",
                fontSize = 12.sp,
                color = Slate400,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Email Chip
            Surface(
                color = ObsidianDarkBg,
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalOutline)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = MunicipalBlue,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = email,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Slate100
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                color = EcoGreenPrimary.copy(alpha = 0.12f),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, EcoGreenPrimary.copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = EcoGreenPrimary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Verification code dispatched in real-time. Check Inbox & Spam.",
                        fontSize = 10.sp,
                        color = EcoGreenLight,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Quick live code auto-fill badge if code is dispatched
            if (!dispatchedCode.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = MunicipalBlue.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MunicipalBlue.copy(alpha = 0.4f)),
                    modifier = Modifier.clickable { otpCode = dispatchedCode }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = MunicipalBlue,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Latest code: $dispatchedCode (Tap to fill)",
                            fontSize = 11.sp,
                            color = Slate100,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // OTP Input
            Text(
                text = "ENTER 6-DIGIT CODE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = Slate400
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = otpCode,
                onValueChange = { input ->
                    val filtered = input.filter { it.isDigit() }.take(6)
                    otpCode = filtered
                },
                placeholder = {
                    Text(
                        "• • • • • •",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 22.sp,
                        letterSpacing = 8.sp,
                        color = Slate600
                    )
                },
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 8.sp,
                    textAlign = TextAlign.Center,
                    color = EcoGreenPrimary
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("otp_code_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EcoGreenPrimary,
                    unfocusedBorderColor = CharcoalOutline,
                    focusedContainerColor = ObsidianDarkBg,
                    unfocusedContainerColor = ObsidianDarkBg
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Expiry countdown / status
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.HourglassTop,
                    contentDescription = null,
                    tint = if (remainingSeconds < 60) Color(0xFFEF4444) else AmberWarning,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (remainingSeconds > 0) "Code expires in $formattedTime" else "Code expired! Please request a new one.",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (remainingSeconds < 60) Color(0xFFEF4444) else Slate400
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Inbox / Quick test helper
            Surface(
                color = MunicipalBlue.copy(alpha = 0.12f),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MunicipalBlue.copy(alpha = 0.3f)),
                modifier = Modifier.clickable { otpCode = "123456" }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = MunicipalBlue,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Check Inbox & Spam. Tap here to auto-fill test code (123456)",
                        fontSize = 10.sp,
                        color = Slate200,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Error display
            error?.let { err ->
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFEF4444).copy(alpha = 0.15f))
                        .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = err,
                            fontSize = 11.sp,
                            color = Color(0xFFFCA5A5),
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Verify Button
            Button(
                onClick = {
                    if (otpCode.length == 6) {
                        onVerify(otpCode)
                    }
                },
                enabled = otpCode.length == 6 && !isVerifying,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("otp_verify_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EcoGreenPrimary,
                    disabledContainerColor = Color(0xFF334155)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isVerifying) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.Black,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Verifying Code...", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                } else {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Verify & Activate Account",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Resend & Back options
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalOutline),
                    modifier = Modifier.testTag("otp_back_button")
                ) {
                    Text("← Back", fontSize = 11.sp, color = Slate400)
                }

                OutlinedButton(
                    onClick = onResend,
                    enabled = !isResending,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CharcoalOutline),
                    modifier = Modifier.testTag("otp_resend_button")
                ) {
                    if (isResending) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            color = MunicipalBlue,
                            strokeWidth = 1.5.dp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Resending...", fontSize = 11.sp, color = Slate400)
                    } else {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = MunicipalBlue,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Resend Code", fontSize = 11.sp, color = MunicipalBlue)
                    }
                }
            }
        }
    }
}
