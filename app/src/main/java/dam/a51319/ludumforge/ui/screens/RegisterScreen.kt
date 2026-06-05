package dam.a51319.ludumforge.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dam.a51319.ludumforge.ui.theme.*
import dam.a51319.ludumforge.viewmodels.AuthUiState
import dam.a51319.ludumforge.viewmodels.AuthViewModel

import dam.a51319.ludumforge.models.UserRole

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: AuthViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) onRegisterSuccess()
    }

    RegisterContent(
        uiState = uiState,
        onRegister = { email, password, username, role -> viewModel.register(email, password, username, role) },
        onNavigateToLogin = onNavigateToLogin
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterContent(
    uiState: AuthUiState,
    onRegister: (String, String, String, UserRole) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.DEVELOPER) }

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFF0F0F0F)).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("NEW JAMMER", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray, letterSpacing = 2.sp)
        Text("Create Account", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)

        Spacer(Modifier.height(32.dp))

        AuthInput(value = username, onValueChange = { username = it }, label = "JAMMER USERNAME")
        Spacer(Modifier.height(16.dp))
        AuthInput(value = email, onValueChange = { email = it }, label = "EMAIL ADDRESS")
        Spacer(Modifier.height(16.dp))
        AuthInput(value = password, onValueChange = { password = it }, label = "PASSWORD", isPassword = true)

        Spacer(Modifier.height(24.dp))
        Text("SELECT YOUR ROLE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.align(Alignment.Start))
        Spacer(Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val roles = listOf(UserRole.DEVELOPER, UserRole.ARTIST, UserRole.AUDIO_ENGINEER)
            roles.forEach { role ->
                FilterChip(
                    selected = selectedRole == role,
                    onClick = { selectedRole = role },
                    label = { Text(role.name.replace("_", " "), fontSize = 10.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color.Transparent,
                        selectedContainerColor = MoltenOrange,
                        labelColor = Color.Gray,
                        selectedLabelColor = Color.White
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = Color.Gray.copy(alpha = 0.3f),
                        selectedBorderColor = Color.Transparent,
                        borderWidth = 1.dp
                    )
                )
            }
        }

        if (uiState is AuthUiState.Error) {
            Text(uiState.message, color = Color(0xFFBA1A1A), fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = { onRegister(email, password, username, selectedRole) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues()
        ) {
            Box(
                Modifier.fillMaxSize().background(Brush.linearGradient(listOf(MoltenOrange, MoltenOrangeEnd))),
                contentAlignment = Alignment.Center
            ) {
                if (uiState is AuthUiState.Loading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                else Text("REGISTER", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(24.dp))
        Text(
            "Already have an account? Sign in.",
            color = Color.Gray,
            fontSize = 13.sp,
            modifier = Modifier.clickable { onNavigateToLogin() }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    LudumForgeTheme {
        RegisterContent(
            uiState = AuthUiState.Idle,
            onRegister = { _, _ -> },
            onNavigateToLogin = {}
        )
    }
}
