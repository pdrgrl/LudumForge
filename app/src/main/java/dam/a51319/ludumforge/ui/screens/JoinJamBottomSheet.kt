package dam.a51319.ludumforge.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dam.a51319.ludumforge.models.Project
import dam.a51319.ludumforge.ui.theme.*
import dam.a51319.ludumforge.viewmodels.JoinJamState
import dam.a51319.ludumforge.viewmodels.JoinJamViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinJamBottomSheet(
    jam: Project,
    viewModel: JoinJamViewModel = viewModel(),
    onDismiss: () -> Unit,
    onGenerateRoadmap: (idea: String, teamSize: Int) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var userIdea by remember { mutableStateOf("") }
    var teamSize by remember { mutableFloatStateOf(1f) }

    LaunchedEffect(jam) { viewModel.extractJamInfo(jam) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceBase
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp).padding(bottom = 24.dp)) {
            Text("Joining: ${jam.name}", style = MaterialTheme.typography.headlineMedium, color = PrimaryBlack)
            Spacer(modifier = Modifier.height(24.dp))

            when (val currentState = state) {
                is JoinJamState.LoadingSummary, is JoinJamState.LoadingFeedback -> {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = PrimaryBlack)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(if (currentState is JoinJamState.LoadingSummary) "Analyzing Jam Rules..." else "Brainstorming Idea...", color = SecondaryGray)
                        }
                    }
                }

                is JoinJamState.InputIdea -> {
                    Card(colors = CardDefaults.cardColors(containerColor = SurfaceContainerHigh)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("THEME", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SecondaryGray)
                            Text(currentState.theme, color = PrimaryBlack, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("RULES", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SecondaryGray)
                            Text(currentState.rules, color = PrimaryBlack, fontSize = 14.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().background(Color(0xFFFFE0B2), RoundedCornerShape(8.dp)).padding(12.dp)) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFE65100))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Remember: You still must click 'Join' on the actual itch.io page to officially participate!", fontSize = 13.sp, color = Color(0xFFE65100), fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = userIdea,
                        onValueChange = { userIdea = it },
                        label = { Text("What is your game idea?") },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryBlack)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Team Size: ${teamSize.toInt()}", fontWeight = FontWeight.Bold)
                    Slider(
                        value = teamSize,
                        onValueChange = { teamSize = it },
                        valueRange = 1f..10f,
                        steps = 9,
                        colors = SliderDefaults.colors(thumbColor = PrimaryBlack, activeTrackColor = PrimaryBlack)
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { viewModel.brainstormIdea(currentState.theme, currentState.rules, userIdea, teamSize.toInt()) },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack)
                    ) { Text("Brainstorm with AI") }
                }

                is JoinJamState.BrainstormResult -> {
                    Text("AI Feedback", fontWeight = FontWeight.Bold, color = PrimaryBlack)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(currentState.feedback, color = OnSurfaceVariant, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = { onGenerateRoadmap(userIdea, teamSize.toInt()) },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack)
                    ) { Text("Yes, Generate Roadmap") }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { viewModel.refineIdea(currentState.theme, currentState.rules) },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                    ) { Text("Refine Idea", color = PrimaryBlack) }
                }

                is JoinJamState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = currentState.message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.extractJamInfo(jam) },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack)
                        ) {
                            Text("Try Again")
                        }
                    }
                }
            }
        }
    }
}