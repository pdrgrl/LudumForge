package dam.a51319.ludumforge.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dam.a51319.ludumforge.data.SessionManager
import dam.a51319.ludumforge.models.*
import dam.a51319.ludumforge.ui.theme.*
import dam.a51319.ludumforge.viewmodels.PersonalDashboardViewModel
import dam.a51319.ludumforge.viewmodels.TeamWorkspaceViewModel
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.ExperimentalFoundationApi
import kotlinx.coroutines.launch

// ── Task Card ─────────────────────────────────────────────────────────────────
import androidx.compose.material.icons.automirrored.filled.Label
import dam.a51319.ludumforge.models.UserRole

// ─── Category colour map ────────────────────────────────────────────────────
private fun categoryColor(category: TaskCategory): Color = when (category) {
    TaskCategory.CODE   -> Color(0xFF4A90D9)
    TaskCategory.ART    -> Color(0xFF9B59B6)
    TaskCategory.AUDIO  -> Color(0xFFE67E22)
    TaskCategory.DESIGN -> Color(0xFF1ABC9C)
    TaskCategory.QA     -> Color(0xFFE74C3C)
    TaskCategory.OTHER  -> Color(0xFF95A5A6)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TeamWorkspaceScreen(
    viewModel: TeamWorkspaceViewModel = viewModel(),
    dashboardViewModel: PersonalDashboardViewModel = viewModel()
) {
    val teamTasks by viewModel.teamTasks.collectAsState()
    val realUsers by viewModel.teamMembers.collectAsState()
    val activeJamId by SessionManager.activeJamId.collectAsState()
    val activeJamName by SessionManager.activeJamName.collectAsState()
    val context = LocalContext.current

    // ── Pending invite state ───────────────────────────────────────────
    val pendingInviteJam by dashboardViewModel.pendingInviteJam.collectAsState()

    TeamWorkspaceContent(
        teamTasks = teamTasks,
        realUsers = realUsers,
        activeJamId = activeJamId,
        activeJamName = activeJamName,
        pendingInviteJam = pendingInviteJam,
        onDeleteTask = { id, title -> viewModel.deleteTask(id, title, context) },
        onUpdateTaskStatus = { id, status, title -> viewModel.updateTaskStatus(id, status, title, context) },
        onAddTask = { title, cat, mins, user -> viewModel.addTask(title, cat, mins, context, user) },
        onUpdateTask = { id, title, cat, mins, user -> viewModel.updateTask(id, title, cat, mins, user, context) },
        onClearPendingInvite = { dashboardViewModel.clearPendingInvite() },
        onAcceptInvite = { dashboardViewModel.acceptInvite() }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TeamWorkspaceContent(
    teamTasks: List<Task>,
    realUsers: List<User>,
    activeJamId: String?,
    activeJamName: String?,
    pendingInviteJam: Project?,
    onDeleteTask: (String, String) -> Unit,
    onUpdateTaskStatus: (String, TaskStatus, String) -> Unit,
    onAddTask: (String, TaskCategory, Int, String?) -> Unit,
    onUpdateTask: (String, String, TaskCategory, Int, String?) -> Unit,
    onClearPendingInvite: () -> Unit,
    onAcceptInvite: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // ── Invite sheet state ───────────────────────────────────────────
    val inviteSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // ── Add-sheet state ──────────────────────────────────────────────
    val addSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showAddSheet by remember { mutableStateOf(false) }
    var newTaskTitle by remember { mutableStateOf("") }
    var newTaskMinutes by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(TaskCategory.CODE) }
    var selectedAssignee by remember { mutableStateOf<User?>(null) }

    // ── Edit-sheet state ──────────────────────────────────────────────
    val editSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var editingTask by remember { mutableStateOf<Task?>(null) }
    var editTitle by remember { mutableStateOf("") }
    var editMinutes by remember { mutableStateOf("") }
    var editCategory by remember { mutableStateOf(TaskCategory.CODE) }
    var editAssignee by remember { mutableStateOf<User?>(null) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    // ── Filter ───────────────────────────────────────────────────────────
    var filterQuery by remember { mutableStateOf("") }
    val filteredTasks = remember(teamTasks, filterQuery) {
        if (filterQuery.isBlank()) teamTasks
        else teamTasks.filter { it.title.contains(filterQuery, ignoreCase = true) }
    }
    val groupedTasks = filteredTasks.groupBy { it.status }

    // ── Delete confirm dialog ─────────────────────────────────────────
    if (showDeleteConfirm && editingTask != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Task", fontWeight = FontWeight.Bold, color = PrimaryBlack) },
            text = { Text("Are you sure you want to delete \"${editingTask!!.title}\"? This cannot be undone.", color = SecondaryGray) },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteTask(editingTask!!.id, editingTask!!.title)
                    showDeleteConfirm = false
                    editingTask = null
                }) {
                    Text("Delete", color = ErrorRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            },
            containerColor = SurfaceContainerLowest
        )
    }

    // ── Invite confirm bottom sheet ─────────────────────────────────
    if (pendingInviteJam != null) {
        ModalBottomSheet(
            onDismissRequest = { onClearPendingInvite() },
            sheetState = inviteSheetState,
            containerColor = SurfaceBase
        ) {
            InviteJamSheet(
                jamName = pendingInviteJam.name,
                onAccept = {
                    onAcceptInvite()
                    scope.launch {
                        snackbarHostState.showSnackbar("Joined \"${pendingInviteJam.name}\"! Find it in your dashboard.")
                    }
                },
                onDecline = { onClearPendingInvite() }
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (activeJamId != null) {
                FloatingActionButton(
                    onClick = { showAddSheet = true },
                    containerColor = MoltenOrange,
                    contentColor = Color.White,
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(8.dp)
                ) {
                    Icon(Icons.Default.AddTask, contentDescription = "Add Task")
                }
            }
        },
        containerColor = SurfaceBase
    ) { innerPadding ->

        if (activeJamId == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.FolderOff, contentDescription = null,
                        tint = SecondaryGray.copy(alpha = 0.4f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text("No Active Jam", style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold, color = PrimaryBlack)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Head to the Dashboard and select or create a Jam to start managing tasks.",
                        style = MaterialTheme.typography.bodyLarge, color = SecondaryGray,
                        textAlign = TextAlign.Center, lineHeight = 22.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp),
                contentPadding = PaddingValues(top = 24.dp, bottom = 100.dp)
            ) {
                item {
                    Text("STUDIO WORKSPACE", style = MaterialTheme.typography.labelLarge, color = SecondaryGray)
                    Spacer(modifier = Modifier.height(8.dp))
                    // ── Header row: title + share button ────────────────────────
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            activeJamName ?: "Team Workspace",
                            style = MaterialTheme.typography.headlineLarge,
                            color = PrimaryBlack
                        )
                        // Share / invite button — only shown when a jam is active
                        IconButton(
                            onClick = {
                                val link = "ludumforge://join?jamId=$activeJamId"
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("LudumForge Invite", link))
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Invite link copied! Share it with your team.",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        ) {
                            Icon(
                                Icons.Default.Share,
                                contentDescription = "Invite collaborators",
                                tint = PrimaryBlack
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    TextField(
                        value = filterQuery,
                        onValueChange = { filterQuery = it },
                        placeholder = { Text("Filter tasks...", color = SecondaryGray, style = MaterialTheme.typography.bodyLarge) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SecondaryGray, modifier = Modifier.size(20.dp)) },
                        trailingIcon = {
                            if (filterQuery.isNotBlank()) {
                                IconButton(onClick = { filterQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = SecondaryGray, modifier = Modifier.size(16.dp))
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = SurfaceContainerHigh,
                            unfocusedContainerColor = SurfaceContainerHigh,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(40.dp))
                }

                listOf(TaskStatus.TODO, TaskStatus.IN_PROGRESS, TaskStatus.REVIEW, TaskStatus.DONE).forEach { status ->
                    val label = when (status) {
                        TaskStatus.TODO -> "TO DO"
                        TaskStatus.IN_PROGRESS -> "IN PROGRESS"
                        TaskStatus.REVIEW -> "IN REVIEW"
                        TaskStatus.DONE -> "DONE"
                    }
                    item { ColumnHeader(label, groupedTasks[status]?.size ?: 0) }
                    items(groupedTasks[status] ?: emptyList()) { task ->
                        TaskCard(
                            task = task,
                            allUsers = realUsers,
                            onStatusChange = { id, newStatus -> onUpdateTaskStatus(id, newStatus, task.title) },
                            onLongPress = {
                                editingTask = task
                                editTitle = task.title
                                editMinutes = if (task.estimatedMinutes > 0) task.estimatedMinutes.toString() else ""
                                editCategory = task.category
                                editAssignee = realUsers.find { it.id == task.assignedTo }
                            }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }

    // ── Add Task Bottom Sheet ────────────────────────────────────────────
    if (showAddSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddSheet = false },
            sheetState = addSheetState,
            containerColor = SurfaceBase
        ) {
            TaskFormSheet(
                title = "Create New Task",
                taskTitle = newTaskTitle,
                taskMinutes = newTaskMinutes,
                taskCategory = selectedCategory,
                taskAssignee = selectedAssignee,
                allUsers = realUsers,
                onTitleChange = { newTaskTitle = it },
                onMinutesChange = { newTaskMinutes = it },
                onCategoryChange = { selectedCategory = it },
                onAssigneeChange = { selectedAssignee = it },
                trailingAction = null,
                onConfirm = {
                    onAddTask(newTaskTitle, selectedCategory, newTaskMinutes.toIntOrNull() ?: 60, selectedAssignee?.id)
                    showAddSheet = false
                    newTaskTitle = ""
                    newTaskMinutes = ""
                    selectedAssignee = null
                },
                confirmLabel = "Forge Task"
            )
        }
    }

    // ── Edit Task Bottom Sheet ───────────────────────────────────────────
    if (editingTask != null) {
        ModalBottomSheet(
            onDismissRequest = { editingTask = null },
            sheetState = editSheetState,
            containerColor = SurfaceBase
        ) {
            TaskFormSheet(
                title = "Edit Task",
                taskTitle = editTitle,
                taskMinutes = editMinutes,
                taskCategory = editCategory,
                taskAssignee = editAssignee,
                allUsers = realUsers,
                onTitleChange = { editTitle = it },
                onMinutesChange = { editMinutes = it },
                onCategoryChange = { editCategory = it },
                onAssigneeChange = { editAssignee = it },
                trailingAction = {
                    IconButton(onClick = { showDeleteConfirm = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Task", tint = ErrorRed)
                    }
                },
                onConfirm = {
                    onUpdateTask(editingTask!!.id, editTitle, editCategory, editMinutes.toIntOrNull() ?: 60, editAssignee?.id)
                    editingTask = null
                },
                confirmLabel = "Save Changes"
            )
        }
    }
}

// ── Invite Sheet Content ──────────────────────────────────────────────────
@Composable
private fun InviteJamSheet(
    jamName: String,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.Groups,
            contentDescription = null,
            tint = PrimaryBlack,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "You've been invited!",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = PrimaryBlack
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Join jam:",
            style = MaterialTheme.typography.bodyLarge,
            color = SecondaryGray
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            jamName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = PrimaryBlack
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onAccept,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MoltenOrange)
        ) {
            Text("Join Jam", color = Color.White, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(12.dp))
        TextButton(
            onClick = onDecline,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Decline", color = SecondaryGray)
        }
    }
}
@Composable
private fun TaskFormSheet(
    title: String,
    taskTitle: String,
    taskMinutes: String,
    taskCategory: TaskCategory,
    taskAssignee: User?,
    allUsers: List<User>,
    onTitleChange: (String) -> Unit,
    onMinutesChange: (String) -> Unit,
    onCategoryChange: (TaskCategory) -> Unit,
    onAssigneeChange: (User?) -> Unit,
    trailingAction: (@Composable () -> Unit)?,
    onConfirm: () -> Unit,
    confirmLabel: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .padding(bottom = 32.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, style = MaterialTheme.typography.titleLarge, color = PrimaryBlack, fontWeight = FontWeight.Bold)
            trailingAction?.invoke()
        }
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = taskTitle,
            onValueChange = onTitleChange,
            label = { Text("Task Title") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlack,
                focusedLabelColor = PrimaryBlack,
                unfocusedBorderColor = GhostBorder
            )
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = taskMinutes,
            onValueChange = onMinutesChange,
            label = { Text("Estimated Minutes") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlack,
                focusedLabelColor = PrimaryBlack,
                unfocusedBorderColor = GhostBorder
            )
        )
        Spacer(modifier = Modifier.height(24.dp))

        Text("Category", style = MaterialTheme.typography.labelLarge, color = SecondaryGray)
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TaskCategory.entries.forEach { category ->
                FilterChip(
                    selected = category == taskCategory,
                    onClick = { onCategoryChange(category) },
                    label = { Text(category.name) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MoltenOrange,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Assign To", style = MaterialTheme.typography.labelLarge, color = SecondaryGray)
        Spacer(modifier = Modifier.height(8.dp))
        if (allUsers.isEmpty()) {
            Text("No team members found.", fontSize = 12.sp, color = SecondaryGray)
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                FilterChip(
                    selected = taskAssignee == null,
                    onClick = { onAssigneeChange(null) },
                    label = { Text("None") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MoltenOrange,
                        selectedLabelColor = Color.White
                    )
                )
                allUsers.forEach { user ->
                    val initials = user.username.split(" ", "_", ".").take(2)
                        .joinToString("") { it.first().uppercaseChar().toString() }
                        .ifBlank { user.username.take(2).uppercase() }
                    FilterChip(
                        selected = taskAssignee?.id == user.id,
                        onClick = { onAssigneeChange(user) },
                        leadingIcon = {
                            Box(
                                modifier = Modifier.size(20.dp).clip(CircleShape)
                                    .background(if (taskAssignee?.id == user.id) Color.White.copy(alpha = 0.25f) else SurfaceContainerHigh),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(initials, fontSize = 8.sp, fontWeight = FontWeight.Bold,
                                    color = if (taskAssignee?.id == user.id) Color.White else PrimaryBlack)
                            }
                        },
                        label = { Text(user.username) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MoltenOrange,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onConfirm,
            enabled = taskTitle.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MoltenOrange)
        ) {
            Text(confirmLabel, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

// ── Column header ────────────────────────────────────────────────────────────
@Composable
fun ColumnHeader(title: String, count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(10.dp).clip(CircleShape).border(2.dp, MoltenOrange, CircleShape))
            Spacer(modifier = Modifier.width(12.dp))
            Text(title, style = MaterialTheme.typography.labelLarge, color = PrimaryBlack)
        }
        Surface(color = SurfaceContainerHigh, shape = CircleShape) {
            Text(count.toString(), modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelLarge, color = PrimaryBlack)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskCard(
    task: Task,
    allUsers: List<User>,
    onStatusChange: (String, TaskStatus) -> Unit,
    onLongPress: () -> Unit = {}
) {
    val isDone = task.status == TaskStatus.DONE
    val isInProgress = task.status == TaskStatus.IN_PROGRESS
    val assigneeIds = task.assignedTo?.split(",") ?: emptyList()
    val assignees = allUsers.filter { assigneeIds.contains(it.id) }
    val contentAlpha = if (isDone) 0.5f else 1f
    var showMenu by remember { mutableStateOf(false) }
    val catColor = categoryColor(task.category)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .shadow(
                elevation = if (isDone) 0.dp else 4.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = PrimaryBlack.copy(alpha = 0.05f)
            )
            .combinedClickable(
                onClick = { showMenu = true },
                onLongClick = onLongPress
            ),
        colors = CardDefaults.cardColors(containerColor = if (isDone) SurfaceBase else SurfaceContainerLowest),
        shape = RoundedCornerShape(12.dp),
        border = if (isDone) BorderStroke(1.dp, GhostBorder) else null
    ) {
        Box {
            Column(modifier = Modifier.padding(20.dp).fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = catColor.copy(alpha = 0.12f)
                    ) {
                        Text(
                            task.category.name,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = catColor
                        )
                    }
                    if (task.estimatedMinutes > 0) {
                        Text(
                            "${task.estimatedMinutes}m",
                            fontSize = 11.sp,
                            color = SecondaryGray.copy(alpha = contentAlpha)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    task.title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    textDecoration = if (isDone) TextDecoration.LineThrough else null,
                    color = PrimaryBlack.copy(alpha = contentAlpha)
                )
                if (isInProgress) {
                    Spacer(modifier = Modifier.height(16.dp))
                    LinearProgressIndicator(
                        progress = { 0.65f },
                        modifier = Modifier.fillMaxWidth().height(2.dp),
                        color = MoltenOrange,
                        trackColor = SurfaceContainerHigh
                    )
                }
                if (assignees.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
                        assignees.forEach { user ->
                            val initials = user.username.split(" ", "_", ".").take(2)
                                .joinToString("") { it.first().uppercaseChar().toString() }
                                .ifBlank { user.username.take(2).uppercase() }
                            
                            val roleColor = when(user.role) {
                                UserRole.DEVELOPER -> Color(0xFF4A90D9)
                                UserRole.ARTIST -> Color(0xFF9B59B6)
                                UserRole.AUDIO_ENGINEER -> Color(0xFFE67E22)
                                else -> SecondaryGray
                            }

                            Box(
                                modifier = Modifier.size(30.dp),
                                contentAlignment = Alignment.BottomEnd
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize().clip(CircleShape)
                                        .background(SurfaceContainerHigh)
                                        .border(2.dp, SurfaceContainerLowest, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(initials, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryBlack)
                                }
                                // Role Badge
                                Box(
                                    modifier = Modifier.size(10.dp).clip(CircleShape)
                                        .background(roleColor).border(1.dp, Color.White, CircleShape)
                                )
                            }
                        }
                    }
                }
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                modifier = Modifier.background(SurfaceContainerLowest)
            ) {
                Text(" Move to...", fontSize = 10.sp, fontWeight = FontWeight.Bold,
                    color = SecondaryGray, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
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
}

@Preview(showBackground = true, name = "Active Workspace")
@Composable
fun TeamWorkspaceScreenPreview() {
    val sampleUsers = listOf(
        User(id = "u1", username = "Pedro", email = "pedro@example.com"),
        User(id = "u2", username = "John Doe", email = "john@example.com")
    )
    val sampleTasks = listOf(
        Task(id = "t1", title = "Design Main Menu", category = TaskCategory.DESIGN, status = TaskStatus.TODO, estimatedMinutes = 120),
        Task(id = "t2", title = "Implement Physics", category = TaskCategory.CODE, status = TaskStatus.IN_PROGRESS, estimatedMinutes = 240, assignedTo = "u1"),
        Task(id = "t3", title = "Bug Squashing", category = TaskCategory.QA, status = TaskStatus.REVIEW, estimatedMinutes = 60, assignedTo = "u1,u2"),
        Task(id = "t4", title = "Final Polish", category = TaskCategory.OTHER, status = TaskStatus.DONE, estimatedMinutes = 30)
    )

    LudumForgeTheme {
        TeamWorkspaceContent(
            teamTasks = sampleTasks,
            realUsers = sampleUsers,
            activeJamId = "jam_123",
            activeJamName = "Global Game Jam 2024",
            pendingInviteJam = null,
            onDeleteTask = { _, _ -> },
            onUpdateTaskStatus = { _, _, _ -> },
            onAddTask = { _, _, _, _ -> },
            onUpdateTask = { _, _, _, _, _ -> },
            onClearPendingInvite = {},
            onAcceptInvite = {}
        )
    }
}

@Preview(showBackground = true, name = "Empty Workspace")
@Composable
fun TeamWorkspaceEmptyPreview() {
    LudumForgeTheme {
        TeamWorkspaceContent(
            teamTasks = emptyList(),
            realUsers = emptyList(),
            activeJamId = null,
            activeJamName = null,
            pendingInviteJam = null,
            onDeleteTask = { _, _ -> },
            onUpdateTaskStatus = { _, _, _ -> },
            onAddTask = { _, _, _, _ -> },
            onUpdateTask = { _, _, _, _, _ -> },
            onClearPendingInvite = {},
            onAcceptInvite = {}
        )
    }
}

@Preview(showBackground = true, name = "Pending Invite")
@Composable
fun TeamWorkspaceInvitePreview() {
    val sampleJam = Project(id = "jam_456", name = "Cyberpunk Jam 2077", theme = "Neon Lights", status = ProjectStatus.PLANNING)

    LudumForgeTheme {
        TeamWorkspaceContent(
            teamTasks = emptyList(),
            realUsers = emptyList(),
            activeJamId = "jam_123",
            activeJamName = "Active Project",
            pendingInviteJam = sampleJam,
            onDeleteTask = { _, _ -> },
            onUpdateTaskStatus = { _, _, _ -> },
            onAddTask = { _, _, _, _ -> },
            onUpdateTask = { _, _, _, _, _ -> },
            onClearPendingInvite = {},
            onAcceptInvite = {}
        )
    }
}

@Preview(showBackground = true, name = "Task Card")
@Composable
fun TaskCardPreview() {
    val sampleUser = User(id = "u1", username = "Pedro")
    val sampleTask = Task(
        id = "t1",
        title = "Implement Character Movement",
        category = TaskCategory.CODE,
        status = TaskStatus.IN_PROGRESS,
        estimatedMinutes = 90,
        assignedTo = "u1"
    )

    LudumForgeTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            TaskCard(
                task = sampleTask,
                allUsers = listOf(sampleUser),
                onStatusChange = { _, _ -> }
            )
        }
    }
}

@Preview(showBackground = true, name = "Column Header")
@Composable
fun ColumnHeaderPreview() {
    LudumForgeTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ColumnHeader(title = "IN PROGRESS", count = 3)
        }
    }
}

@Preview(showBackground = true, name = "Invite Jam Sheet")
@Composable
fun InviteJamSheetPreview() {
    LudumForgeTheme {
        InviteJamSheet(
            jamName = "Global Game Jam 2024",
            onAccept = {},
            onDecline = {}
        )
    }
}

@Preview(showBackground = true, name = "Task Form Sheet")
@Composable
fun TaskFormSheetPreview() {
    val sampleUsers = listOf(
        User(id = "u1", username = "Pedro"),
        User(id = "u2", username = "John Doe")
    )
    LudumForgeTheme {
        TaskFormSheet(
            title = "Create New Task",
            taskTitle = "Build Physics Engine",
            taskMinutes = "120",
            taskCategory = TaskCategory.CODE,
            taskAssignee = sampleUsers[0],
            allUsers = sampleUsers,
            onTitleChange = {},
            onMinutesChange = {},
            onCategoryChange = {},
            onAssigneeChange = {},
            trailingAction = {
                IconButton(onClick = {}) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = ErrorRed)
                }
            },
            onConfirm = {},
            confirmLabel = "Forge Task"
        )
    }
}
