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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LudumForgeTopAppBar(
    currentUser: User?,
    onLogout: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

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
                        text = { Text("Sign Out", color = ErrorRed) },
                        onClick = {
                            showMenu = false
                            onLogout()
                        }
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceBase.copy(alpha = 0.9f))
    )
}