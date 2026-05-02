package dam.a51319.ludumforge.ui.screens
import dam.a51319.ludumforge.data.SessionManager


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dam.a51319.ludumforge.models.*
import dam.a51319.ludumforge.ui.theme.*
import dam.a51319.ludumforge.viewmodels.AuthViewModel
import dam.a51319.ludumforge.viewmodels.PersonalDashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalDashboardScreen(
    viewModel: PersonalDashboardViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel(),
    onLogout: () -> Unit,
    onNavigateToSubscription: () -> Unit = {}
) {
    val timeLeft by viewModel.timeLeftInSeconds.collectAsState()
    val priorityTasks by viewModel.myTasks.collectAsState()
    val activeJamId by SessionManager.activeJamId.collectAsState()
    val myJams by viewModel.myJams.collectAsState()
    val hours = timeLeft / 3600
    val minutes = (timeLeft % 3600) / 60
    val timeDisplay = if (timeLeft < 0L) "--" else "${hours}h ${minutes}m"
    val completionRatios by viewModel.completionRatios.collectAsState()
    val currentPlan by viewModel.currentPlan.collectAsState()

    // ── Jam limit snackbar ───────────────────────────────────────────────────
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        viewModel.jamLimitReached.collect {
            val result = snackbarHostState.showSnackbar(
                message = "Free plan limit reached (2 jams/month)",
                actionLabel = "Upgrade",
                duration = SnackbarDuration.Long
            )
            if (result == SnackbarResult.ActionPerformed) {
                onNavigateToSubscription()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = SurfaceBase
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(top = 24.dp, bottom = 100.dp)
        ) {
            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Text("OVERVIEW", style = MaterialTheme.typography.labelLarge, color = SecondaryGray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Personal Dashboard", style = MaterialTheme.typography.headlineLarge, color = PrimaryBlack)
                    Spacer(modifier = Modifier.height(32.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard(modifier = Modifier.weight(1f), value = timeDisplay, label = "Time\nRemaining")
                        StatCard(modifier = Modifier.weight(1f), value = "${priorityTasks.size}", label = "Tasks\nDue")
                        StatCard(modifier = Modifier.weight(1f), value = "84%", label = "Avg.\nVelocity")
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                    Text("ACTIVE PROJECTS", style = MaterialTheme.typography.labelLarge, color = SecondaryGray)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(myJams) { project ->
                        ActiveProjectCard(
                            project = project,
                            isActive = project.id == activeJamId,
                            completionRatio = completionRatios[project.id] ?: 0f,
                            onSelectJam = { SessionManager.setActiveJam(project.id, project.name) },
                            onRename = { newName -> viewModel.renameJam(project.id, newName) },
                            onDelete = { viewModel.deleteJam(project.id) }
                        )
                    }
                    item {
                        CreateJamCard(
                            onCreate = { name, days -> viewModel.createNewJam(name, "", days) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Text("YOUR PRIORITY TASKS", style = MaterialTheme.typography.labelLarge, color = SecondaryGray)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            items(priorityTasks) { task ->
                Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                    PriorityTaskCard(
                        task = task,
                        projectName = myJams.find { it.id == task.projectId }?.name ?: "Unknown Project",
                        onStatusChange = { taskId, newStatus -> viewModel.updateTaskStatus(taskId, newStatus) }
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(modifier: Modifier = Modifier, value: String, label: String) {
    Card(
        modifier = modifier.shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp), spotColor = PrimaryBlack.copy(alpha = 0.05f)),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, GhostBorder)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryBlack)
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SecondaryGray, textAlign = androidx.compose.ui.text.style.TextAlign.Center, lineHeight = 14.sp)
        }
    }
}

@Composable
fun ActiveProjectCard(
    project: Project,
    isActive: Boolean = false,
    completionRatio: Float = 0f,
    onSelectJam: () -> Unit = {},
    onRename: (String) -> Unit = {},
    onDelete: () -> Unit = {}
) {
    var showOptionsMenu by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var renameInput by remember { mutableStateOf(project.name) }

    Card(
        modifier = Modifier
            .width(280.dp)
            .shadow(elevation = if (isActive) 12.dp else 8.dp, shape = RoundedCornerShape(16.dp), spotColor = PrimaryBlack.copy(alpha = 0.08f))
            .clickable { onSelectJam() },
        colors = CardDefaults.cardColors(containerColor = if (isActive) PrimaryBlack else SurfaceContainerLowest),
        shape = RoundedCornerShape(16.dp),
        border = if (isActive) null else BorderStroke(1.dp, GhostBorder)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Completion",
                    fontSize = 10.sp,
                    color = if (isActive) Color.White.copy(alpha = 0.6f) else SecondaryGray
                )
                Text(
                    "${(completionRatio * 100).toInt()}%",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isActive) Color.White else PrimaryBlack
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { completionRatio },
                modifier = Modifier.fillMaxWidth().height(3.dp).clip(RoundedCornerShape(2.dp)),
                color = if (isActive) Color.White else PrimaryBlack,
                trackColor = if (isActive) Color.White.copy(alpha = 0.2f) else SurfaceContainerHigh
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isActive) Color.White.copy(alpha = 0.15f) else SurfaceContainerHigh)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        if (isActive) "● ACTIVE JAM" else project.status.name,
                        fontSize = 10.sp, fontWeight = FontWeight.Bold,
                        color = if (isActive) Color.White else PrimaryBlack,
                        letterSpacing = 0.5.sp
                    )
                }
                Box {
                    IconButton(onClick = { showOptionsMenu = true }, modifier = Modifier.size(24.dp)) {
                        Icon(
                            Icons.Default.MoreHoriz, contentDescription = "Options",
                            tint = if (isActive) Color.White.copy(alpha = 0.6f) else SecondaryGray
                        )
                    }
                    DropdownMenu(
                        expanded = showOptionsMenu,
                        onDismissRequest = { showOptionsMenu = false },
                        modifier = Modifier.background(SurfaceContainerLowest)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Rename", color = PrimaryBlack) },
                            onClick = { renameInput = project.name; showOptionsMenu = false; showRenameDialog = true },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = PrimaryBlack, modifier = Modifier.size(16.dp)) }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete", color = ErrorRed) },
                            onClick = { showOptionsMenu = false; showDeleteDialog = true },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(16.dp)) }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                project.name,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                color = if (isActive) Color.White else PrimaryBlack,
                maxLines = 1, overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Theme: ${project.theme}",
                style = MaterialTheme.typography.bodyLarge,
                color = if (isActive) Color.White.copy(alpha = 0.7f) else OnSurfaceVariant,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { onSelectJam() },
                modifier = Modifier.fillMaxWidth().height(44.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (isActive) Color.White else Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().then(
                        if (isActive) Modifier.background(Color.Transparent)
                        else Modifier.background(Brush.linearGradient(listOf(PrimaryBlack, PrimaryContainerDark)))
                    ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (isActive) "✓ Selected" else "Select Jam",
                        color = if (isActive) PrimaryBlack else Color.White,
                        fontWeight = FontWeight.Bold, fontSize = 13.sp
                    )
                }
                if (showRenameDialog) {
                    AlertDialog(
                        onDismissRequest = { showRenameDialog = false },
                        containerColor = SurfaceContainerLowest,
                        title = { Text("Rename Jam", fontWeight = FontWeight.Bold, color = PrimaryBlack) },
                        text = {
                            OutlinedTextField(
                                value = renameInput, onValueChange = { renameInput = it },
                                label = { Text("Jam Name") },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryBlack)
                            )
                        },
                        confirmButton = {
                            Button(
                                onClick = { onRename(renameInput); showRenameDialog = false },
                                enabled = renameInput.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack)
                            ) { Text("Save", color = Color.White) }
                        },
                        dismissButton = { TextButton(onClick = { showRenameDialog = false }) { Text("Cancel", color = PrimaryBlack) } }
                    )
                }
                if (showDeleteDialog) {
                    AlertDialog(
                        onDismissRequest = { showDeleteDialog = false },
                        containerColor = SurfaceContainerLowest,
                        title = { Text("Delete Jam?", fontWeight = FontWeight.Bold, color = PrimaryBlack) },
                        text = {
                            Text(
                                "\"${project.name}\" and all its tasks will be permanently deleted. This cannot be undone.",
                                color = OnSurfaceVariant
                            )
                        },
                        confirmButton = {
                            Button(
                                onClick = { onDelete(); showDeleteDialog = false },
                                colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                            ) { Text("Delete", color = Color.White) }
                        },
                        dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel", color = PrimaryBlack) } }
                    )
                }
            }
        }
    }
}

@Composable
fun CreateJamCard(onCreate: (String, Int) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    var jamName by remember { mutableStateOf("") }
    var selectedDays by remember { mutableStateOf(7) }
    val durationOptions = listOf(1, 2, 3, 7, 14, 30, 48)
    var showDurationMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.width(220.dp).clickable { showDialog = true },
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, GhostBorder)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp).height(160.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = SecondaryGray, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text("New Jam", fontWeight = FontWeight.Bold, color = SecondaryGray)
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor = SurfaceContainerLowest,
            title = { Text("Forge New Jam", fontWeight = FontWeight.Bold, color = PrimaryBlack) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = jamName, onValueChange = { jamName = it },
                        label = { Text("Jam Name") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryBlack)
                    )
                    Text("Duration", style = MaterialTheme.typography.labelLarge, color = SecondaryGray)
                    Box {
                        OutlinedButton(
                            onClick = { showDurationMenu = true },
                            modifier = Modifier.fillMaxWidth(),
                            border = BorderStroke(1.dp, GhostBorder),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                when (selectedDays) {
                                    1 -> "1 day"; 7 -> "1 week"; 14 -> "2 weeks"; 30 -> "1 month"
                                    48 -> "48h (classic jam)"; else -> "$selectedDays days"
                                },
                                color = PrimaryBlack
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = SecondaryGray)
                        }
                        DropdownMenu(
                            expanded = showDurationMenu,
                            onDismissRequest = { showDurationMenu = false },
                            modifier = Modifier.background(SurfaceContainerLowest)
                        ) {
                            durationOptions.forEach { days ->
                                DropdownMenuItem(
                                    text = {
                                        Text(when (days) {
                                            1 -> "1 day"; 7 -> "1 week"; 14 -> "2 weeks"; 30 -> "1 month"
                                            48 -> "48h (classic jam)"; else -> "$days days"
                                        })
                                    },
                                    onClick = { selectedDays = days; showDurationMenu = false }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { onCreate(jamName, selectedDays); showDialog = false; jamName = ""; selectedDays = 7 },
                    enabled = jamName.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack)
                ) { Text("Create", color = Color.White) }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancel", color = PrimaryBlack) } }
        )
    }
}

@Composable
fun PriorityTaskCard(
    task: Task,
    projectName: String,
    onStatusChange: (String, TaskStatus) -> Unit = { _, _ -> }
) {
    val isDone = task.status == TaskStatus.DONE
    val isInProgress = task.status == TaskStatus.IN_PROGRESS
    val statusText = when (task.status) {
        TaskStatus.IN_PROGRESS -> "In Progress"
        TaskStatus.TODO -> "To Do"
        TaskStatus.REVIEW -> "Review"
        TaskStatus.DONE -> "Done"
    }
    var showMenu by remember { mutableStateOf(false) }

    Box {
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).clickable { showMenu = true },
            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, GhostBorder)
        ) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape)
                        .background(if (isDone) SurfaceContainerLowest else SurfaceContainerHigh)
                        .border(1.dp, if (isDone) GhostBorder else Color.Transparent, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (isDone) Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = PrimaryBlack, modifier = Modifier.size(20.dp))
                    else if (isInProgress) Icon(Icons.Outlined.Schedule, contentDescription = null, tint = PrimaryBlack, modifier = Modifier.size(20.dp))
                    else Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(SecondaryGray))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(task.title, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = PrimaryBlack, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(projectName, fontSize = 12.sp, color = SecondaryGray)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(4.dp))
                        .background(if (isInProgress) PrimaryBlack else SurfaceContainerHigh)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(statusText, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (isInProgress) SurfaceContainerLowest else PrimaryBlack, letterSpacing = 0.5.sp)
                }
            }
        }
        DropdownMenu(
            expanded = showMenu, onDismissRequest = { showMenu = false },
            modifier = Modifier.background(SurfaceContainerLowest)
        ) {
            Text(" Move to...", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SecondaryGray, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
            TaskStatus.entries.forEach { status ->
                if (status != task.status) {
                    DropdownMenuItem(
                        text = { Text(status.name.replace("_", " ")) },
                        onClick = { showMenu = false; onStatusChange(task.id, status) }
                    )
                }
            }
        }
    }
}
