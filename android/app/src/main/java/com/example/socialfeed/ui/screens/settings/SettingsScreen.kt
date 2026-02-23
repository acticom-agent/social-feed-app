package com.example.socialfeed.ui.screens.settings

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current
    val darkMode by viewModel.darkMode.collectAsState()
    var showClearDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear All Data") },
            text = { Text("This will permanently delete all posts, comments, likes, and your profile. This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    showClearDialog = false
                    viewModel.clearAllData { onProfileReset() }
                }) { Text("Clear", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Profile") },
            text = { Text("This will reset your profile. You'll need to set up a new one. Your posts will remain.") },
            confirmButton = {
                TextButton(onClick = {
                    showResetDialog = false
                    viewModel.resetProfile { onProfileReset() }
                }) { Text("Reset", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) { Text("Cancel") }
            }
        )
    }

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Settings", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                }
            }
        )

        Column(Modifier.padding(16.dp)) {
            // Dark mode toggle
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
                        Icon(Icons.Default.DarkMode, contentDescription = null)
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

            // Export as JSON
            SettingsButton(
                icon = Icons.Default.Download,
                text = "Export as JSON",
                onClick = {
                    viewModel.exportAsJson { path ->
                        Toast.makeText(context, "Exported to $path", Toast.LENGTH_LONG).show()
                    }
                }
            )

            Spacer(Modifier.height(8.dp))

            // Reset profile
            SettingsButton(
                icon = Icons.Default.PersonOff,
                text = "Reset Profile",
                onClick = { showResetDialog = true },
                isDestructive = true
            )

            Spacer(Modifier.height(8.dp))

            // Clear all data
            SettingsButton(
                icon = Icons.Default.DeleteForever,
                text = "Clear All Data",
                onClick = { showClearDialog = true },
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
