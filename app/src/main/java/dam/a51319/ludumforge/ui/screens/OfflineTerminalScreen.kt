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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dam.a51319.ludumforge.ui.theme.*
import dam.a51319.ludumforge.viewmodels.OfflineTerminalViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import dam.a51319.ludumforge.data.ActionLog
import dam.a51319.ludumforge.viewmodels.PanicTerminalState

data class OfflineLogEntry(val timestamp: Date, val type: String, val description: String, val isError: Boolean = false)

@Composable
fun OfflineTerminalScreen(viewModel: OfflineTerminalViewModel = viewModel()) {
    val context = LocalContext.current

    // Initialize the Room database connection once
    LaunchedEffect(Unit) {
        viewModel.initializeDatabase(context)
    }

    // State Collection
    val commandInput by viewModel.noteText.collectAsState()
    val sessionTimerSeconds by viewModel.sessionTimerSeconds.collectAsState()

    // Collect the REAL logs from Room!
    val terminalLogs by viewModel.logs.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val panicState by viewModel.panicState.collectAsState()

    // Get API Key and Premium Status from SharedPreferences
    val sharedPrefs = remember { context.getSharedPreferences("LudumForgePrefs", Context.MODE_PRIVATE) }
    val userApiKey = sharedPrefs.getString("gemini_api_key", "") ?: ""
    val isPremium = sharedPrefs.getBoolean("is_premium", false)

    OfflineTerminalContent(
        commandInput = commandInput,
        sessionTimerSeconds = sessionTimerSeconds,
        terminalLogs = terminalLogs,
        isSyncing = isSyncing,
        isAnalyzing = panicState is PanicTerminalState.Analyzing,
        onCommandInputChange = { viewModel.noteText.value = it },
        onManualSync = { viewModel.manualSync() },
        onSubmitNote = { viewModel.submitNote(userApiKey, isPremium) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfflineTerminalContent(
    commandInput: String,
    sessionTimerSeconds: Long,
    terminalLogs: List<ActionLog>,
    isSyncing: Boolean,
    isAnalyzing: Boolean = false,
    onCommandInputChange: (String) -> Unit,
    onManualSync: () -> Unit,
    onSubmitNote: () -> Unit
) {
    val mins = sessionTimerSeconds / 60
    val secs = sessionTimerSeconds % 60
    val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", mins, secs)

    Scaffold(
        containerColor = SurfaceBase
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 24.dp),
            contentPadding = PaddingValues(top = 24.dp, bottom = 100.dp)
        ) {
            item {
                Text("SYSTEM STATUS", style = MaterialTheme.typography.labelLarge, color = SecondaryGray)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Offline Terminal", style = MaterialTheme.typography.headlineLarge, color = PrimaryBlack)
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth().shadow(elevation = 4.dp, shape = RoundedCornerShape(12.dp), spotColor = PrimaryBlack.copy(alpha = 0.05f)),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, GhostBorder)
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(40.dp).clip(CircleShape)
                                .background(SurfaceContainerHigh),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Outlined.CloudOff,
                                contentDescription = "Offline",
                                tint = SecondaryGray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Working Locally",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlack
                            )
                            Text(
                                "Session Duration: $formattedTime",
                                fontSize = 13.sp,
                                color = SecondaryGray
                            )
                        }
                        Button(
                            onClick = onManualSync,
                            enabled = !isSyncing,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SurfaceContainerHigh,
                                contentColor = PrimaryBlack,
                                disabledContainerColor = SurfaceContainerHigh.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            if (isSyncing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    color = MoltenOrange,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    Icons.Outlined.Sync,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                if (isSyncing) "Syncing..." else "Retry",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                Text("ACTION QUEUE & LOGS", style = MaterialTheme.typography.labelLarge, color = SecondaryGray)
                Spacer(modifier = Modifier.height(12.dp))
                Box(modifier = Modifier.fillMaxWidth().height(280.dp).clip(RoundedCornerShape(12.dp)).background(SurfaceContainerHigh).padding(16.dp)) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        reverseLayout = true
                    ){ items(terminalLogs) { log -> TerminalLogLine(log) }
                        item {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(">", fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = PrimaryBlack, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(modifier = Modifier.size(6.dp, 12.dp).background(PrimaryBlack.copy(alpha = 0.5f)))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(SurfaceContainerHigh).padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("$", fontFamily = FontFamily.Monospace, color = SecondaryGray, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    BasicTextField(
                        value = commandInput,
                        onValueChange = onCommandInputChange,
                        modifier = Modifier.weight(1f),
                        textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 14.sp, color = PrimaryBlack),
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            if (commandInput.isEmpty()) {
                                Text("Add a dev log or note...", color = SecondaryGray.copy(alpha = 0.7f), fontFamily = FontFamily.Monospace, fontSize = 14.sp)
                            }
                            innerTextField()
                        }
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Execute",
                        tint = if (commandInput.isNotEmpty()) MoltenOrange else SecondaryGray.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp).clickable(enabled = commandInput.isNotEmpty()) { onSubmitNote() }
                    )
                }
            }
        }
    }
}

@Composable
fun TerminalLogLine(log: ActionLog) {
    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val timeString = timeFormat.format(Date(log.timestamp))

    // Color coding based on the type of event
    val tagColor = when(log.type) {
        "SYSTEM" -> CyberCyan
        "DEV_NOTE" -> SecondaryGray    // Gray for manual notes
        else -> PrimaryBlack
    }

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Text("[$timeString]", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = SecondaryGray.copy(alpha = 0.7f), modifier = Modifier.width(70.dp))
        Text(log.type, fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = tagColor, modifier = Modifier.width(85.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(log.message, fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = PrimaryBlack, lineHeight = 16.sp)
            // Show a tiny indicator if it hasn't synced to Firebase yet
            if (!log.isSynced) {
                Text("Pending Sync...", fontSize = 8.sp, color = ErrorRed, modifier = Modifier.padding(top = 2.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OfflineTerminalScreenPreview() {
    LudumForgeTheme {
        OfflineTerminalContent(
            commandInput = "sample note",
            sessionTimerSeconds = 125L,
            terminalLogs = listOf(
                ActionLog(
                    id = "1",
                    projectId = "p1",
                    message = "System initialized",
                    type = "SYSTEM",
                    timestamp = System.currentTimeMillis(),
                    isSynced = true
                ),
                ActionLog(
                    id = "2",
                    projectId = "p1",
                    message = "Working on UI",
                    type = "DEV_NOTE",
                    timestamp = System.currentTimeMillis() - 10000,
                    isSynced = false
                )
            ),
            isSyncing = false,
            onCommandInputChange = {},
            onManualSync = {},
            onSubmitNote = {}
        )
    }
}
