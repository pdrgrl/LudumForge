package dam.a51319.ludumforge.ui.screens

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
import androidx.compose.material.icons.outlined.AccountCircle
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
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalDashboardScreen(
    viewModel: PersonalDashboardViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel(),
    onLogout: () -> Unit
) {
//    val currentUser by authViewModel.currentUser.collectAsState()
//    var showMenu by remember { mutableStateOf(false) }
    // State Collection
    val timeLeft by viewModel.timeLeftInSeconds.collectAsState()
    val priorityTasks by viewModel.myTasks.collectAsState()

    val hours = timeLeft / 3600
    val minutes = (timeLeft % 3600) / 60

    val activeProjects = listOf(
        Project("p1", "Neon Nights", "Cyberpunk", Date(), Date(), 4, ProjectStatus.ACTIVE),
        Project("p2", "Cozy Tavern", "Fantasy/Management", Date(), Date(), 2, ProjectStatus.ACTIVE)
    )

    Scaffold(
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
                        StatCard(modifier = Modifier.weight(1f), value = "${hours}h ${minutes}m", label = "Time\nRemaining")
                        StatCard(modifier = Modifier.weight(1f), value = "${priorityTasks.size}", label = "Tasks\nDue")
                        StatCard(modifier = Modifier.weight(1f), value = "84%", label = "Avg.\nVelocity")
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                    Text("ACTIVE PROJECTS", style = MaterialTheme.typography.labelLarge, color = SecondaryGray)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            item {
                LazyRow(contentPadding = PaddingValues(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(activeProjects) { project ->
                        ActiveProjectCard(project)
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
                    PriorityTaskCard(task = task, projectName = activeProjects.find { it.id == task.projectId }?.name ?: "Unknown Project")
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
fun ActiveProjectCard(project: Project) {
    val progress = if (project.id == "p1") 0.65f else 0.30f
    val role = if (project.id == "p1") "Lead Dev" else "Architect"

    Card(
        modifier = Modifier.width(280.dp).shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp), spotColor = PrimaryBlack.copy(alpha = 0.08f)),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, GhostBorder)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(SurfaceContainerHigh).padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Text(role, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryBlack, letterSpacing = 0.5.sp)
                }
                Icon(Icons.Default.MoreHoriz, contentDescription = "Options", tint = SecondaryGray)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(project.name, style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp), color = PrimaryBlack, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Theme: ${project.theme}", style = MaterialTheme.typography.bodyLarge, color = OnSurfaceVariant, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Sprint Progress", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SecondaryGray)
                Text("${(progress * 100).toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryBlack)
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)), color = PrimaryBlack, trackColor = SurfaceContainerHigh)
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { /* TODO */ },
                modifier = Modifier.fillMaxWidth().height(44.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Box(modifier = Modifier.fillMaxSize().background(Brush.linearGradient(listOf(PrimaryBlack, PrimaryContainerDark))), contentAlignment = Alignment.Center) {
                    Text("Manage Workspace", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun PriorityTaskCard(task: Task, projectName: String) {
    val isDone = task.status == TaskStatus.DONE
    val isInProgress = task.status == TaskStatus.IN_PROGRESS
    val statusText = when (task.status) {
        TaskStatus.IN_PROGRESS -> "In Progress"
        TaskStatus.TODO -> "To Do"
        TaskStatus.REVIEW -> "Review"
        TaskStatus.DONE -> "Done"
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).clickable { /* TODO */ },
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, GhostBorder)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(if (isDone) SurfaceContainerLowest else SurfaceContainerHigh).border(1.dp, if (isDone) GhostBorder else Color.Transparent, CircleShape),
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
            Column(horizontalAlignment = Alignment.End) {
                Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(if (isInProgress) PrimaryBlack else SurfaceContainerHigh).padding(horizontal = 6.dp, vertical = 2.dp)) {
                    Text(statusText, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (isInProgress) SurfaceContainerLowest else PrimaryBlack, letterSpacing = 0.5.sp)
                }
            }
        }
    }
}