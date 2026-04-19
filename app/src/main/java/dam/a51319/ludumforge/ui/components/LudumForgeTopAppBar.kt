package dam.a51319.ludumforge.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BlurOn
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dam.a51319.ludumforge.models.User
import dam.a51319.ludumforge.ui.theme.*
import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LudumForgeTopAppBar(currentUser: User?, onLogout: () -> Unit) {
    var showMenu by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("LudumForgePrefs", Context.MODE_PRIVATE) }
    var apiKeyInput by remember { mutableStateOf(sharedPrefs.getString("gemini_api_key", "") ?: "") }

    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.BlurOn, contentDescription = "Logo", tint = PrimaryBlack, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("LudumForge", style = MaterialTheme.typography.titleLarge, color = PrimaryBlack)
            }
        },
        actions = {
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Outlined.AccountCircle, contentDescription = "Profile", tint = PrimaryBlack)
                }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }, modifier = Modifier.background(SurfaceContainerLowest)) {
                    DropdownMenuItem(text = { Text(currentUser?.username ?: "Loading...", fontWeight = FontWeight.Bold, color = PrimaryBlack) }, onClick = { showMenu = false })
                    HorizontalDivider(color = GhostBorder)

                    // NEW SETTINGS BUTTON
                    DropdownMenuItem(
                        text = { Text("Settings", color = PrimaryBlack) },
                        onClick = {
                            showMenu = false
                            showSettings = true
                        }
                    )

                    DropdownMenuItem(text = { Text("Sign Out", color = ErrorRed) }, onClick = { showMenu = false; onLogout() })
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceBase.copy(alpha = 0.9f))
    )
    // SETTINGS DIALOG
    if (showSettings) {
        AlertDialog(
            onDismissRequest = { showSettings = false },
            containerColor = SurfaceContainerLowest,
            title = { Text("Profile & Settings", fontWeight = FontWeight.Bold, color = PrimaryBlack) },
            text = {
                Column {
                    Text("Free users must provide their own Google Gemini API Key to use the AI Roadmap Generator.", fontSize = 12.sp, color = SecondaryGray)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = apiKeyInput,
                        onValueChange = { apiKeyInput = it },
                        label = { Text("Gemini API Key") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryBlack)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        sharedPrefs.edit().putString("gemini_api_key", apiKeyInput).apply()
                        showSettings = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack)
                ) {
                    Text("Save", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSettings = false }) { Text("Cancel", color = PrimaryBlack) }
            }
        )
    }
}