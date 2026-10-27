package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.ui.graphics.vector.ImageVector

enum class ResidentNavTab(val title: String, val icon: ImageVector) {
    HOME("Home", Icons.Default.Home),
    REQUEST("Request", Icons.Default.DeleteSweep),
    MAP("Map", Icons.Default.Map),
    PROFILE("Profile", Icons.Default.Person)
}

enum class BusinessNavTab(val title: String, val icon: ImageVector) {
    OVERVIEW("Overview", Icons.Default.Dashboard),
    PICKUPS("Pickups", Icons.Default.LocalShipping),
    CONTRACTS("Contracts", Icons.Default.CalendarMonth),
    PROFILE("Profile & Tax", Icons.Default.Business)
}

enum class DriverNavTab(val title: String, val icon: ImageVector) {
    MAP("Route Map", Icons.Default.Navigation),
    DISPATCHES("Dispatches", Icons.Default.Assignment),
    VEHICLE("Vehicle", Icons.Default.LocalShipping),
    PROFILE("Shift Profile", Icons.Default.Badge)
}

enum class CommissionerNavTab(val title: String, val icon: ImageVector) {
    OVERVIEW("Overview", Icons.Default.Analytics),
    FLEET_MAP("Fleet & Map", Icons.Default.Map),
    APPROVALS("Approvals", Icons.Default.VerifiedUser),
    AUDITS("Audits & Reports", Icons.Default.Assessment)
}

enum class DisposalNavTab(val title: String, val icon: ImageVector) {
    WEIGHBRIDGE("Weighbridge", Icons.Default.Scale),
    MANIFESTS("Manifests", Icons.Default.FactCheck),
    FACILITY("Facility & MRF", Icons.Default.Recycling),
    PROFILE("Officer Profile", Icons.Default.Badge)
}
