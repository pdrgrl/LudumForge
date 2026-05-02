package dam.a51319.ludumforge.ui.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BlurOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dam.a51319.ludumforge.data.SessionManager
import dam.a51319.ludumforge.models.User
import dam.a51319.ludumforge.models.UserPlan
import dam.a51319.ludumforge.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LudumForgeTopAppBar(
    currentUser: User?,
    onLogout: () -> Unit,
    onNavigateToSubscription: () -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }

    val activeJamName by SessionManager.activeJamName.collectAsState()
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("LudumForgePrefs", Context.MODE_PRIVATE) }
    var apiKeyInput by remember { mutableStateOf(sharedPrefs.getString("gemini_api_key", "") ?: "") }

    val isPremium = currentUser?.plan == UserPlan.PREMIUM

    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.BlurOn, contentDescription = "Logo", tint = PrimaryBlack, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        "LudumForge",
                        style = MaterialTheme.typography.titleLarge,
                        color = PrimaryBlack,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (!activeJamName.isNullOrBlank()) "▸ $activeJamName" else " ",
                        fontSize = 14.sp,
                        color = SecondaryGray,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.3.sp,
                        modifier = Modifier.graphicsLayer {
                            alpha = if (!activeJamName.isNullOrBlank()) 1f else 0f
                        }
                    )
                }
            }
        },
        actions = {
            // ── Plan badge (tappable) ─────────────────────────────────────────
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isPremium) PrimaryBlack else SurfaceContainerHigh)
                    .clickable { onNavigateToSubscription() }
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (isPremium) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                    Text("Premium", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                } else {
                    Text("FREE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SecondaryGray)
                }
            }

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Outlined.AccountCircle, contentDescription = "Profile", tint = PrimaryBlack)
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(SurfaceContainerLowest)
                ) {
                    DropdownMenuItem(
                        text = { Text(currentUser?.username ?: "Loading...", fontWeight = FontWeight.Bold, color = PrimaryBlack) },
                        onClick = { showMenu = false }
                    )
                    HorizontalDivider(color = GhostBorder)
                    DropdownMenuItem(
                        text = { Text("Subscription", color = PrimaryBlack) },
                        onClick = { showMenu = false; onNavigateToSubscription() },
                        leadingIcon = { Icon(Icons.Default.Star, contentDescription = null, tint = PrimaryBlack, modifier = Modifier.size(16.dp)) }
                    )
                    DropdownMenuItem(
                        text = { Text("Settings", color = PrimaryBlack) },
                        onClick = { showMenu = false; showSettings = true }
                    )
                    DropdownMenuItem(
                        text = { Text("Sign Out", color = ErrorRed) },
                        onClick = { showMenu = false; onLogout() }
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceBase.copy(alpha = 0.9f))
    )

    if (showSettings) {
        AlertDialog(
            onDismissRequest = { showSettings = false },
            containerColor = SurfaceContainerLowest,
            title = { Text("Profile & Settings", fontWeight = FontWeight.Bold, color = PrimaryBlack) },
            text = {
                Column {
                    Text(
                        "Free users must provide their own Google Gemini API Key to use the AI Roadmap Generator.",
                        fontSize = 12.sp, color = SecondaryGray
                    )
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
                ) { Text("Save", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { showSettings = false }) { Text("Cancel", color = PrimaryBlack) }
            }
        )
    }
}
