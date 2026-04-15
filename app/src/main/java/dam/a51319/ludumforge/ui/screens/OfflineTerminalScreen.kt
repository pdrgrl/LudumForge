package dam.a51319.ludumforge.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.BlurOn
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dam.a51319.ludumforge.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Data class to represent local offline actions
data class OfflineLogEntry(
    val timestamp: Date,
    val type: String,
    val description: String,
    val isError: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfflineTerminalScreen() {
    var commandInput by remember { mutableStateOf("") }

    // Dummy Terminal Logs
    val terminalLogs = listOf(
        OfflineLogEntry(Date(System.currentTimeMillis() - 3600000), "SYSTEM", "Connection to main server lost. Switching to local cache."),
        OfflineLogEntry(Date(System.currentTimeMillis() - 2400000), "CACHE", "Local state synchronized. Workspace ready for offline mode."),
        OfflineLogEntry(Date(System.currentTimeMillis() - 1200000), "CREATE_TASK", "Task T-84 'Refactor Dialogue System' added to local queue."),
        OfflineLogEntry(Date(System.currentTimeMillis() - 600000), "UPDATE_PROJ", "Project P-02 status changed to ACTIVE."),
        OfflineLogEntry(Date(System.currentTimeMillis() - 300000), "SYNC_ERR", "Attempted sync... Network unreachable.", isError = true)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.BlurOn,
                            contentDescription = "Logo",
                            tint = PrimaryBlack,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "LudumForge",
                            style = MaterialTheme.typography.titleLarge,
                            color = PrimaryBlack
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(
                            imageVector = Icons.Outlined.AccountCircle,
                            contentDescription = "Profile",
                            tint = PrimaryBlack
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceBase.copy(alpha = 0.9f),
                    scrolledContainerColor = SurfaceBase
                )
            )
        },
//        bottomBar = {
//            NavigationBar(
//                containerColor = SurfaceContainerLowest,
//                contentColor = SecondaryGray,
//                tonalElevation = 8.dp
//            ) {
//                NavigationBarItem(
//                    icon = { Icon(Icons.Default.Architecture, contentDescription = "Planning") },
//                    label = { Text("Project Planning", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
//                    selected = false,
//                    colors = NavigationBarItemDefaults.colors(
//                        unselectedIconColor = SecondaryGray,
//                        unselectedTextColor = SecondaryGray
//                    ),
//                    onClick = { /* TODO */ }
//                )
//                NavigationBarItem(
//                    icon = { Icon(Icons.Default.Groups, contentDescription = "Workspace") },
//                    label = { Text("Team Workspace", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
//                    selected = true,
//                    onClick = { /* TODO */ },
//                    colors = NavigationBarItemDefaults.colors(
//                        indicatorColor = SurfaceContainerHigh,
//                        selectedIconColor = PrimaryBlack,
//                        selectedTextColor = PrimaryBlack
//                    )
//                )
//                NavigationBarItem(
//                    icon = { Icon(Icons.AutoMirrored.Filled.AltRoute, contentDescription = "Roadmap") },
//                    label = { Text("Roadmap", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
//                    selected = false,
//                    colors = NavigationBarItemDefaults.colors(
//                        unselectedIconColor = SecondaryGray,
//                        unselectedTextColor = SecondaryGray
//                    ),
//                    onClick = { /* TODO */ }
//                )
//            }
//        },
        containerColor = SurfaceBase
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(top = 24.dp, bottom = 100.dp)
        ) {
            // Header
            item {
                Text(
                    text = "SYSTEM STATUS",
                    style = MaterialTheme.typography.labelLarge,
                    color = SecondaryGray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Offline Terminal",
                    style = MaterialTheme.typography.headlineLarge,
                    color = PrimaryBlack
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Connection Status Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(12.dp),
                            spotColor = PrimaryBlack.copy(alpha = 0.05f)
                        ),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, GhostBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(SurfaceContainerHigh),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CloudOff,
                                contentDescription = "Offline",
                                tint = SecondaryGray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Working Locally",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlack
                            )
                            Text(
                                text = "Changes will sync when reconnected.",
                                fontSize = 13.sp,
                                color = SecondaryGray
                            )
                        }
                        Button(
                            onClick = { /* TODO */ },
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerHigh, contentColor = PrimaryBlack),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Icon(Icons.Outlined.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Retry", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Terminal Log View (Debossed)
            item {
                Text(
                    text = "ACTION QUEUE & LOGS",
                    style = MaterialTheme.typography.labelLarge,
                    color = SecondaryGray
                )
                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceContainerHigh) // Debossed feel
                        .padding(16.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(terminalLogs) { log ->
                            TerminalLogLine(log)
                        }
                        item {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = ">",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    color = PrimaryBlack,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                // Blinking cursor effect representation
                                Box(modifier = Modifier.size(6.dp, 12.dp).background(PrimaryBlack.copy(alpha = 0.5f)))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Command Input (Debossed)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceContainerHigh)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$",
                        fontFamily = FontFamily.Monospace,
                        color = SecondaryGray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    BasicTextField(
                        value = commandInput,
                        onValueChange = { commandInput = it },
                        modifier = Modifier.weight(1f),
                        textStyle = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp,
                            color = PrimaryBlack
                        ),
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            if (commandInput.isEmpty()) {
                                Text(
                                    text = "Enter terminal command or quick note...",
                                    color = SecondaryGray.copy(alpha = 0.7f),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 14.sp
                                )
                            }
                            innerTextField()
                        }
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Execute",
                        tint = if (commandInput.isNotEmpty()) PrimaryBlack else SecondaryGray.copy(alpha = 0.5f),
                        modifier = Modifier
                            .size(20.dp)
                            .clickable(enabled = commandInput.isNotEmpty()) { /* Execute Command */ }
                    )
                }
            }
        }
    }
}

@Composable
fun TerminalLogLine(log: OfflineLogEntry) {
    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val timeString = timeFormat.format(log.timestamp)

    val tagColor = if (log.isError) ErrorRed else SecondaryGray

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "[$timeString]",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            color = SecondaryGray.copy(alpha = 0.7f),
            modifier = Modifier.width(70.dp)
        )
        Text(
            text = log.type,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = tagColor,
            modifier = Modifier.width(85.dp)
        )
        Text(
            text = log.description,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            color = if (log.isError) ErrorRed else PrimaryBlack,
            modifier = Modifier.weight(1f),
            lineHeight = 16.sp
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF9F9F9)
@Composable
fun OfflineTerminalScreenPreview() {
    LudumForgeTheme {
        OfflineTerminalScreen()
    }
}