package dam.a51319.ludumforge.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dam.a51319.ludumforge.models.*
import dam.a51319.ludumforge.ui.theme.*
import dam.a51319.ludumforge.viewmodels.TeamWorkspaceViewModel
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamWorkspaceScreen(viewModel: TeamWorkspaceViewModel = viewModel()) {
    val teamTasks by viewModel.teamTasks.collectAsState()
    val groupedTasks = teamTasks.groupBy { it.status }

    // State for the Bottom Sheet
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    // Form states
    var newTaskTitle by remember { mutableStateOf("") }
    var newTaskMinutes by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(TaskCategory.CODE) }

    val dummyUsers = listOf(
        User("u1", "JD", "jd@test.com", UserRole.DEVELOPER),
        User("u2", "AK", "ak@test.com", UserRole.ARTIST),
        User("u3", "LW", "lw@test.com", UserRole.AUDIO_ENGINEER),
        User("u4", "SM", "sm@test.com", UserRole.DEVELOPER),
        User("u5", "TH", "th@test.com", UserRole.GAME_DESIGNER),
        User("u6", "RB", "rb@test.com", UserRole.DEVELOPER),
        User("u7", "QA", "qa@test.com", UserRole.ADMIN)
    )
    val context = LocalContext.current


    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showBottomSheet = true },
                containerColor = PrimaryBlack,
                contentColor = SurfaceContainerLowest,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(Icons.Default.AddTask, contentDescription = "Add Task")
            }
        },
        containerColor = SurfaceBase
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 24.dp),
            contentPadding = PaddingValues(top = 24.dp, bottom = 100.dp)
        ) {
            item {
                Text("STUDIO WORKSPACE", style = MaterialTheme.typography.labelLarge, color = SecondaryGray)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Team Workspace", style = MaterialTheme.typography.headlineLarge, color = PrimaryBlack)
                Spacer(modifier = Modifier.height(24.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TextField(
                        value = "",
                        onValueChange = {},
                        placeholder = { Text("Filter tasks...", color = SecondaryGray, style = MaterialTheme.typography.bodyLarge) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SecondaryGray, modifier = Modifier.size(20.dp)) },
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = SurfaceContainerHigh,
                            unfocusedContainerColor = SurfaceContainerHigh,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true
                    )
                }
                Spacer(modifier = Modifier.height(40.dp))
            }

            item { ColumnHeader("TO DO", groupedTasks[TaskStatus.TODO]?.size ?: 0) }
            items(groupedTasks[TaskStatus.TODO] ?: emptyList()) { task ->
                TaskCard(task, dummyUsers) { id, status -> viewModel.updateTaskStatus(id, status, task.title, context) }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item { ColumnHeader("IN PROGRESS", groupedTasks[TaskStatus.IN_PROGRESS]?.size ?: 0) }
            items(groupedTasks[TaskStatus.IN_PROGRESS] ?: emptyList()) { task ->
                TaskCard(task, dummyUsers) { id, status -> viewModel.updateTaskStatus(id, status, task.title, context) }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item { ColumnHeader("DONE", groupedTasks[TaskStatus.DONE]?.size ?: 0) }
            items(groupedTasks[TaskStatus.DONE] ?: emptyList()) { task ->
                TaskCard(task, dummyUsers) { id, status -> viewModel.updateTaskStatus(id, status, task.title, context) }
            }        }
    }
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = SurfaceBase
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text("Create New Task", style = MaterialTheme.typography.titleLarge, color = PrimaryBlack, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = newTaskTitle,
                    onValueChange = { newTaskTitle = it },
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
                    value = newTaskMinutes,
                    onValueChange = { newTaskMinutes = it },
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

                // Simple Category Picker
                Text("Category", style = MaterialTheme.typography.labelLarge, color = SecondaryGray)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TaskCategory.values().forEach { category ->
                        FilterChip(
                            selected = category == selectedCategory,
                            onClick = { selectedCategory = category },
                            label = { Text(category.name) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryBlack,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        viewModel.addTask(newTaskTitle, selectedCategory, newTaskMinutes.toIntOrNull() ?: 60, context)
                        showBottomSheet = false
                        newTaskTitle = ""
                        newTaskMinutes = ""
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack)
                ) {
                    Text("Forge Task", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ColumnHeader(title: String, count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(10.dp).clip(CircleShape).border(2.dp, PrimaryBlack, CircleShape))
            Spacer(modifier = Modifier.width(12.dp))
            Text(title, style = MaterialTheme.typography.labelLarge, color = PrimaryBlack)
        }
        Surface(color = SurfaceContainerHigh, shape = CircleShape) {
            Text(count.toString(), modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.labelLarge, color = PrimaryBlack)
        }
    }
}

@Composable
fun TaskCard(
    task: Task,
    allUsers: List<User>,
    onStatusChange: (String, TaskStatus) -> Unit
) {
    val isDone = task.status == TaskStatus.DONE
    val isInProgress = task.status == TaskStatus.IN_PROGRESS
    val assigneeIds = task.assignedTo?.split(",") ?: emptyList()
    val assignees = allUsers.filter { assigneeIds.contains(it.id) }
    val contentAlpha = if (isDone) 0.5f else 1f

    // State for the dropdown menu
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .shadow(elevation = if (isDone) 0.dp else 4.dp, shape = RoundedCornerShape(12.dp), spotColor = PrimaryBlack.copy(alpha = 0.05f))
            .clickable { showMenu = true }, // <--- TRIGGER MENU ON CLICK
        colors = CardDefaults.cardColors(containerColor = if (isDone) SurfaceBase else SurfaceContainerLowest),
        shape = RoundedCornerShape(12.dp),
        border = if (isDone) BorderStroke(1.dp, GhostBorder) else null
    ) {
        Box { // Wrap content in a Box so the DropdownMenu anchors correctly
            Column(modifier = Modifier.padding(20.dp).fillMaxWidth()) {
                Text(task.category.name, style = MaterialTheme.typography.labelLarge, color = if (isInProgress) PrimaryBlack else SecondaryGray.copy(alpha = contentAlpha))
                Spacer(modifier = Modifier.height(16.dp))
                Text(task.title, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), textDecoration = if (isDone) TextDecoration.LineThrough else null, color = PrimaryBlack.copy(alpha = contentAlpha))

                if (isInProgress) {
                    Spacer(modifier = Modifier.height(20.dp))
                    LinearProgressIndicator(progress = { 0.65f }, modifier = Modifier.fillMaxWidth().height(2.dp), color = PrimaryBlack, trackColor = SurfaceContainerHigh)
                }

                Spacer(modifier = Modifier.height(20.dp))
                Row(horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
                    assignees.forEach { user ->
                        Box(modifier = Modifier.size(30.dp).clip(CircleShape).background(SurfaceContainerHigh).border(2.dp, SurfaceContainerLowest, CircleShape), contentAlignment = Alignment.Center) {
                            Text(user.username, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryBlack)
                        }
                    }
                }
            }

            // The Status Picker Menu
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                modifier = Modifier.background(SurfaceContainerLowest)
            ) {
                Text(" Move to...", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SecondaryGray, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
                TaskStatus.entries.forEach { status ->
                    if (status != task.status) { // Only show options that are different from current
                        DropdownMenuItem(
                            text = { Text(status.name.replace("_", " ")) },
                            onClick = {
                                showMenu = false
                                onStatusChange(task.id, status)
                            }
                        )
                    }
                }
            }
        }
    }
}