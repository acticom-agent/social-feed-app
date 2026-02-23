package com.example.socialfeed.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.socialfeed.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onProfileReset: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val darkMode by viewModel.darkMode.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    viewModel.logout { onProfileReset() }
                }) { Text("Logout", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel") }
            }
        )
    }

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Settings", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, "Back")
                }
            }
        )

        Column(Modifier.padding(16.dp)) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Row {
                        Icon(Icons.Filled.DarkMode, contentDescription = null)
                        Spacer(Modifier.width(12.dp))
                        Text("Dark Mode", style = MaterialTheme.typography.bodyLarge)
                    }
                    Switch(
                        checked = darkMode ?: false,
                        onCheckedChange = { viewModel.setDarkMode(it) }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            SettingsButton(
                icon = Icons.Filled.Logout,
                text = "Logout",
                onClick = { showLogoutDialog = true },
                isDestructive = true
            )
        }
    }
}

@Composable
private fun SettingsButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    val color = if (isDestructive) MaterialTheme.colorScheme.error
    else MaterialTheme.colorScheme.onSurface

    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Icon(icon, contentDescription = null, tint = color)
        Spacer(Modifier.width(8.dp))
        Text(text, color = color)
    }
}
