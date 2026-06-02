package dam.a51319.ludumforge.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.BlurOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dam.a51319.ludumforge.viewmodels.AuthUiState
import dam.a51319.ludumforge.viewmodels.AuthViewModel
import androidx.compose.ui.tooling.preview.Preview
import dam.a51319.ludumforge.ui.theme.LudumForgeTheme
import dam.a51319.ludumforge.R

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val webClientId = stringResource(R.string.default_web_client_id)

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) onLoginSuccess()
    }

    LoginScreenContent(
        uiState = uiState,
        onLogin = { email, password -> viewModel.login(email, password) },
        onLoginWithGoogle = { viewModel.loginWithGoogle(context, webClientId) },
        onNavigateToRegister = onNavigateToRegister
    )
}

@Composable
fun LoginScreenContent(
    uiState: AuthUiState,
    onLogin: (String, String) -> Unit,
    onLoginWithGoogle: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F0F)) // Midnight Vellum Base
            .safeDrawingPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.BlurOn, null, tint = Color.White, modifier = Modifier.size(64.dp))
        Spacer(Modifier.height(16.dp))
        Text(
            "LUDUMFORGE",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = Color.White,
            letterSpacing = 4.sp
        )
        Text("Architect your vision.", fontSize = 14.sp, color = Color.Gray)

        Spacer(Modifier.height(48.dp))

        // Inset Input
        AuthInput(value = email, onValueChange = { email = it }, label = "EMAIL ADDRESS")
        Spacer(Modifier.height(16.dp))
        AuthInput(
            value = password,
            onValueChange = { password = it },
            label = "PASSWORD",
            isPassword = true
        )

        if (uiState is AuthUiState.Error) {
            Text(
                uiState.message,
                color = Color(0xFFBA1A1A),
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(Modifier.height(32.dp))

        // Premium Action Button
        Button(
            onClick = { onLogin(email, password) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues()
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Brush.linearGradient(listOf(Color.White, Color(0xFFCCCCCC)))),
                contentAlignment = Alignment.Center
            ) {
                if (uiState is AuthUiState.Loading) CircularProgressIndicator(
                    color = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
                else Text("SIGN IN", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }



        Spacer(Modifier.height(16.dp))

// Divider
        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = Color.White.copy(alpha = 0.1f)
            )
            Text("  OR  ", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = Color.White.copy(alpha = 0.1f)
            )
        }

        Spacer(Modifier.height(16.dp))

        OutlinedButton(
            onClick = onLoginWithGoogle,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
        ) {
            // Using default icon if you haven't added the Google drawable yet
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Google",
                modifier = Modifier.size(20.dp),
                tint = Color.White
            )
            Spacer(Modifier.width(12.dp))
            Text("CONTINUE WITH GOOGLE", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }

        Spacer(Modifier.height(24.dp))
        Text(
            "Don't have an account? Create one.",
            color = Color.Gray,
            fontSize = 13.sp,
            modifier = Modifier.clickable { onNavigateToRegister() }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LudumForgeTheme {
        LoginScreenContent(
            uiState = AuthUiState.Idle,
            onLogin = { _, _ -> },
            onLoginWithGoogle = { },
            onNavigateToRegister = { }
        )
    }
}

@Composable
fun AuthInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false
) {
    Column(Modifier.fillMaxWidth()) {
        Text(
            label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            letterSpacing = 1.sp
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Email),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF1A1A1A),
                unfocusedContainerColor = Color(0xFF1A1A1A),
                focusedBorderColor = Color.White.copy(0.2f),
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )
    }
}