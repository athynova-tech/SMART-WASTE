package com.example.ui.screens

import androidx.compose.runtime.Composable
import com.example.data.local.UserEntity
import com.example.data.model.UserRole

@Composable
fun AuthDialog(
    currentUser: UserEntity,
    onDismiss: () -> Unit,
    onLoginResident: (UserEntity) -> Unit = {},
    onVerifyDriverLogin: (String, String, (Boolean, String?) -> Unit) -> Unit = { _, _, _ -> }
) {
    UserProfileDialog(
        currentUser = currentUser,
        currentRole = UserRole.RESIDENT,
        currentDriverEntity = null,
        onDismiss = onDismiss,
        onSaveResidentProfile = onLoginResident
    )
}
