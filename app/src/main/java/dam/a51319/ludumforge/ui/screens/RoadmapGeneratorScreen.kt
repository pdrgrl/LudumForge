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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoadmapGeneratorScreen(viewModel: RoadmapGeneratorViewModel = viewModel()) {

    // ViewModel State Collection
    val projectVision by viewModel.gameTitle.collectAsState()
    val teamSize by viewModel.teamSize.collectAsState()
    val projectHorizon by viewModel.duration.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

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
                    text = "Architect your development cycle with precision. Input your vision to generate a surgically precise project blueprint.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = OnSurfaceVariant
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Project Vision Input
            item {
                Text("PROJECT VISION", style = MaterialTheme.typography.labelLarge, color = SecondaryGray)
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
                Text("PERSONNEL COUNT", style = MaterialTheme.typography.labelLarge, color = SecondaryGray)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceContainerHigh)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Groups, contentDescription = null, tint = SecondaryGray, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    BasicTextField(
                        value = teamSize,
                        onValueChange = { viewModel.teamSize.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(color = PrimaryBlack),
                        decorationBox = { innerTextField ->
                            if (teamSize.isEmpty()) Text("Team Size", color = SecondaryGray.copy(alpha = 0.7f), style = MaterialTheme.typography.bodyLarge)
                            innerTextField()
                        }
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Project Horizon
            item {
                Text("PROJECT HORIZON", style = MaterialTheme.typography.labelLarge, color = SecondaryGray)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceContainerHigh)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = SecondaryGray, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    BasicTextField(
                        value = projectHorizon,
                        onValueChange = { viewModel.duration.value = it },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(color = PrimaryBlack),
                        decorationBox = { innerTextField ->
                            if (projectHorizon.isEmpty()) Text("e.g. 6 Months", color = SecondaryGray.copy(alpha = 0.7f), style = MaterialTheme.typography.bodyLarge)
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
                        .shadow(elevation = 16.dp, shape = RoundedCornerShape(12.dp), spotColor = PrimaryBlack.copy(alpha = 0.2f))
                        .clip(RoundedCornerShape(12.dp))
                        .background(Brush.linearGradient(listOf(PrimaryBlack, PrimaryContainerDark)))
                        .clickable { viewModel.onGenerateClicked() }
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
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }

            // UI State Display
            item {
                when (uiState) {
                    is RoadmapUiState.Loading -> {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = PrimaryBlack)
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
                        Text("GENERATED ROADMAP", style = MaterialTheme.typography.labelLarge, color = SecondaryGray)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    else -> {
                        // Idle state - show architect protocol
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, GhostBorder)
                        ) {
                            Column(modifier = Modifier.padding(24.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Info, contentDescription = null, tint = PrimaryBlack, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Architect’s Protocol", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryBlack)
                                }
                                Spacer(modifier = Modifier.height(24.dp))
                                ProtocolStep("01", "Be specific about genre and target platforms to ensure asset pipeline accuracy.")
                                Spacer(modifier = Modifier.height(20.dp))
                                ProtocolStep("02", "Team size influences the parallelization of art and engineering sprints.")
                            }
                        }
                    }
                }
            }

            // Show Tasks if Success
            if (uiState is RoadmapUiState.Success) {
                val generatedTasks = (uiState as RoadmapUiState.Success).tasks
                items(generatedTasks) { task ->
                    GeneratedTaskCard(task)
                }
            }
        }
    }
}

@Composable
fun ProtocolStep(number: String, description: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier.size(32.dp).clip(CircleShape).background(SurfaceContainerHigh),
            contentAlignment = Alignment.Center
        ) {
            Text(number, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryBlack)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(description, style = MaterialTheme.typography.bodyLarge, color = OnSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
fun GeneratedTaskCard(task: Task) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, GhostBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(task.category.name, style = MaterialTheme.typography.labelLarge, color = SecondaryGray)
            Spacer(modifier = Modifier.height(8.dp))
            Text(task.title, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = PrimaryBlack)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Est: ${task.estimatedMinutes} mins", fontSize = 12.sp, color = SecondaryGray)
        }
    }
}