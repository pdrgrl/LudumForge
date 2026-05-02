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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dam.a51319.ludumforge.models.Task
import dam.a51319.ludumforge.models.TaskStatus
import dam.a51319.ludumforge.ui.theme.*
import dam.a51319.ludumforge.viewmodels.RoadmapGeneratorViewModel
import dam.a51319.ludumforge.viewmodels.RoadmapUiState
import androidx.compose.ui.platform.LocalContext
import android.content.Context
import androidx.compose.ui.text.style.TextDecoration
import dam.a51319.ludumforge.viewmodels.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoadmapGeneratorScreen(
    viewModel: RoadmapGeneratorViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel() // To check Premium status
) {
    val currentUser by authViewModel.currentUser.collectAsState()


    // Read API Key from SharedPreferences
    val context = LocalContext.current
    val sharedPrefs =
        remember { context.getSharedPreferences("LudumForgePrefs", Context.MODE_PRIVATE) }
    val savedApiKey = sharedPrefs.getString("gemini_api_key", "") ?: ""

    val isPremium = currentUser?.role?.name == "PREMIUM"

    // ViewModel State Collection
    val projectVision by viewModel.gameTitle.collectAsState()
    val teamSize by viewModel.teamSize.collectAsState()
    val projectHorizon by viewModel.duration.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    // Selection state — initialised to all tasks selected whenever Success arrives
    val selectedTaskIds = remember(uiState) {
        if (uiState is RoadmapUiState.Success) {
            mutableStateOf((uiState as RoadmapUiState.Success).tasks.map { it.id }.toMutableSet())
        } else {
            mutableStateOf(mutableSetOf())
        }
    }
    Scaffold(
        containerColor = SurfaceBase
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(top = 24.dp, bottom = 100.dp)
        ) {
            item {
                Text(
                    text = "Roadmap Generator",
                    style = MaterialTheme.typography.headlineLarge,
                    color = PrimaryBlack
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Architect your development cycle with precision. Input your vision to generate a surgically precise jam blueprint.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = OnSurfaceVariant
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Jam Vision Input
            item {
                Text(
                    "JAM VISION",
                    style = MaterialTheme.typography.labelLarge,
                    color = SecondaryGray
                )
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceContainerHigh)
                        .padding(16.dp)
                ) {
                    BasicTextField(
                        value = projectVision,
                        onValueChange = { viewModel.gameTitle.value = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 120.dp),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(color = PrimaryBlack),
                        decorationBox = { innerTextField ->
                            if (projectVision.isEmpty()) {
                                Text(
                                    text = "Describe your game idea, mechanics, and core loop...",
                                    color = SecondaryGray.copy(alpha = 0.7f),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                            innerTextField()
                        }
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Personnel Count
            item {
                Text(
                    "PERSONNEL COUNT",
                    style = MaterialTheme.typography.labelLarge,
                    color = SecondaryGray
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceContainerHigh)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Groups,
                        contentDescription = null,
                        tint = SecondaryGray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    BasicTextField(
                        value = teamSize,
                        onValueChange = { viewModel.teamSize.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(color = PrimaryBlack),
                        decorationBox = { innerTextField ->
                            if (teamSize.isEmpty()) Text(
                                "Team Size",
                                color = SecondaryGray.copy(alpha = 0.7f),
                                style = MaterialTheme.typography.bodyLarge
                            )
                            innerTextField()
                        }
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Jam Horizon
            item {
                Text(
                    "JAM HORIZON",
                    style = MaterialTheme.typography.labelLarge,
                    color = SecondaryGray
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceContainerHigh)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        tint = SecondaryGray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    BasicTextField(
                        value = projectHorizon,
                        onValueChange = { viewModel.duration.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(color = PrimaryBlack),
                        decorationBox = { innerTextField ->
                            if (projectHorizon.isEmpty()) Text(
                                "e.g. 6 Months",
                                color = SecondaryGray.copy(alpha = 0.7f),
                                style = MaterialTheme.typography.bodyLarge
                            )
                            innerTextField()
                        }
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Generate Button
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 16.dp,
                            shape = RoundedCornerShape(12.dp),
                            spotColor = PrimaryBlack.copy(alpha = 0.2f)
                        )
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    PrimaryBlack,
                                    PrimaryContainerDark
                                )
                            )
                        )
                        .clickable {
                            // Pass the key and premium status here!
                            viewModel.onGenerateClicked(
                                userApiKey = savedApiKey,
                                isPremium = isPremium
                            )
                        }
                        .padding(vertical = 18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Generate Roadmap",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }

            // UI State Display
            item {
                when (uiState) {
                    is RoadmapUiState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = PrimaryBlack)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    "Generating roadmap...",
                                    color = SecondaryGray,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    is RoadmapUiState.Pushing -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = PrimaryBlack)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    "Forging tasks into workspace...",
                                    color = SecondaryGray,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    is RoadmapUiState.Error -> {
                        Text(
                            text = (uiState as RoadmapUiState.Error).message,
                            color = ErrorRed,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }

                    is RoadmapUiState.Success -> {
                        Text(
                            "REVIEW ROADMAP",
                            style = MaterialTheme.typography.labelLarge,
                            color = SecondaryGray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Select the tasks you want to push to the Workspace.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = OnSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    else -> {
                        // Idle — Architect Protocol card (keep as-is)
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, GhostBorder)
                        ) {
                            Column(modifier = Modifier.padding(24.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Info,
                                        contentDescription = null,
                                        tint = PrimaryBlack,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Architect's Protocol",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryBlack
                                    )
                                }
                                Spacer(modifier = Modifier.height(24.dp))
                                ProtocolStep(
                                    "01",
                                    "Be specific about genre and target platforms to ensure asset pipeline accuracy."
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                ProtocolStep(
                                    "02",
                                    "Team size influences the parallelization of art and engineering sprints."
                                )
                            }
                        }
                    }
                }
            }

            // Show Tasks if Success
            // Selectable task list — only rendered in Success state
            if (uiState is RoadmapUiState.Success) {
                val allTasks = (uiState as RoadmapUiState.Success).tasks

                items(allTasks) { task ->
                    GeneratedTaskCard(
                        task = task,
                        isSelected = selectedTaskIds.value.contains(task.id),
                        onToggle = {
                            val updated = selectedTaskIds.value.toMutableSet()
                            if (updated.contains(task.id)) updated.remove(task.id)
                            else updated.add(task.id)
                            selectedTaskIds.value = updated
                        }
                    )
                }

                // ── Action Bar ──────────────────────────────────────────
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    val selectedTasks = allTasks.filter { selectedTaskIds.value.contains(it.id) }

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                        // Add Selected (primary)
                        Button(
                            onClick = {
                                viewModel.pushSelectedTasksToWorkspace(
                                    selectedTasks,
                                    context
                                )
                            },
                            enabled = selectedTasks.isNotEmpty(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack)
                        ) {
                            Icon(
                                Icons.Default.RocketLaunch,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Add Selected (${selectedTasks.size})",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Add All (secondary)
                        OutlinedButton(
                            onClick = { viewModel.pushSelectedTasksToWorkspace(allTasks, context) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, PrimaryBlack)
                        ) {
                            Text(
                                "Add All (${allTasks.size})",
                                color = PrimaryBlack,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Discard (ghost/destructive)
                        TextButton(
                            onClick = { viewModel.discard() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text("Discard", color = ErrorRed, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun ProtocolStep(number: String, description: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(SurfaceContainerHigh),
            contentAlignment = Alignment.Center
        ) {
            Text(
                number,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlack
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            description,
            style = MaterialTheme.typography.bodyLarge,
            color = OnSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun GeneratedTaskCard(
    task: Task,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    val alpha = if (isSelected) 1f else 0.4f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .clickable { onToggle() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) SurfaceContainerLowest else SurfaceBase
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            1.dp,
            if (isSelected) PrimaryBlack.copy(alpha = 0.15f) else GhostBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = PrimaryBlack,
                    uncheckedColor = SecondaryGray
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    task.category.name,
                    style = MaterialTheme.typography.labelLarge,
                    color = SecondaryGray.copy(alpha = alpha)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    task.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        textDecoration = if (isSelected) null else TextDecoration.LineThrough
                    ),
                    color = PrimaryBlack.copy(alpha = alpha)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "${task.estimatedMinutes}m",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = SecondaryGray.copy(alpha = alpha)
            )
        }
    }
}
