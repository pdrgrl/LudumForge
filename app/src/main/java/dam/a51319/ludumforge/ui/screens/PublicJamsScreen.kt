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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import dam.a51319.ludumforge.models.*
import dam.a51319.ludumforge.ui.theme.*
import dam.a51319.ludumforge.viewmodels.PublicJamsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicJamsScreen(viewModel: PublicJamsViewModel = viewModel()) {

    val jams by viewModel.publicJams.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var selectedJamToJoin by remember { mutableStateOf<Project?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Active", "Upcoming", "Archived")

    val filteredJams = remember(jams, searchQuery, selectedFilter) {
        jams.filter { jam ->
            val matchesSearch = searchQuery.isBlank() ||
                    jam.name.contains(searchQuery, ignoreCase = true) ||
                    jam.theme.contains(searchQuery, ignoreCase = true)
            val matchesFilter = when (selectedFilter) {
                "Active"   -> jam.status == ProjectStatus.ACTIVE
                "Upcoming" -> jam.status == ProjectStatus.PLANNING
                "Archived" -> jam.status == ProjectStatus.COMPLETED
                else       -> true
            }
            matchesSearch && matchesFilter
        }
    }

    Scaffold(containerColor = SurfaceBase) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(top = 24.dp, bottom = 100.dp)
        ) {
            item {
                Text("GLOBAL EVENTS", style = MaterialTheme.typography.labelLarge, color = SecondaryGray)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Public Jams", style = MaterialTheme.typography.headlineLarge, color = PrimaryBlack)
                Spacer(modifier = Modifier.height(24.dp))

                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            "Search jams, themes, or hosts...",
                            color = SecondaryGray,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = SecondaryGray, modifier = Modifier.size(20.dp))
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
                Spacer(modifier = Modifier.height(24.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(filters) { filter ->
                        val isSelected = filter == selectedFilter
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) PrimaryBlack else SurfaceContainerLowest)
                                .border(1.dp, if (isSelected) Color.Transparent else GhostBorder, RoundedCornerShape(20.dp))
                                .clickable { selectedFilter = filter }
                                .padding(horizontal = 20.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                filter,
                                color = if (isSelected) SurfaceContainerLowest else PrimaryBlack,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = PrimaryBlack)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Fetching live jams…",
                                color = SecondaryGray,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            if (!isLoading && filteredJams.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 64.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🎮", fontSize = 40.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("No jams found", fontWeight = FontWeight.Bold, color = PrimaryBlack)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Try a different filter or search term", color = SecondaryGray, fontSize = 13.sp)
                        }
                    }
                }
            }

            items(filteredJams) { jam ->
                JamCard(jam = jam, onJoinClicked = { selectedJamToJoin = it })
            }
        }

        selectedJamToJoin?.let { jam ->
            JoinJamBottomSheet(
                jam = jam,
                onDismiss = { selectedJamToJoin = null },
                onGenerateRoadmap = { idea, size ->
                    selectedJamToJoin = null
                    // TODO: Call personalDashboardViewModel.createNewJam(jam.name, idea, durationDays)
                    // TODO: Navigate user to the Team Workspace / Roadmap Screen
                }
            )
        }
    }
}

@Composable
fun JamCard(jam: Project, onJoinClicked: (Project) -> Unit) {
    val statusText = when (jam.status) {
        ProjectStatus.ACTIVE    -> "ACTIVE"
        ProjectStatus.PLANNING  -> "UPCOMING"
        ProjectStatus.COMPLETED -> "ARCHIVED"
        else                    -> "UNKNOWN"
    }
    val statusColor = when (jam.status) {
        ProjectStatus.ACTIVE   -> Color(0xFF00C853)
        ProjectStatus.PLANNING -> Color(0xFFFF9800)
        else                   -> SecondaryGray
    }
    val timeLeftText = when (jam.status) {
        ProjectStatus.ACTIVE   -> "Live now"
        ProjectStatus.PLANNING -> "Coming soon"
        else                   -> "Ended"
    }

    val context = androidx.compose.ui.platform.LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = PrimaryBlack.copy(alpha = 0.08f)
            )
            .clickable {
                jam.jamUrl?.let { url ->
                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
                    context.startActivity(intent)
                }
            },
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, GhostBorder)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // ── Cover image / placeholder header ────────────────────────────
            if (jam.coverImageUrl != null) {
                SubcomposeAsyncImage(
                    model = jam.coverImageUrl,
                    contentDescription = "${jam.name} cover",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            SurfaceContainerHigh,
                                            SurfaceContainerLowest
                                        )
                                    )
                                )
                        )
                    },
                    error = {
                        JamCoverPlaceholder(jam.name)
                    }
                )
            } else {
                JamCoverPlaceholder(jam.name)
            }

            // ── Card body ────────────────────────────────────────────────────
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(statusColor))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(statusText, style = MaterialTheme.typography.labelLarge, color = PrimaryBlack)
                    }
                    Text(timeLeftText, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SecondaryGray)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    jam.name,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp),
                    color = PrimaryBlack,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    jam.theme,
                    style = MaterialTheme.typography.bodyLarge,
                    color = OnSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = SecondaryGray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("${jam.teamSize} participants", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = SecondaryGray)
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            jam.jamUrl?.let { url ->
                                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
                                context.startActivity(intent)
                            }
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, GhostBorder),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlack)
                    ) {
                        Text("View Details", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    if (jam.status != ProjectStatus.COMPLETED) {
                        Button(onClick = { onJoinClicked(jam) },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.linearGradient(listOf(PrimaryBlack, PrimaryContainerDark))
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Join Jam", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// Shown when coverImageUrl is null or image fails to load
@Composable
private fun JamCoverPlaceholder(jamName: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF1A1A2E), Color(0xFF16213E), Color(0xFF0F3460))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = jamName.take(1).uppercase(),
                fontSize = 48.sp,
                fontWeight = FontWeight.Black,
                color = Color.White.copy(alpha = 0.15f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "🎮",
                fontSize = 28.sp
            )
        }
    }
}