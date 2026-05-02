# LudumForge — Implementation Plan
> Ordered session-by-session plan. Each session ~2-3h.
> Last updated: May 2, 2026

---

## Session A — Task Edit & Delete + Category/Minutes in Form (M3 + M5)
**Goal:** Make tasks fully editable, deletable, and have all fields surfaced in the create form.

### A1 — `TaskRepository` — add `updateTask` and `deleteTask`
```kotlin
suspend fun updateTask(taskId: String, updates: Map<String, Any>) {
    db.collection("tasks").document(taskId).update(updates).await()
}

suspend fun deleteTask(taskId: String) {
    db.collection("tasks").document(taskId).delete().await()
}
```

### A2 — `TeamWorkspaceViewModel` — expose update/delete
```kotlin
fun updateTask(taskId: String, updates: Map<String, Any>) {
    viewModelScope.launch { try { taskRepository.updateTask(taskId, updates) } catch (e: Exception) { e.printStackTrace() } }
}
fun deleteTask(taskId: String) {
    viewModelScope.launch { try { taskRepository.deleteTask(taskId) } catch (e: Exception) { e.printStackTrace() } }
}
```

### A3 — `TeamWorkspaceScreen` — Edit bottom sheet + delete confirm
- Reuse `AddTaskBottomSheet` pattern; pre-fill fields when editing
- Long-press `TaskCard` → show edit sheet
- Trailing trash icon in edit sheet → `AlertDialog` confirm → `viewModel.deleteTask()`
- Category `FilterChip` row (CODE / ART / AUDIO / DESIGN / QA) in both Add and Edit sheets
- `estimatedMinutes` `TextField` (number input, optional) in both sheets

### A4 — `TaskCard` — show category badge
- Small rounded pill at bottom-left of card
- Color map: CODE=Blue, ART=Purple, AUDIO=Orange, DESIGN=Teal, QA=Red

---

## Session B — Subscription System (M1)
**Goal:** Add the Free/Premium model end-to-end with a polished paywall UI but a one-tap upgrade (no real payment).

### B1 — Data model — `UserPlan` enum + Firestore fields
```kotlin
// User.kt — add field
enum class UserPlan { FREE, PREMIUM }

data class User(
    ...
    val plan: UserPlan = UserPlan.FREE,
    val monthlyJamsCreated: Int = 0,
    val lastResetMonth: String = "" // "YYYY-MM"
)
```
Also update Firestore document writes in `AuthRepository.saveUserToFirestore` to include:
```kotlin
"plan" to "FREE",
"monthlyJamsCreated" to 0,
"lastResetMonth" to ""
```

### B2 — `AuthRepository` — upgrade + jam count helpers
```kotlin
// Upgrade user to premium
suspend fun upgradeToPremium(userId: String) {
    db.collection("users").document(userId)
        .update("plan", "PREMIUM").await()
}

// Read current plan (called on app start / profile load)
suspend fun getUserPlan(userId: String): UserPlan {
    val doc = db.collection("users").document(userId).get().await()
    return UserPlan.valueOf(doc.getString("plan") ?: "FREE")
}

// Increment jam creation counter for the month; returns false if FREE limit reached
suspend fun canCreateJam(userId: String): Boolean {
    val doc = db.collection("users").document(userId).get().await()
    val plan = UserPlan.valueOf(doc.getString("plan") ?: "FREE")
    if (plan == UserPlan.PREMIUM) return true

    val currentMonth = java.time.YearMonth.now().toString() // "YYYY-MM"
    val storedMonth = doc.getString("lastResetMonth") ?: ""
    val count = if (storedMonth == currentMonth) (doc.getLong("monthlyJamsCreated") ?: 0L).toInt() else 0

    return count < 2
}

suspend fun incrementJamCount(userId: String) {
    val currentMonth = java.time.YearMonth.now().toString()
    db.collection("users").document(userId).update(
        mapOf(
            "monthlyJamsCreated" to com.google.firebase.firestore.FieldValue.increment(1),
            "lastResetMonth" to currentMonth
        )
    ).await()
}
```

### B3 — `SubscriptionViewModel.kt` — new file
```kotlin
class SubscriptionViewModel : ViewModel() {
    private val authRepo = AuthRepository()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    private val _isPremium = MutableStateFlow(false)
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init { loadPlan() }

    private fun loadPlan() {
        viewModelScope.launch {
            val uid = userId ?: return@launch
            val plan = authRepo.getUserPlan(uid)
            _isPremium.value = plan == UserPlan.PREMIUM
        }
    }

    // Single-tap upgrade (no real payment)
    fun upgradeToPremium() {
        viewModelScope.launch {
            val uid = userId ?: return@launch
            _isLoading.value = true
            try {
                authRepo.upgradeToPremium(uid)
                _isPremium.value = true
            } catch (e: Exception) { e.printStackTrace() }
            _isLoading.value = false
        }
    }
}
```

### B4 — `SubscriptionScreen.kt` — new file
Layout:
- **Hero section:** "LudumForge Premium" title + subtitle
- **Plan comparison table** using a `LazyColumn` of rows or a `Table`-style composable:

| Feature | Free | Premium |
|---|---|---|
| Team Jams / month | 2 | ♾️ Unlimited |
| AI Roadmap Generation | Own API key | ✅ Included |
| Panic Button | 🔒 Locked | ✅ Enabled |
| Real-time Team Board | ✅ | ✅ |
| Offline Terminal | ✅ | ✅ |
| Premium badge | ❌ | ✅ |

- **Price card:** `€3.99 / month` (large text, styled pill card in PrimaryBlack)
- **CTA button:**
  - FREE state: `"Upgrade to Premium — €3.99/mo"` button → calls `viewModel.upgradeToPremium()` → shows spinner
  - PREMIUM state: `"⭐ You're on Premium"` (disabled, success color) + `"Cancel subscription (mock)"` text button → writes `plan = FREE` back
- Navigate to this screen from the Settings dialog in `LudumForgeTopAppBar`

### B5 — Enforce jam limit in `PersonalDashboardViewModel.createNewJam`
```kotlin
fun createNewJam(name: String, theme: String, durationDays: Int = 7) {
    if (currentUserId == null || name.isBlank()) return
    viewModelScope.launch {
        try {
            val canCreate = projectRepository.authRepository.canCreateJam(currentUserId)
            if (!canCreate) {
                _createJamError.value = "FREE_LIMIT_REACHED" // triggers upgrade dialog in UI
                return@launch
            }
            projectRepository.createJam(name, theme, durationDays, 1, currentUserId)
            projectRepository.authRepository.incrementJamCount(currentUserId)
        } catch (e: Exception) { e.printStackTrace() }
    }
}
```
Add `_createJamError: MutableStateFlow<String?>` to `PersonalDashboardViewModel` and show an upgrade `AlertDialog` in `PersonalDashboardScreen` when it fires.

### B6 — Gate Panic Button in `TeamWorkspaceScreen`
- Collect `isPremium` from `SubscriptionViewModel` (shared or passed as param)
- If `!isPremium`: show a locked Panic FAB (dimmed, lock icon)
- On tap when locked: `AlertDialog` with "⚡ Unlock with Premium" CTA → navigate to `SubscriptionScreen`

### B7 — Gate AI API key in `RoadmapGeneratorScreen`
- Already partially wired: `isPremium` passed to `RoadmapGeneratorViewModel.onGenerateClicked`
- Premium users don't need to paste a key; free users still need to configure one in Settings
- Add a visible "Settings → Add API Key" nudge banner on the screen for free users

---

## Session C — Panic Button (M2)
**Goal:** Implement the emergency MVP generator. Requires Session B (subscription) to be done first since it's gated behind Premium.

### C1 — `TeamWorkspaceViewModel` — add `triggerPanicMode(context)`
```kotlin
fun triggerPanicMode(context: Context) {
    viewModelScope.launch {
        val tasks = _tasks.value.filter { it.status != TaskStatus.DONE }
        val timeLeft = SessionManager.activeJamTimeLeftSeconds.value // or pass from UI
        _panicState.value = PanicUiState.Loading

        val taskJson = tasks.map { mapOf("id" to it.id, "title" to it.title, "category" to it.category.name) }
        // Call Gemini via RoadmapGeneratorViewModel or inline
        // Prompt: given these tasks + X minutes, return MVP-only ids as JSON array
    }
}
```

### C2 — `TeamWorkspaceScreen` — Panic FAB
- Red FAB with `Icons.Default.Warning` or a custom skull icon
- Only enabled if `isPremium`; otherwise shows locked state (from Session B)
- Confirm dialog: `"🚨 Panic Mode will use AI to drop non-essential tasks. This cannot be undone. Continue?"`
- After confirm → loading overlay on the workspace
- Review step: list tasks to keep (green) vs drop (red), Confirm / Cancel actions
- On Confirm: delete dropped tasks from Firestore, log SYSTEM event

### C3 — Sealed `PanicUiState`
```kotlin
sealed class PanicUiState {
    object Idle : PanicUiState()
    object Loading : PanicUiState()
    data class Review(val keep: List<Task>, val drop: List<Task>) : PanicUiState()
    object Applying : PanicUiState()
}
```

---

## Session D — Public Jams Feed (M4)
**Goal:** Replace dummy data with real itch.io jam data.

### D1 — `ItchRepository` — parse itch.io RSS
- Endpoint: `https://itch.io/jams.xml`
- Use `XmlPullParser` to parse `<item>` elements (title, description, link, pubDate)
- Map to `Project` model (name, theme parsed from description, endDate from feed data)
- Cache in memory for the session; expose `url` field on `Project` for browser intent

### D2 — `PublicJamsViewModel` — error + search state
- Add `_error: MutableStateFlow<String?>` for network failures
- Add `_searchQuery` state and `filteredJams` derived from `publicJams`
- Show error card in `PublicJamsScreen` when offline

### D3 — `PublicJamsScreen` — wire search, filters, actions
- Bind `searchQuery` to filter live
- Filter chips (All / Active / Upcoming / Archived) filter by `ProjectStatus`
- "View Details" → `Intent(ACTION_VIEW, Uri.parse(jam.url))`
- "Join Jam" → `viewModel.joinJam(jam)` → creates `Project` in Firestore with `memberIds = [currentUser]`

---

## Session E — User Role on Registration + REVIEW Status + Multi-User (S1 + S2 + S4)
**Goal:** Polish data model to fully match the proposal spec.

### E1 — `RegisterScreen` — role picker
- `FilterChip` row: Programmer / Artist / Musician (map to existing `UserRole` enum)
- Pass selected role to `AuthRepository.signUp` → store in Firestore

### E2 — Workspace — role badge on avatars
- Fetch role from user doc; display as small tag under initials avatar

### E3 — Status pickers — add REVIEW
- Add `TaskStatus.REVIEW` to dropdowns in `PersonalDashboardScreen` and `TeamWorkspaceScreen`

### E4 — `ProjectRepository` — multi-user query
- Add `memberIds` field to Firestore project documents on create
- Update `getMyJams` to union `creatorId == me` OR `memberIds contains me`

---

## Session F — Task Detail Screen + Navigation (S3)
**Goal:** Full task detail view and in-app navigation.

### F1 — Create `TaskDetailScreen.kt`
- Full fields: title, category badge, assignee, estimated time, status, notes
- Inline edit mode (tap any field)
- Delete with confirm dialog at bottom

### F2 — Wire navigation
- Add `TaskDetail/{taskId}` destination to `NavHost`
- Tap `TaskCard` → navigate with task ID
- Tap `PriorityTaskCard` on Dashboard → navigate

---

## Completion Checklist (vs Proposal)

```
[x] Criar Projetos (Jams)
[x] Geração de Roadmap via IA
[ ] Gestão de Tarefas — edit/delete missing       ← Session A
[x] Temporizador e Progresso
[ ] Botão de Pânico                               ← Session C (after B)
[x] Colaboração em Equipa — partial (no shared jams yet)
[x] Dashboard Pessoal
[ ] Pesquisa de Jams (The Arcade)                 ← Session D
[x] Modo Offline
[x] Autenticação e Dados
[ ] Subscrições Free/Premium                      ← Session B  ⭐ MUST-HAVE
```
