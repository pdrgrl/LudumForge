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
import androidx.compose.ui.tooling.preview.Preview
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
    onGenerateRoadmap: (seedText: String, teamSize: Int) -> Unit
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
        JoinJamBottomSheetUI(
            jam = jam,
            state = state,
            userIdea = userIdea,
            onUserIdeaChange = { userIdea = it },
            teamSize = teamSize,
            onTeamSizeChange = { teamSize = it },
            onBrainstormIdea = viewModel::brainstormIdea,
            onRefineIdea = viewModel::refineIdea,
            onGenerateRoadmap = onGenerateRoadmap,
            onExtractJamInfo = { viewModel.extractJamInfo(jam) }
        )
    }
}

@Composable
private fun JoinJamBottomSheetUI(
    jam: Project,
    state: JoinJamState,
    userIdea: String,
    onUserIdeaChange: (String) -> Unit,
    teamSize: Float,
    onTeamSizeChange: (Float) -> Unit,
    onBrainstormIdea: (theme: String, rules: String, userIdea: String, teamSize: Int) -> Unit,
    onRefineIdea: (theme: String, rules: String) -> Unit,
    onGenerateRoadmap: (seedText: String, teamSize: Int) -> Unit,
    onExtractJamInfo: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .padding(bottom = 24.dp)
    ) {
        Text(
            "Joining: ${jam.name}",
            style = MaterialTheme.typography.headlineMedium,
            color = PrimaryBlack
        )
        Spacer(modifier = Modifier.height(24.dp))

        when (val currentState = state) {
            is JoinJamState.LoadingSummary, is JoinJamState.LoadingFeedback -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = PrimaryBlack)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            if (currentState is JoinJamState.LoadingSummary) "Analyzing Jam Rules..." else "Brainstorming Idea...",
                            color = SecondaryGray
                        )
                    }
                }
            }

            is JoinJamState.InputIdea -> {
                Card(colors = CardDefaults.cardColors(containerColor = SurfaceContainerHigh)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "THEME",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = SecondaryGray
                        )
                        Text(
                            currentState.theme,
                            color = PrimaryBlack,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "RULES",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = SecondaryGray
                        )
                        Text(currentState.rules, color = PrimaryBlack, fontSize = 14.sp)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFE0B2), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color(0xFFE65100)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Remember: You still must click 'Join' on the actual itch.io page to officially participate!",
                        fontSize = 13.sp,
                        color = Color(0xFFE65100),
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = userIdea,
                    onValueChange = onUserIdeaChange,
                    label = { Text("What is your game idea?") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryBlack)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text("Team Size: ${teamSize.toInt()}", fontWeight = FontWeight.Bold)
                Slider(
                    value = teamSize,
                    onValueChange = onTeamSizeChange,
                    valueRange = 1f..10f,
                    steps = 9,
                    colors = SliderDefaults.colors(
                        thumbColor = PrimaryBlack,
                        activeTrackColor = PrimaryBlack
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        onBrainstormIdea(
                            currentState.theme,
                            currentState.rules,
                            userIdea,
                            teamSize.toInt()
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack)
                ) { Text("Brainstorm with AI") }
            }

            is JoinJamState.BrainstormResult -> {
                Text("AI Feedback", fontWeight = FontWeight.Bold, color = PrimaryBlack)
                Spacer(modifier = Modifier.height(8.dp))
                Text(currentState.feedback, color = OnSurfaceVariant, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        val seededPrompt = """
            Jam: ${jam.name}

            My idea:
            $userIdea

            AI notes:
            ${currentState.feedback}
        """.trimIndent()

                        onGenerateRoadmap(seededPrompt, teamSize.toInt())
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack)
                ) { Text("Yes, Generate Roadmap") }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = { onRefineIdea(currentState.theme, currentState.rules) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
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
                        onClick = onExtractJamInfo,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack)
                    ) {
                        Text("Try Again")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewJoinJamInput() {
    LudumForgeTheme {
        JoinJamBottomSheetUI(
            jam = Project(name = "Ludum Dare 55"),
            state = JoinJamState.InputIdea(
                theme = "Summoning",
                rules = "- Must use provided assets\n- 48 hours\n- Solo or Team"
            ),
            userIdea = "A game about summoning snacks",
            onUserIdeaChange = {},
            teamSize = 3f,
            onTeamSizeChange = {},
            onBrainstormIdea = { _, _, _, _ -> },
            onRefineIdea = { _, _ -> },
            onGenerateRoadmap = { _, _ -> },
            onExtractJamInfo = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewJoinJamBrainstorm() {
    LudumForgeTheme {
        JoinJamBottomSheetUI(
            jam = Project(name = "Ludum Dare 55"),
            state = JoinJamState.BrainstormResult(
                theme = "Summoning",
                rules = "- Must use provided assets\n- 48 hours\n- Solo or Team",
                feedback = "Great idea! It fits the theme well and is achievable for a team of 3. Consider adding a timer for snack delivery."
            ),
            userIdea = "A game about summoning snacks",
            onUserIdeaChange = {},
            teamSize = 3f,
            onTeamSizeChange = {},
            onBrainstormIdea = { _, _, _, _ -> },
            onRefineIdea = { _, _ -> },
            onGenerateRoadmap = { _, _ -> },
            onExtractJamInfo = {}
        )
    }
}