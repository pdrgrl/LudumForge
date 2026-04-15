package dam.a51319.ludumforge.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AltRoute
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dam.a51319.ludumforge.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoadmapGenerator() {
    var projectVision by remember { mutableStateOf("") }
    var teamSize by remember { mutableStateOf("") }
    var projectHorizon by remember { mutableStateOf("") }

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
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceContainerLowest,
                contentColor = SecondaryGray,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Architecture, contentDescription = "Planning") },
                    label = { Text("Project Planning", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    selected = false,
                    onClick = { /* TODO */ },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = SecondaryGray,
                        unselectedTextColor = SecondaryGray
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Groups, contentDescription = "Workspace") },
                    label = { Text("Team Workspace", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    selected = false,
                    onClick = { /* TODO */ },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = SecondaryGray,
                        unselectedTextColor = SecondaryGray
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.AltRoute, contentDescription = "Roadmap") },
                    label = { Text("Roadmap", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    selected = true,
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = SurfaceContainerHigh,
                        selectedIconColor = PrimaryBlack,
                        selectedTextColor = PrimaryBlack
                    ),
                    onClick = { /* TODO */ }
                )
            }
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
                Text(
                    text = "PROJECT VISION",
                    style = MaterialTheme.typography.labelLarge,
                    color = SecondaryGray
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Neumorphic Inset approximation
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceContainerHigh)
                        .padding(16.dp)
                ) {
                    BasicTextField(
                        value = projectVision,
                        onValueChange = { projectVision = it },
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
                    text = "PERSONNEL COUNT",
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
                    Icon(Icons.Default.Groups, contentDescription = null, tint = SecondaryGray, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    BasicTextField(
                        value = teamSize,
                        onValueChange = { teamSize = it },
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
                Text(
                    text = "PROJECT HORIZON",
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
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = SecondaryGray, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    BasicTextField(
                        value = projectHorizon,
                        onValueChange = { projectHorizon = it },
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

            // Contextual Info
            item {
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
                            Text(
                                text = "Architect’s Protocol",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlack
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))

                        ProtocolStep("01", "Be specific about genre and target platforms to ensure asset pipeline accuracy.")
                        Spacer(modifier = Modifier.height(20.dp))
                        ProtocolStep("02", "Team size influences the parallelization of art and engineering sprints.")
                        Spacer(modifier = Modifier.height(20.dp))
                        ProtocolStep("03", "The AI generator utilizes studio-standard milestones (Pre-Alpha, Vertical Slice, Beta).")
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun ProtocolStep(number: String, description: String) {
    Row(verticalAlignment = Alignment.Top) { // FIXED: Was using Flutter syntax previously!
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(SurfaceContainerHigh),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlack
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = OnSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RoadmapGeneratorPreview() {
    LudumForgeTheme {
        RoadmapGenerator()
    }
}