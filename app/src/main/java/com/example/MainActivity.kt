package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CollectionRequestEntity
import com.example.data.model.CollectionStatus
import com.example.data.model.DriverApplicationStatus
import com.example.data.model.InAppNotification
import com.example.data.model.UserRole
import com.example.ui.WasteViewModel
import com.example.ui.components.PickupCoordinationChatDialog
import com.example.ui.components.PortalSwitcherDialog
import com.example.ui.components.ResidentNotificationAlertBanner
import com.example.ui.navigation.BusinessNavTab
import com.example.ui.navigation.CommissionerNavTab
import com.example.ui.navigation.DisposalNavTab
import com.example.ui.navigation.DriverNavTab
import com.example.ui.navigation.ResidentNavTab
import com.example.ui.screens.AuthDialog
import com.example.ui.screens.BusinessPortalScreen
import com.example.ui.screens.BusinessRegistrationDialog
import com.example.ui.screens.ChainOfCustodyDialog
import com.example.ui.screens.CommissionerPortalScreen
import com.example.ui.screens.ComplaintDialog
import com.example.ui.screens.DisposalFacilityScreen
import com.example.ui.screens.DriverApplicationDialog
import com.example.ui.screens.DriverPortalScreen
import com.example.ui.screens.DriverVerificationDialog
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MunicipalReportsDialog
import com.example.ui.screens.NotificationCenterDialog
import com.example.ui.screens.OverflowBinReportScreen
import com.example.ui.screens.RatingDialog
import com.example.ui.screens.ReceiptDialog
import com.example.ui.screens.RequestCollectionDialog
import com.example.ui.screens.ResidentPortalScreen
import com.example.ui.screens.ScheduleCollectionPlanDialog
import com.example.ui.screens.SmartAssignmentDialog
import com.example.ui.screens.SuperAdminScreen
import com.example.ui.screens.ThemeSettingsDialog
import com.example.ui.screens.UserProfileDialog
import com.example.ui.theme.AppThemePalette
import com.example.ui.theme.CharcoalOutline
import com.example.ui.theme.CharcoalSurface
import com.example.ui.theme.EcoGreenPrimary
import com.example.ui.theme.FontSizeScale
import com.example.ui.theme.MunicipalBlue
import com.example.ui.theme.ObsidianDarkBg
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate600
import com.example.ui.theme.SmartWasteTheme
import com.example.ui.theme.ThemeMode

class MainActivity : ComponentActivity() {
  private val viewModel: WasteViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    com.example.fcm.SmartWasteNotificationManager.createNotificationChannels(this)
    com.example.fcm.FcmTokenManager.fetchAndRegisterToken(this)
    setContent {
      val themeMode by viewModel.themeMode.collectAsState()
      val appThemePalette by viewModel.appThemePalette.collectAsState()
      val fontSizeScale by viewModel.fontSizeScale.collectAsState()

      SmartWasteTheme(
        themeMode = themeMode,
        palette = appThemePalette,
        fontSizeScale = fontSizeScale
      ) {
        SmartWasteApp(viewModel = viewModel)
      }
    }
  }
}

data class NavItem(
  val role: UserRole,
  val label: String,
  val icon: ImageVector,
  val badgeCount: Int
)

@Composable
fun SpeedDialActionItem(
  label: String,
  icon: ImageVector,
  iconColor: Color,
  badge: String? = null,
  onClick: () -> Unit
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    modifier = Modifier.clickable(onClick = onClick)
  ) {
    Surface(
      shape = RoundedCornerShape(8.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 4.dp,
      shadowElevation = 4.dp,
      border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Text(
          text = label,
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.onSurface
        )
        if (badge != null) {
          Box(
            modifier = Modifier
              .clip(CircleShape)
              .background(MunicipalBlue)
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = badge,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = Color.Black
            )
          }
        }
      }
    }

    SmallFloatingActionButton(
      onClick = onClick,
      containerColor = MaterialTheme.colorScheme.surface,
      contentColor = iconColor,
      shape = CircleShape,
      elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp),
      modifier = Modifier.size(42.dp)
    ) {
      Icon(
        imageVector = icon,
        contentDescription = label,
        tint = iconColor,
        modifier = Modifier.size(20.dp)
      )
    }
  }
}

@Composable
fun SmartWasteApp(viewModel: WasteViewModel) {
  val context = androidx.compose.ui.platform.LocalContext.current
  if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
      contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission(),
      onResult = { isGranted ->
        android.util.Log.d("MainActivity", "POST_NOTIFICATIONS granted: $isGranted")
      }
    )
    LaunchedEffect(Unit) {
      if (androidx.core.content.ContextCompat.checkSelfPermission(
          context,
          android.Manifest.permission.POST_NOTIFICATIONS
        ) != android.content.pm.PackageManager.PERMISSION_GRANTED
      ) {
        permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
      }
    }
  }

  val isAuthenticated by viewModel.isAuthenticated.collectAsState()
  val isAuthenticating by viewModel.isAuthenticating.collectAsState()
  val authVerificationStatus by viewModel.authVerificationStatus.collectAsState()
  val currentRole by viewModel.currentUserRole.collectAsState()
  val residentUser by viewModel.currentResidentUser.collectAsState()
  val allRequests by viewModel.allRequests.collectAsState()
  val allApplications by viewModel.allApplications.collectAsState()
  val approvedDrivers by viewModel.approvedDrivers.collectAsState()
  val allComplaints by viewModel.allComplaints.collectAsState()
  val allAuditLogs by viewModel.allAuditLogs.collectAsState()
  val allFacilities by viewModel.allFacilities.collectAsState()
  val allUsers by viewModel.allUsers.collectAsState()

  val showOtpScreen by viewModel.showOtpScreen.collectAsState()
  val pendingRegistrationEmail by viewModel.pendingRegistrationEmail.collectAsState()
  val latestDispatchedOtpCode by viewModel.latestDispatchedOtpCode.collectAsState()
  val otpExpiresAt by viewModel.otpExpiresAt.collectAsState()
  val otpError by viewModel.otpError.collectAsState()
  val isVerifyingOtp by viewModel.isVerifyingOtp.collectAsState()
  val isResendingOtp by viewModel.isResendingOtp.collectAsState()

  val themeMode by viewModel.themeMode.collectAsState()
  val appThemePalette by viewModel.appThemePalette.collectAsState()
  val fontSizeScale by viewModel.fontSizeScale.collectAsState()
  val showThemeSettingsDialog by viewModel.showThemeSettingsDialog.collectAsState()

  if (!isAuthenticated) {
    LoginScreen(
      onLoginSuccess = { user, role, driverEntity ->
        viewModel.login(user, role, driverEntity)
      },
      onRegisterResident = { name, email, phone, password, nida, addr, ward, dist, isComm, bizName ->
        viewModel.registerResidentUser(name, email, phone, password, nida, addr, ward, dist, isComm, bizName)
      },
      onInitiateResidentRegistration = { name, email, phone, password, nida, addr, ward, dist, isComm, bizName, callback ->
        viewModel.initiateResidentRegistration(name, email, phone, password, nida, addr, ward, dist, isComm, bizName, callback)
      },
      showOtpScreen = showOtpScreen,
      pendingRegistrationEmail = pendingRegistrationEmail,
      latestDispatchedOtpCode = latestDispatchedOtpCode,
      otpExpiresAt = otpExpiresAt,
      otpError = otpError,
      isVerifyingOtp = isVerifyingOtp,
      isResendingOtp = isResendingOtp,
      onVerifyResidentOtp = { email, code, onSuccess, onError ->
        viewModel.verifyResidentOtp(email, code, onSuccess, onError)
      },
      onResendResidentOtp = { email, callback ->
        viewModel.resendResidentOtp(email, callback)
      },
      onCancelOtpVerification = { viewModel.cancelOtpVerification() },
      onSubmitDriverApplication = { name, phone, email, nida, licNo, licClass, licExp, vehReg, vehType, cap, insExp, zone ->
        viewModel.submitDriverApplication(name, phone, email, nida, licNo, licClass, licExp, vehReg, vehType, cap, insExp, zone)
      },
      onVerifyDriverLogin = { identifier, secret, callback ->
        viewModel.verifyDriverLogin(identifier, secret, callback)
      },
      onRegisterCommercial = { bizName, bizType, tin, contact, phone, email, password, addr, ward, dist, hours, notes, bins, binType, lat, lng ->
        viewModel.registerCommercialBusiness(bizName, bizType, tin, contact, phone, email, password, addr, ward, dist, hours, "", notes, bins, binType, lat, lng)
      },
      onVerifyFirestoreLogin = { identifier, password, requestedRole, callback ->
        viewModel.loginWithFirestoreRoleCheck(identifier, password, requestedRole, callback)
      },
      onGoogleSignInFirestore = { email, name, callback ->
        viewModel.loginWithGoogleFirestore(email, name, callback)
      },
      isAuthenticating = isAuthenticating,
      authVerificationStatus = authVerificationStatus,
      driverApplications = allApplications,
      onToggleThemeMode = { viewModel.toggleThemeMode() },
      onOpenThemeSettings = { viewModel.openThemeSettingsDialog() },
      isDarkMode = themeMode == ThemeMode.DARK
    )

    if (showThemeSettingsDialog) {
      ThemeSettingsDialog(
        currentThemeMode = themeMode,
        currentPalette = appThemePalette,
        currentFontSizeScale = fontSizeScale,
        onSelectThemeMode = { viewModel.setThemeMode(it) },
        onSelectPalette = { viewModel.setAppThemePalette(it) },
        onSelectFontSizeScale = { viewModel.setFontSizeScale(it) },
        onDismiss = { viewModel.closeThemeSettingsDialog() }
      )
    }
    return
  }

  val currentDriverEntity by viewModel.currentDriver.collectAsState()

  // Dialog States
  val showNewRequestDialog by viewModel.showNewRequestDialog.collectAsState()
  val showDriverApplicationDialog by viewModel.showDriverApplicationDialog.collectAsState()
  val selectedRequestForDispatch by viewModel.selectedRequestForDispatch.collectAsState()
  val driverRecommendations by viewModel.driverRecommendations.collectAsState()
  val selectedAppForReview by viewModel.selectedApplicationForReview.collectAsState()
  val selectedReceipt by viewModel.selectedReceipt.collectAsState()
  val selectedRequestForRating by viewModel.selectedRequestForRating.collectAsState()
  val selectedRequestForComplaint by viewModel.selectedRequestForComplaint.collectAsState()
  val showOverflowBinReportScreen by viewModel.showOverflowBinReportScreen.collectAsState()
  val feedbackMessage by viewModel.feedbackSnackbarMessage.collectAsState()
  val activeChatRequestId by viewModel.activeChatRequestId.collectAsState()
  val activeChatRequest by viewModel.activeChatRequest.collectAsState()
  val activeChatMessages by viewModel.activeChatMessages.collectAsState()
  val activeResidentAlert by viewModel.activeResidentAlert.collectAsState()
  val liveNotifications by viewModel.notifications.collectAsState()
  val unreadNotificationCount by viewModel.unreadNotificationCount.collectAsState()
  val allScheduledPlans by viewModel.allScheduledPlans.collectAsState()
  val showSchedulePlanDialog by viewModel.showSchedulePlanDialog.collectAsState()
  val showBusinessRegistrationDialog by viewModel.showBusinessRegistrationDialog.collectAsState()

    // Additional Municipal Dialog States
  var showAuthDialog by remember { mutableStateOf(false) }
  var showPortalSwitcherDialog by remember { mutableStateOf(false) }
  var showNotificationDialog by remember { mutableStateOf(false) }
  var showReportsDialog by remember { mutableStateOf(false) }
  var selectedRequestForChainOfCustody by remember { mutableStateOf<CollectionRequestEntity?>(null) }
  var isSpeedDialOpen by remember { mutableStateOf(false) }

  // Persona-specific Navigation Tab States
  var residentTab by remember { mutableStateOf(ResidentNavTab.HOME) }
  var businessTab by remember { mutableStateOf(BusinessNavTab.OVERVIEW) }
  var driverTab by remember { mutableStateOf(DriverNavTab.MAP) }
  var commissionerTab by remember { mutableStateOf(CommissionerNavTab.OVERVIEW) }
  var disposalTab by remember { mutableStateOf(DisposalNavTab.WEIGHBRIDGE) }

  val snackbarHostState = remember { SnackbarHostState() }

  LaunchedEffect(feedbackMessage) {
    feedbackMessage?.let { msg ->
      snackbarHostState.showSnackbar(msg)
      viewModel.clearSnackbar()
    }
  }

  val pendingAppsCount = allApplications.count { it.status == DriverApplicationStatus.SUBMITTED }
  val activeDispatchesCount = allRequests.count {
    it.collectionStatus in listOf(CollectionStatus.REQUESTED, CollectionStatus.PAID, CollectionStatus.WAITING_ASSIGNMENT)
  }
  val residentUserRequests = remember(allRequests, residentUser) {
    allRequests.filter {
      it.residentId == residentUser.id ||
      (it.residentPhone.isNotBlank() && it.residentPhone == residentUser.phone) ||
      (it.residentName.isNotBlank() && it.residentName.equals(residentUser.fullName, ignoreCase = true) && !it.isCommercialRequest)
    }
  }
  val residentActiveCount = residentUserRequests.count {
    it.collectionStatus !in listOf(CollectionStatus.DISPOSED, CollectionStatus.CANCELLED)
  }

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background),
    containerColor = MaterialTheme.colorScheme.background,
    snackbarHost = {
      SnackbarHost(hostState = snackbarHostState) { data ->
        Snackbar(
          snackbarData = data,
          containerColor = MaterialTheme.colorScheme.surface,
          contentColor = MaterialTheme.colorScheme.onSurface,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
        )
      }
    },
    topBar = {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable { showPortalSwitcherDialog = true }
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .testTag("top_bar_portal_identity_badge")
            ) {
              Box(
                modifier = Modifier
                  .size(38.dp)
                  .clip(RoundedCornerShape(10.dp))
                  .background(
                    when (currentRole) {
                      UserRole.RESIDENT -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                      UserRole.BUSINESS -> MunicipalBlue.copy(alpha = 0.15f)
                      UserRole.DRIVER -> Color(0xFF10B981).copy(alpha = 0.15f)
                      UserRole.MUNICIPAL_COMMISSIONER -> Color(0xFFF59E0B).copy(alpha = 0.15f)
                      UserRole.DISPOSAL_OFFICER -> Color(0xFF8B5CF6).copy(alpha = 0.15f)
                    }
                  )
                  .border(
                    1.dp,
                    when (currentRole) {
                      UserRole.RESIDENT -> MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                      UserRole.BUSINESS -> MunicipalBlue.copy(alpha = 0.4f)
                      UserRole.DRIVER -> Color(0xFF10B981).copy(alpha = 0.4f)
                      UserRole.MUNICIPAL_COMMISSIONER -> Color(0xFFF59E0B).copy(alpha = 0.4f)
                      UserRole.DISPOSAL_OFFICER -> Color(0xFF8B5CF6).copy(alpha = 0.4f)
                    },
                    RoundedCornerShape(10.dp)
                  ),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = when (currentRole) {
                    UserRole.RESIDENT -> Icons.Default.Home
                    UserRole.BUSINESS -> Icons.Default.Business
                    UserRole.DRIVER -> Icons.Default.LocalShipping
                    UserRole.MUNICIPAL_COMMISSIONER -> Icons.Default.AccountBalance
                    UserRole.DISPOSAL_OFFICER -> Icons.Default.Recycling
                  },
                  contentDescription = "Portal Icon",
                  tint = when (currentRole) {
                    UserRole.RESIDENT -> MaterialTheme.colorScheme.primary
                    UserRole.BUSINESS -> MunicipalBlue
                    UserRole.DRIVER -> Color(0xFF10B981)
                    UserRole.MUNICIPAL_COMMISSIONER -> Color(0xFFF59E0B)
                    UserRole.DISPOSAL_OFFICER -> Color(0xFF8B5CF6)
                  },
                  modifier = Modifier.size(20.dp)
                )
              }
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = when (currentRole) {
                      UserRole.RESIDENT -> "Resident Portal"
                      UserRole.BUSINESS -> "Commercial Enterprise"
                      UserRole.DRIVER -> "Driver Console"
                      UserRole.MUNICIPAL_COMMISSIONER -> "Commissioner Directorate"
                      UserRole.DISPOSAL_OFFICER -> "Disposal & Landfill Hub"
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Switch Portal",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                  )
                }
                Text(
                  text = when (currentRole) {
                    UserRole.RESIDENT -> "${residentUser.fullName} • ${residentUser.ward}"
                    UserRole.BUSINESS -> "${residentUser.businessName ?: residentUser.fullName} • ${residentUser.ward}"
                    UserRole.DRIVER -> "${currentDriverEntity?.fullName ?: "Approved Driver"} • ${currentDriverEntity?.vehicleRegNumber ?: "Truck"}"
                    UserRole.MUNICIPAL_COMMISSIONER -> "Hon. Eng. David Malecela • Kigamboni"
                    UserRole.DISPOSAL_OFFICER -> "Grace Temba • Pugu Facility"
                  },
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            // Action Buttons (Dark/Light Toggle, Theme Palette, Notifications, Reports, Profile, Logout)
            Row(verticalAlignment = Alignment.CenterVertically) {
              // Quick 1-Tap Dark / Light Mode Toggle
              IconButton(
                onClick = { viewModel.toggleThemeMode() },
                modifier = Modifier.testTag("quick_theme_toggle_button")
              ) {
                Icon(
                  imageVector = if (themeMode == ThemeMode.DARK) Icons.Default.LightMode else Icons.Default.DarkMode,
                  contentDescription = "Toggle Dark or Light Mode",
                  tint = if (themeMode == ThemeMode.DARK) Color(0xFFFBBF24) else Color(0xFF475569),
                  modifier = Modifier.size(20.dp)
                )
              }

              // Display, Font Size & Theme Customization Dialog
              IconButton(
                onClick = { viewModel.openThemeSettingsDialog() },
                modifier = Modifier.testTag("theme_settings_button")
              ) {
                Icon(
                  imageVector = Icons.Default.Palette,
                  contentDescription = "Theme & Display Settings",
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(20.dp)
                )
              }

              // Notifications Bell with Pulse Badge
              IconButton(
                onClick = { showNotificationDialog = true },
                modifier = Modifier.testTag("top_bar_notifications_button")
              ) {
                BadgedBox(
                  badge = {
                    if (unreadNotificationCount > 0) {
                      Badge(
                        containerColor = MunicipalBlue,
                        contentColor = Color.Black
                      ) {
                        Text(
                          text = if (unreadNotificationCount > 99) "99+" else "$unreadNotificationCount",
                          fontSize = 10.sp,
                          fontWeight = FontWeight.Bold
                        )
                      }
                    }
                  }
                ) {
                  Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                  )
                }
              }

              // Municipal Reports (for Commissioner)
              if (currentRole == UserRole.MUNICIPAL_COMMISSIONER) {
                IconButton(onClick = { showReportsDialog = true }) {
                  Icon(
                    imageVector = Icons.Default.Assessment,
                    contentDescription = "Municipal Reports",
                    tint = MunicipalBlue,
                    modifier = Modifier.size(20.dp)
                  )
                }
              }

              // Profile & Identity Modal
              IconButton(onClick = { showAuthDialog = true }) {
                Icon(
                  imageVector = Icons.Default.Person,
                  contentDescription = "Identity & Profile",
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(20.dp)
                )
              }

              Spacer(modifier = Modifier.width(2.dp))

              // Secure Sign Out Button (Returns to authentication gateway)
              OutlinedButton(
                onClick = { viewModel.logout() },
                modifier = Modifier
                  .height(34.dp)
                  .testTag("sign_out_button"),
                colors = ButtonDefaults.outlinedButtonColors(
                  containerColor = MaterialTheme.colorScheme.surfaceVariant,
                  contentColor = Color(0xFFEF4444)
                ),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Logout,
                  contentDescription = "Sign Out",
                  tint = Color(0xFFEF4444),
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "Sign Out",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFFEF4444)
                )
              }
            }
          }
        }
      }
    },
    floatingActionButton = {
      // Speed Dial Expandable Quick-Action Floating Navigation Menu
      Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        AnimatedVisibility(
          visible = isSpeedDialOpen,
          enter = fadeIn(animationSpec = tween(150)) + slideInVertically(initialOffsetY = { it / 2 }, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + scaleIn(),
          exit = fadeOut(animationSpec = tween(120)) + slideOutVertically(targetOffsetY = { it / 2 }) + scaleOut()
        ) {
          Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // Action 1: Request Pickup
            SpeedDialActionItem(
              label = "Request Waste Pickup",
              icon = Icons.Default.LocalShipping,
              iconColor = EcoGreenPrimary,
              onClick = {
                isSpeedDialOpen = false
                viewModel.openNewRequestDialog()
              }
            )

            // Action 2: Schedule Contract
            SpeedDialActionItem(
              label = "Schedule Collection Contract",
              icon = Icons.Default.EventRepeat,
              iconColor = Color(0xFFF59E0B),
              onClick = {
                isSpeedDialOpen = false
                viewModel.openSchedulePlanDialog()
              }
            )

            // Action 3: Commercial Business Setup
            SpeedDialActionItem(
              label = "Commercial Business Portal",
              icon = Icons.Default.Business,
              iconColor = MunicipalBlue,
              onClick = {
                isSpeedDialOpen = false
                viewModel.openBusinessRegistrationDialog()
              }
            )

            // Action 4: Report Overflowing Bin
            SpeedDialActionItem(
              label = "Report Overflowing Bin",
              icon = Icons.Default.ReportProblem,
              iconColor = Color(0xFFEF4444),
              onClick = {
                isSpeedDialOpen = false
                viewModel.openOverflowBinReportScreen()
              }
            )

            // Action 5: Live Notifications
            SpeedDialActionItem(
              label = "Notification Center",
              icon = Icons.Default.Notifications,
              iconColor = Color(0xFFA855F7),
              badge = if (unreadNotificationCount > 0) "$unreadNotificationCount" else null,
              onClick = {
                isSpeedDialOpen = false
                showNotificationDialog = true
              }
            )
          }
        }

        // Primary Floating Action Button with Animated Rotation
        val fabRotation by animateFloatAsState(
          targetValue = if (isSpeedDialOpen) 135f else 0f,
          animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
          label = "fabRotation"
        )
        val fabBgColor by animateColorAsState(
          targetValue = if (isSpeedDialOpen) Color(0xFFEF4444) else MaterialTheme.colorScheme.primary,
          label = "fabBg"
        )

        FloatingActionButton(
          onClick = { isSpeedDialOpen = !isSpeedDialOpen },
          containerColor = fabBgColor,
          contentColor = Color.Black,
          shape = CircleShape,
          elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp, pressedElevation = 10.dp),
          modifier = Modifier
            .testTag("floating_quick_action_nav_button")
            .size(56.dp)
        ) {
          Icon(
            imageVector = if (isSpeedDialOpen) Icons.Default.Close else Icons.Default.Add,
            contentDescription = if (isSpeedDialOpen) "Close Actions" else "Open Quick Navigation Actions",
            modifier = Modifier
              .size(26.dp)
              .rotate(fabRotation),
            tint = if (isSpeedDialOpen) Color.White else Color.Black
          )
        }
      }
    },
    bottomBar = {
      // Role-Specific Dynamic Material 3 Bottom Navigation Bar
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .shadow(elevation = 8.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp
      ) {
        NavigationBar(
          modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
          containerColor = MaterialTheme.colorScheme.surface,
          tonalElevation = 0.dp
        ) {
          when (currentRole) {
            UserRole.RESIDENT -> {
              ResidentNavTab.values().forEach { tab ->
                val isSelected = residentTab == tab
                val scale by animateFloatAsState(
                  targetValue = if (isSelected) 1.15f else 1.0f,
                  animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                  label = "resIconScale_${tab.name}"
                )
                val badgeCount = when (tab) {
                  ResidentNavTab.REQUEST -> residentActiveCount
                  else -> 0
                }

                NavigationBarItem(
                  selected = isSelected,
                  onClick = { residentTab = tab },
                  icon = {
                    BadgedBox(
                      badge = {
                        if (badgeCount > 0) {
                          Badge(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.Black
                          ) {
                            Text(
                              text = if (badgeCount > 99) "99+" else "$badgeCount",
                              fontWeight = FontWeight.Bold,
                              fontSize = 9.sp
                            )
                          }
                        }
                      }
                    ) {
                      Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.title,
                        modifier = Modifier
                          .size(22.dp)
                          .scale(scale)
                      )
                    }
                  },
                  label = {
                    Text(
                      text = tab.title,
                      fontSize = 11.sp,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                  },
                  colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                  ),
                  modifier = Modifier.testTag("nav_resident_${tab.name.lowercase()}")
                )
              }
            }
            UserRole.BUSINESS -> {
              BusinessNavTab.values().forEach { tab ->
                val isSelected = businessTab == tab
                val scale by animateFloatAsState(
                  targetValue = if (isSelected) 1.15f else 1.0f,
                  animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                  label = "busIconScale_${tab.name}"
                )
                val badgeCount = when (tab) {
                  BusinessNavTab.PICKUPS -> allRequests.count { (it.residentId == residentUser.id || (it.businessName != null && it.businessName.equals(residentUser.businessName ?: residentUser.fullName, ignoreCase = true))) && it.collectionStatus != CollectionStatus.DISPOSED }
                  BusinessNavTab.CONTRACTS -> allScheduledPlans.count { it.businessId == residentUser.id || it.businessName.equals(residentUser.businessName ?: residentUser.fullName, ignoreCase = true) }
                  else -> 0
                }

                NavigationBarItem(
                  selected = isSelected,
                  onClick = { businessTab = tab },
                  icon = {
                    BadgedBox(
                      badge = {
                        if (badgeCount > 0) {
                          Badge(
                            containerColor = MunicipalBlue,
                            contentColor = Color.White
                          ) {
                            Text(
                              text = "$badgeCount",
                              fontWeight = FontWeight.Bold,
                              fontSize = 9.sp
                            )
                          }
                        }
                      }
                    ) {
                      Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.title,
                        modifier = Modifier
                          .size(22.dp)
                          .scale(scale)
                      )
                    }
                  },
                  label = {
                    Text(
                      text = tab.title,
                      fontSize = 11.sp,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                  },
                  colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = MunicipalBlue,
                    indicatorColor = MunicipalBlue,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                  ),
                  modifier = Modifier.testTag("nav_business_${tab.name.lowercase()}")
                )
              }
            }
            UserRole.DRIVER -> {
              DriverNavTab.values().forEach { tab ->
                val isSelected = driverTab == tab
                val scale by animateFloatAsState(
                  targetValue = if (isSelected) 1.15f else 1.0f,
                  animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                  label = "drvIconScale_${tab.name}"
                )
                val badgeCount = when (tab) {
                  DriverNavTab.DISPATCHES -> activeDispatchesCount
                  else -> 0
                }

                NavigationBarItem(
                  selected = isSelected,
                  onClick = { driverTab = tab },
                  icon = {
                    BadgedBox(
                      badge = {
                        if (badgeCount > 0) {
                          Badge(
                            containerColor = Color(0xFF10B981),
                            contentColor = Color.Black
                          ) {
                            Text(
                              text = "$badgeCount",
                              fontWeight = FontWeight.Bold,
                              fontSize = 9.sp
                            )
                          }
                        }
                      }
                    ) {
                      Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.title,
                        modifier = Modifier
                          .size(22.dp)
                          .scale(scale)
                      )
                    }
                  },
                  label = {
                    Text(
                      text = tab.title,
                      fontSize = 11.sp,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                  },
                  colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    selectedTextColor = Color(0xFF10B981),
                    indicatorColor = Color(0xFF10B981),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                  ),
                  modifier = Modifier.testTag("nav_driver_${tab.name.lowercase()}")
                )
              }
            }
            UserRole.MUNICIPAL_COMMISSIONER -> {
              CommissionerNavTab.values().forEach { tab ->
                val isSelected = commissionerTab == tab
                val scale by animateFloatAsState(
                  targetValue = if (isSelected) 1.15f else 1.0f,
                  animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                  label = "comIconScale_${tab.name}"
                )
                val badgeCount = when (tab) {
                  CommissionerNavTab.APPROVALS -> pendingAppsCount
                  CommissionerNavTab.AUDITS -> allComplaints.count { it.status != "RESOLVED" }
                  else -> 0
                }

                NavigationBarItem(
                  selected = isSelected,
                  onClick = { commissionerTab = tab },
                  icon = {
                    BadgedBox(
                      badge = {
                        if (badgeCount > 0) {
                          Badge(
                            containerColor = Color(0xFFF59E0B),
                            contentColor = Color.Black
                          ) {
                            Text(
                              text = "$badgeCount",
                              fontWeight = FontWeight.Bold,
                              fontSize = 9.sp
                            )
                          }
                        }
                      }
                    ) {
                      Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.title,
                        modifier = Modifier
                          .size(22.dp)
                          .scale(scale)
                      )
                    }
                  },
                  label = {
                    Text(
                      text = tab.title,
                      fontSize = 11.sp,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                  },
                  colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    selectedTextColor = Color(0xFFF59E0B),
                    indicatorColor = Color(0xFFF59E0B),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                  ),
                  modifier = Modifier.testTag("nav_commissioner_${tab.name.lowercase()}")
                )
              }
            }
            UserRole.DISPOSAL_OFFICER -> {
              DisposalNavTab.values().forEach { tab ->
                val isSelected = disposalTab == tab
                val scale by animateFloatAsState(
                  targetValue = if (isSelected) 1.15f else 1.0f,
                  animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                  label = "dispIconScale_${tab.name}"
                )
                val badgeCount = when (tab) {
                  DisposalNavTab.MANIFESTS -> allRequests.count { it.collectionStatus == CollectionStatus.COLLECTED }
                  else -> 0
                }

                NavigationBarItem(
                  selected = isSelected,
                  onClick = { disposalTab = tab },
                  icon = {
                    BadgedBox(
                      badge = {
                        if (badgeCount > 0) {
                          Badge(
                            containerColor = Color(0xFF8B5CF6),
                            contentColor = Color.White
                          ) {
                            Text(
                              text = "$badgeCount",
                              fontWeight = FontWeight.Bold,
                              fontSize = 9.sp
                            )
                          }
                        }
                      }
                    ) {
                      Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.title,
                        modifier = Modifier
                          .size(22.dp)
                          .scale(scale)
                      )
                    }
                  },
                  label = {
                    Text(
                      text = tab.title,
                      fontSize = 11.sp,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                  },
                  colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color(0xFF8B5CF6),
                    indicatorColor = Color(0xFF8B5CF6),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                  ),
                  modifier = Modifier.testTag("nav_disposal_${tab.name.lowercase()}")
                )
              }
            }
          }
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .background(MaterialTheme.colorScheme.background)
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        // High-Priority Resident Proximity & Completion Alert Banner
        ResidentNotificationAlertBanner(
          alert = activeResidentAlert,
          onDismiss = { viewModel.dismissActiveResidentAlert() },
          onTrackLive = { reqId ->
            viewModel.dismissActiveResidentAlert()
            residentTab = ResidentNavTab.MAP
          },
          onViewReceipt = { reqId ->
            viewModel.dismissActiveResidentAlert()
            val req = allRequests.find { it.id == reqId }
            if (req != null) {
              viewModel.openReceiptDialog(req)
            }
          },
          onRateDriver = { reqId ->
            viewModel.dismissActiveResidentAlert()
            val req = allRequests.find { it.id == reqId }
            if (req != null) {
              viewModel.openRatingDialog(req)
            }
          },
          onOpenChat = { reqId ->
            viewModel.dismissActiveResidentAlert()
            viewModel.openChatForRequest(reqId)
          }
        )

        // Directional Smooth Animated Slide & Fade Screen Transition
        AnimatedContent(
          targetState = currentRole,
          transitionSpec = {
            (slideInHorizontally(initialOffsetX = { 200 }, animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy)) + fadeIn(animationSpec = tween(220)))
              .togetherWith(slideOutHorizontally(targetOffsetX = { -200 }, animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy)) + fadeOut(animationSpec = tween(180)))
          },
          label = "RolePortalTransition",
          modifier = Modifier.weight(1f)
        ) { role ->
          when (role) {
            UserRole.RESIDENT -> {
              val highPrecisionGps by viewModel.highPrecisionGpsLocation.collectAsState()
              ResidentPortalScreen(
                user = residentUser,
                requests = residentUserRequests,
                facilities = allFacilities,
                scheduledPlans = allScheduledPlans,
                gpsLocation = highPrecisionGps,
                currentTab = residentTab,
                onNavigateToTab = { residentTab = it },
                onRequestCollectionClick = { viewModel.openNewRequestDialog() },
                onCommercialRequestClick = { viewModel.openNewRequestDialog() },
                onScheduleCollectionPlanClick = { viewModel.openSchedulePlanDialog() },
                onRegisterCommercialClick = { viewModel.openBusinessRegistrationDialog() },
                onTogglePlanActive = { planId, active -> viewModel.toggleScheduledPlanActive(planId, active) },
                onDeletePlan = { planId -> viewModel.deleteScheduledPlan(planId) },
                onReportOverflowingBinClick = { viewModel.openOverflowBinReportScreen() },
                onViewReceiptClick = { req -> viewModel.openReceiptDialog(req) },
                onRateDriverClick = { req -> viewModel.openRatingDialog(req) },
                onLodgeComplaintClick = { req -> viewModel.openComplaintDialog(req) },
                onViewChainOfCustodyClick = { req -> selectedRequestForChainOfCustody = req },
                onOpenChatClick = { req -> viewModel.openChatForRequest(req.id) },
                onSimulateDriverStep = { reqId -> viewModel.simulateDriverMove(reqId) },
                onUpdatePrecisionPin = { reqId, lat, lng -> viewModel.updateResidentRequestGpsPin(reqId, lat, lng) },
                onOpenNotifications = { showNotificationDialog = true }
              )
            }
            UserRole.DRIVER -> {
            val effectiveDriver = currentDriverEntity ?: approvedDrivers.firstOrNull()
            val driverAssignedRequests = allRequests.filter {
              it.assignedDriverId == effectiveDriver?.assignedDriverId ||
              it.assignedDriverId == effectiveDriver?.id
            }
            val unassignedNearby = allRequests.filter { it.assignedDriverId == null }
            DriverPortalScreen(
              driver = effectiveDriver,
              assignedRequests = driverAssignedRequests,
              nearbyUnassignedRequests = unassignedNearby,
              allRequests = allRequests,
              disposalFacilities = allFacilities,
              currentTab = driverTab,
              onNavigateToTab = { driverTab = it },
              onAdvanceStatus = { reqId, nextStatus, weightKg ->
                viewModel.advanceDriverStatus(reqId, nextStatus, weightKg)
              },
              onCheckInAtDisposal = { reqId, facId, facName, isRecycled ->
                viewModel.checkInAtDisposalFacility(reqId, facId, facName, isRecycled)
              },
              onClaimNearbyRequest = { req ->
                if (effectiveDriver != null) {
                  viewModel.claimRequestByDriver(req.id, effectiveDriver)
                }
              },
              onOpenChat = { req -> viewModel.openChatForRequest(req.id) },
              onApplyClick = { viewModel.openDriverApplicationDialog() }
            )
          }
          UserRole.MUNICIPAL_COMMISSIONER -> {
            CommissionerPortalScreen(
              requests = allRequests,
              applications = allApplications,
              approvedDrivers = approvedDrivers,
              complaints = allComplaints,
              facilities = allFacilities,
              auditLogs = allAuditLogs,
              onOpenDispatchDialog = { req -> viewModel.openDispatchDialog(req) },
              onOpenReviewAppDialog = { app -> viewModel.openReviewApplicationDialog(app) },
              onResolveComplaint = { compId, notes -> viewModel.resolveComplaint(compId, notes) },
              onOpenReportsDialog = { showReportsDialog = true },
              onSuspendDriver = { driverId, reason -> viewModel.suspendDriver(driverId, reason) },
              onReactivateDriver = { driverId -> viewModel.reactivateDriver(driverId) },
              onQuickApproveDriver = { appId -> viewModel.quickApproveDriver(appId) },
              onQuickRejectDriver = { appId, reason -> viewModel.quickRejectDriver(appId, reason) },
              onQuickAutoAssign = { reqId -> viewModel.quickAutoAssignRequest(reqId) },
              onAssignDriverDirect = { reqId, driver -> viewModel.assignDriver(reqId, driver) },
              onBatchAutoAssign = { viewModel.batchAutoAssignAllPending() },
              onBatchApproveDrivers = { viewModel.batchApprovePendingApplications() },
              currentTab = commissionerTab,
              onNavigateToTab = { commissionerTab = it }
            )
          }
          UserRole.DISPOSAL_OFFICER -> {
            DisposalFacilityScreen(
              facilities = allFacilities,
              requests = allRequests,
              onVerifyDisposal = { reqId, facId, facName, isRecycled ->
                viewModel.checkInAtDisposalFacility(reqId, facId, facName, isRecycled)
              },
              currentTab = disposalTab,
              onNavigateToTab = { disposalTab = it }
            )
          }
          UserRole.BUSINESS -> {
            BusinessPortalScreen(
              viewModel = viewModel,
              onRequestCollection = { viewModel.openNewRequestDialog() },
              onScheduleContract = { viewModel.openSchedulePlanDialog() },
              onOpenComplaint = { viewModel.openOverflowBinReportScreen() },
              onViewReceipt = { req: CollectionRequestEntity -> viewModel.openReceiptDialog(req) },
              onViewCustodyProof = { req: CollectionRequestEntity -> selectedRequestForChainOfCustody = req },
              onOpenNavigationMap = { businessTab = BusinessNavTab.PICKUPS },
              currentTab = businessTab,
              onNavigateToTab = { businessTab = it }
            )
          }
        }
      }
    }
  }
}

  // Active Dialog Overlays
  if (showNewRequestDialog) {
    RequestCollectionDialog(
      onDismiss = { viewModel.closeNewRequestDialog() },
      onSubmit = { cat, qty, bags, desc, addr, ward, dist, lat, lon, date, slot, urg, pay, recur, inter ->
        viewModel.createRequest(
          category = cat,
          quantityKg = qty,
          bagCount = bags,
          description = desc,
          pickupAddress = addr,
          ward = ward,
          district = dist,
          latitude = lat,
          longitude = lon,
          preferredDate = date,
          preferredTimeSlot = slot,
          urgency = urg,
          paymentMethod = pay,
          isRecurring = recur,
          recurringInterval = inter
        )
      }
    )
  }

  if (showDriverApplicationDialog) {
    DriverApplicationDialog(
      onDismiss = { viewModel.closeDriverApplicationDialog() },
      onSubmit = { name, phone, email, nida, licNo, licClass, licExp, vehReg, vehType, cap, insExp, zone ->
        viewModel.submitDriverApplication(
          fullName = name,
          phone = phone,
          email = email,
          nidaNumber = nida,
          licenseNumber = licNo,
          licenseClass = licClass,
          licenseExpiry = licExp,
          vehicleReg = vehReg,
          vehicleType = vehType,
          capacityKg = cap,
          insuranceExpiry = insExp,
          preferredZone = zone
        )
      }
    )
  }

  selectedRequestForDispatch?.let { request ->
    SmartAssignmentDialog(
      request = request,
      recommendations = driverRecommendations,
      approvedDrivers = approvedDrivers,
      onDismiss = { viewModel.closeDispatchDialog() },
      onAssignDriver = { driver ->
        viewModel.assignDriver(request.id, driver)
      }
    )
  }

  selectedAppForReview?.let { app ->
    DriverVerificationDialog(
      application = app,
      onDismiss = { viewModel.closeReviewApplicationDialog() },
      onApprove = { notes -> viewModel.approveDriverApplication(app.id, notes) },
      onReject = { notes -> viewModel.rejectDriverApplication(app.id, notes) }
    )
  }

  selectedReceipt?.let { req ->
    ReceiptDialog(
      request = req,
      onDismiss = { viewModel.closeReceiptDialog() }
    )
  }

  selectedRequestForRating?.let { req ->
    RatingDialog(
      request = req,
      onDismiss = { viewModel.closeRatingDialog() },
      onSubmitRating = { score, fb -> viewModel.submitRating(req.id, score, fb) }
    )
  }

  selectedRequestForComplaint?.let { req ->
    ComplaintDialog(
      request = req,
      onDismiss = { viewModel.closeComplaintDialog() },
      onSubmitComplaint = { cat, desc ->
        viewModel.submitComplaint(
          requestId = req.id,
          driverId = req.assignedDriverId,
          driverName = req.assignedDriverName,
          category = cat,
          description = desc
        )
      }
    )
  }

  if (showAuthDialog) {
    UserProfileDialog(
      currentUser = residentUser,
      currentRole = currentRole,
      currentDriverEntity = currentDriverEntity ?: approvedDrivers.firstOrNull(),
      onDismiss = { showAuthDialog = false },
      onSaveResidentProfile = { updatedUser ->
        viewModel.saveResidentUser(updatedUser)
      },
      onSignOut = {
        showAuthDialog = false
        viewModel.logout()
      }
    )
  }

  if (showPortalSwitcherDialog) {
    PortalSwitcherDialog(
      currentRole = currentRole,
      onSelectRole = { newRole ->
        viewModel.switchRole(newRole)
        showPortalSwitcherDialog = false
      },
      onDismiss = { showPortalSwitcherDialog = false },
      residentActiveRequestsCount = residentActiveCount,
      driverActiveDispatchesCount = activeDispatchesCount,
      commissionerPendingApprovalsCount = pendingAppsCount,
      businessOrdersCount = allRequests.count { it.isCommercialRequest }
    )
  }

  if (showNotificationDialog) {
    NotificationCenterDialog(
      notifications = liveNotifications,
      onDismiss = { showNotificationDialog = false },
      onMarkAllAsRead = { viewModel.markAllNotificationsAsRead() },
      onTrackRequest = { reqId ->
        showNotificationDialog = false
        if (currentRole != UserRole.RESIDENT) {
          viewModel.switchRole(UserRole.RESIDENT)
        }
      },
      onViewReceipt = { reqId ->
        showNotificationDialog = false
        val req = allRequests.find { it.id == reqId }
        if (req != null) {
          viewModel.openReceiptDialog(req)
        }
      },
      onRateService = { reqId ->
        showNotificationDialog = false
        val req = allRequests.find { it.id == reqId }
        if (req != null) {
          viewModel.openRatingDialog(req)
        }
      }
    )
  }

  if (showReportsDialog) {
    MunicipalReportsDialog(
      requests = allRequests,
      drivers = approvedDrivers,
      facilities = allFacilities,
      onDismiss = { showReportsDialog = false }
    )
  }

  selectedRequestForChainOfCustody?.let { req ->
    ChainOfCustodyDialog(
      request = req,
      onDismiss = { selectedRequestForChainOfCustody = null }
    )
  }

  if (showOverflowBinReportScreen) {
    OverflowBinReportScreen(
      onDismiss = { viewModel.closeOverflowBinReportScreen() },
      onSubmitReport = { ward, landmark, severity, notes, photoUri, lat, lon ->
        viewModel.reportOverflowingBin(ward, landmark, severity, notes, photoUri, lat, lon)
      }
    )
  }

  if (showThemeSettingsDialog) {
    ThemeSettingsDialog(
      currentThemeMode = themeMode,
      currentPalette = appThemePalette,
      currentFontSizeScale = fontSizeScale,
      onSelectThemeMode = { viewModel.setThemeMode(it) },
      onSelectPalette = { viewModel.setAppThemePalette(it) },
      onSelectFontSizeScale = { viewModel.setFontSizeScale(it) },
      onDismiss = { viewModel.closeThemeSettingsDialog() }
    )
  }

  activeChatRequest?.let { req ->
    val currentUserId = if (currentRole == UserRole.DRIVER) {
      currentDriverEntity?.id ?: "drv_unknown"
    } else {
      residentUser.id
    }
    PickupCoordinationChatDialog(
      request = req,
      messages = activeChatMessages,
      currentUserRole = currentRole,
      currentUserId = currentUserId,
      onSendMessage = { content, isQuickAction ->
        viewModel.sendChatMessage(content, isQuickAction)
      },
      onDismiss = {
        viewModel.closeChat()
      }
    )
  }

  if (showSchedulePlanDialog) {
    ScheduleCollectionPlanDialog(
      user = residentUser,
      onDismiss = { viewModel.closeSchedulePlanDialog() },
      onSubmitPlan = { freq, days, slot, cat, bins, cont, addr, ward, land, lat, lon, rate ->
        viewModel.createScheduledCollectionPlan(
          frequency = freq,
          selectedDays = days,
          timeSlot = slot,
          category = cat,
          binCount = bins,
          containerType = cont,
          pickupAddress = addr,
          ward = ward,
          landmark = land,
          latitude = lat,
          longitude = lon,
          monthlyRateTzs = rate
        )
      }
    )
  }

  if (showBusinessRegistrationDialog) {
    BusinessRegistrationDialog(
      initialName = if (residentUser.isBusinessAccount) (residentUser.businessName ?: "") else "",
      initialEmail = residentUser.email,
      initialPhone = residentUser.phone,
      initialWard = residentUser.ward,
      onDismiss = { viewModel.closeBusinessRegistrationDialog() },
      onRegister = { name, type, tin, contact, email, phone, addr, ward, dist, dock, cont, bins, hours, lat, lon ->
        viewModel.registerCommercialBusiness(
          businessName = name,
          businessType = type,
          tinNumber = tin,
          contactPerson = contact,
          contactPhone = phone,
          contactEmail = email,
          address = addr,
          ward = ward,
          district = dist,
          operatingHours = hours,
          branchName = "",
          loadingDockNotes = dock,
          binCount = bins,
          binType = cont,
          latitude = lat,
          longitude = lon
        )
      }
    )
  }
}



