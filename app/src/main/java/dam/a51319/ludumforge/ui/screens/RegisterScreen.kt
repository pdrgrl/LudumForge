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
import dam.a51319.ludumforge.ui.theme.LudumForgeTheme
import dam.a51319.ludumforge.viewmodels.AuthUiState
import dam.a51319.ludumforge.viewmodels.AuthViewModel

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
        onRegister = { email, password -> viewModel.register(email, password) },
        onNavigateToLogin = onNavigateToLogin
    )
}

@Composable
fun RegisterContent(
    uiState: AuthUiState,
    onRegister: (String, String) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFF0F0F0F)).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("NEW ARCHITECT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray, letterSpacing = 2.sp)
        Text("Create Account", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)

        Spacer(Modifier.height(48.dp))

        AuthInput(value = username, onValueChange = { username = it }, label = "STUDIO USERNAME")
        Spacer(Modifier.height(16.dp))
        AuthInput(value = email, onValueChange = { email = it }, label = "EMAIL ADDRESS")
        Spacer(Modifier.height(16.dp))
        AuthInput(value = password, onValueChange = { password = it }, label = "PASSWORD", isPassword = true)

        if (uiState is AuthUiState.Error) {
            Text(uiState.message, color = Color(0xFFBA1A1A), fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(Modifier.height(40.dp))

        Button(
            onClick = { onRegister(email, password) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues()
        ) {
            Box(
                Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Color.White, Color(0xFFCCCCCC)))),
                contentAlignment = Alignment.Center
            ) {
                if (uiState is AuthUiState.Loading) CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                else Text("REGISTER", color = Color.Black, fontWeight = FontWeight.Bold)
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
