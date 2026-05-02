package dam.a51319.ludumforge.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dam.a51319.ludumforge.models.UserPlan
import dam.a51319.ludumforge.ui.theme.*
import dam.a51319.ludumforge.viewmodels.AuthViewModel
import dam.a51319.ludumforge.viewmodels.PersonalDashboardViewModel

private data class PlanFeature(val label: String, val free: Boolean, val premium: Boolean)

private val FEATURES = listOf(
    PlanFeature("Jams per month",                    free = true,  premium = true),
    PlanFeature("AI Roadmap Generator",              free = true,  premium = true),
    PlanFeature("Team Workspace",                    free = true,  premium = true),
    PlanFeature("Offline Terminal",                  free = true,  premium = true),
    PlanFeature("Public Jam Explorer",               free = true,  premium = true),
    PlanFeature("Unlimited team jams",               free = false, premium = true),
    PlanFeature("Panic Button (AI Triage)",          free = false, premium = true),
    PlanFeature("Priority AI generation",            free = false, premium = true),
    PlanFeature("Premium API key (no key needed)",   free = false, premium = true)
)

@Composable
fun SubscriptionScreen(
    dashboardViewModel: PersonalDashboardViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val currentPlan by dashboardViewModel.currentPlan.collectAsState()
    val jamsThisMonth by dashboardViewModel.jamsThisMonth.collectAsState()
    var upgrading by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Refresh plan + jam count every time this screen is entered
    LaunchedEffect(Unit) {
        dashboardViewModel.refreshSubscriptionState()
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            containerColor = SurfaceContainerLowest,
            title = { Text("⭐ Welcome to Premium!", fontWeight = FontWeight.Bold, color = PrimaryBlack) },
            text = { Text("You now have unlimited jams, the Panic Button, and priority AI generation.", color = SecondaryGray) },
            confirmButton = {
                Button(
                    onClick = { showSuccessDialog = false; onNavigateBack() },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack)
                ) { Text("Let's go!", color = Color.White) }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBase)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(top = 32.dp, bottom = 100.dp)
    ) {
        Text("SUBSCRIPTION", style = MaterialTheme.typography.labelLarge, color = SecondaryGray)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Choose Your Plan", style = MaterialTheme.typography.headlineLarge, color = PrimaryBlack)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "LudumForge is free for casual jammers. Go Premium to remove limits and unlock power features.",
            style = MaterialTheme.typography.bodyLarge,
            color = SecondaryGray,
            lineHeight = 22.sp
        )
        Spacer(modifier = Modifier.height(32.dp))

        // ── Usage pill (FREE only) ──────────────────────────────────────────────
        if (currentPlan == UserPlan.FREE) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerHigh),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("This month's jams", style = MaterialTheme.typography.labelLarge, color = SecondaryGray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "$jamsThisMonth / ${PersonalDashboardViewModel.FREE_JAM_LIMIT}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (jamsThisMonth >= PersonalDashboardViewModel.FREE_JAM_LIMIT) ErrorRed else PrimaryBlack
                        )
                    }
                    LinearProgressIndicator(
                        progress = { (jamsThisMonth.toFloat() / PersonalDashboardViewModel.FREE_JAM_LIMIT.toFloat()).coerceIn(0f, 1f) },
                        modifier = Modifier.width(100.dp).height(6.dp).clip(RoundedCornerShape(4.dp)),
                        color = if (jamsThisMonth >= PersonalDashboardViewModel.FREE_JAM_LIMIT) ErrorRed else PrimaryBlack,
                        trackColor = SurfaceContainerLowest
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // ── Plan cards ─────────────────────────────────────────────────────────
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PlanCard(
                modifier = Modifier.weight(1f),
                title = "Free", price = "€0", period = "forever",
                isCurrentPlan = currentPlan == UserPlan.FREE, isPremium = false
            )
            PlanCard(
                modifier = Modifier.weight(1f),
                title = "Premium", price = "€3.99", period = "/ month",
                isCurrentPlan = currentPlan == UserPlan.PREMIUM, isPremium = true
            )
        }
        Spacer(modifier = Modifier.height(32.dp))

        // ── Feature table ──────────────────────────────────────────────────────
        Text("WHAT'S INCLUDED", style = MaterialTheme.typography.labelLarge, color = SecondaryGray)
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, GhostBorder)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("Feature", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelLarge, color = SecondaryGray)
                    Text("Free",    modifier = Modifier.width(48.dp), style = MaterialTheme.typography.labelLarge, color = SecondaryGray, textAlign = TextAlign.Center)
                    Text("Premium", modifier = Modifier.width(64.dp), style = MaterialTheme.typography.labelLarge, color = SecondaryGray, textAlign = TextAlign.Center)
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = GhostBorder)
                FEATURES.forEachIndexed { i, feature ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (i == 0) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(feature.label, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium), color = PrimaryBlack)
                                Text("Free: 2 | Premium: ∞", fontSize = 11.sp, color = SecondaryGray)
                            }
                        } else {
                            Text(feature.label, modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium), color = PrimaryBlack)
                        }
                        FeatureCell(enabled = feature.free, modifier = Modifier.width(48.dp))
                        FeatureCell(enabled = feature.premium, modifier = Modifier.width(64.dp))
                    }
                    if (i < FEATURES.lastIndex) HorizontalDivider(color = GhostBorder.copy(alpha = 0.1f))
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))

        // ── CTA ────────────────────────────────────────────────────────────────
        when (currentPlan) {
            UserPlan.PREMIUM -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PrimaryBlack, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("You're on Premium — enjoy unlimited jams!", color = PrimaryBlack, fontWeight = FontWeight.Bold)
                }
            }
            UserPlan.FREE -> {
                Button(
                    onClick = { upgrading = true },
                    enabled = !upgrading,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(listOf(PrimaryBlack, PrimaryContainerDark)),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (upgrading) {
                            LaunchedEffect(Unit) {
                                kotlinx.coroutines.delay(1200)
                                // Upgrade via VM: writes to Firestore + flips _currentPlan immediately
                                val result = dashboardViewModel.upgradeToPremium()
                                if (result.isSuccess) {
                                    // Re-fetch the full User object so TopAppBar badge also updates
                                    authViewModel.fetchUserProfile()
                                }
                                upgrading = false
                                showSuccessDialog = result.isSuccess
                            }
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                Text("Upgrade to Premium — €3.99/mo", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Cancel anytime. Upgrade takes effect immediately.",
                    fontSize = 11.sp, color = SecondaryGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun PlanCard(
    modifier: Modifier = Modifier,
    title: String, price: String, period: String,
    isCurrentPlan: Boolean, isPremium: Boolean
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = if (isPremium && isCurrentPlan) PrimaryBlack else SurfaceContainerLowest),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(if (isCurrentPlan) 2.dp else 1.dp, if (isCurrentPlan) PrimaryBlack else GhostBorder)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            if (isPremium) {
                Surface(shape = RoundedCornerShape(4.dp), color = if (isCurrentPlan) Color.White.copy(alpha = 0.15f) else PrimaryBlack) {
                    Text("PREMIUM", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                }
                Spacer(modifier = Modifier.height(12.dp))
            } else {
                Surface(shape = RoundedCornerShape(4.dp), color = SurfaceContainerHigh) {
                    Text("FREE", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = SecondaryGray)
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold,
                color = if (isPremium && isCurrentPlan) Color.White else PrimaryBlack)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(price, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold,
                    color = if (isPremium && isCurrentPlan) Color.White else PrimaryBlack)
                Spacer(modifier = Modifier.width(4.dp))
                Text(period, fontSize = 12.sp,
                    color = if (isPremium && isCurrentPlan) Color.White.copy(alpha = 0.7f) else SecondaryGray,
                    modifier = Modifier.padding(bottom = 4.dp))
            }
            if (isCurrentPlan) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (isPremium) Color.White.copy(alpha = 0.15f) else SurfaceContainerHigh
                ) {
                    Text("Current Plan",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp, fontWeight = FontWeight.Bold,
                        color = if (isPremium) Color.White else PrimaryBlack)
                }
            }
        }
    }
}

@Composable
private fun FeatureCell(enabled: Boolean, modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (enabled) Icon(Icons.Default.CheckCircle, contentDescription = "Included", tint = PrimaryBlack, modifier = Modifier.size(18.dp))
        else Icon(Icons.Default.Lock, contentDescription = "Not included", tint = GhostBorder, modifier = Modifier.size(16.dp))
    }
}
