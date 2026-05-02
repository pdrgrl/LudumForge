# LudumForge — Implementation Plan
> Ordered session-by-session plan. Each session ~2-3h.

---

## Session A — Task Edit & Delete + Category/Minutes in Form (M2 + M4)
**Goal:** Make tasks fully editable and deletable. Also surface the missing `category` and `estimatedMinutes` fields.

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
fun updateTask(taskId: String, updates: Map<String, Any>) { ... }
fun deleteTask(taskId: String) { ... }
```

### A3 — `TeamWorkspaceScreen` — Edit bottom sheet + delete confirm
- Reuse the existing `AddTaskBottomSheet` pattern with pre-filled fields
- Long-press `TaskCard` → show edit sheet
- Swipe or icon tap → confirm-then-delete
- Add `category` FilterChip row (Code / Art / Audio) and `estimatedMinutes` TextField to the Add/Edit form

### A4 — `TaskCard` — show category badge
- Small pill badge (Code=Blue, Art=Purple, Audio=Orange) on each card

---

## Session B — Panic Button (M1)
**Goal:** Implement the emergency MVP generator.

### B1 — `RoadmapGeneratorViewModel` — add `triggerPanicMode(context, currentTasks, timeLeftSeconds)`
- Build prompt: current tasks serialised as JSON + time remaining
- Ask Gemini: "Given these tasks and X minutes left, return ONLY the MVP-critical ones as JSON"
- Reuse existing `RoadmapUiState` states (Loading → Success → review step)
- On confirm: call `deleteTask` for dropped tasks, keep MVP ones

### B2 — `TeamWorkspaceScreen` — Panic Button
- Red secondary FAB or top-right icon button
- Confirm dialog: "⚠️ Panic Mode will remove non-essential tasks. Continue?"
- Triggers `viewModel.triggerPanicMode(...)`
- Logs a SYSTEM event: `"PANIC MODE activated — X tasks dropped"`

---

## Session C — Public Jams Feed (M3)
**Goal:** Replace dummy data with real itch.io jam data.

### C1 — `ItchRepository` — scrape itch.io RSS
- itch.io exposes a public RSS feed for game jams: `https://itch.io/jams.xml`
- Parse XML with `XmlPullParser` or `Jsoup`
- Map to `Project` model fields (name, theme inferred from description, endDate from deadline)
- Cache result in memory for the session

### C2 — `PublicJamsViewModel` — error state
- Add `error: String?` state for network failures
- Show error card in `PublicJamsScreen` when offline

### C3 — `PublicJamsScreen` — wire search and filters
- Connect `searchQuery` to filter `publicJams` list by name/theme
- Filter chips (All / Active / Upcoming / Archived) filter by `ProjectStatus`
- "View Details" → `Intent` to open itch.io URL in browser
- "Join Jam" → creates a new `Project` in user's Firestore with the jam's data pre-filled

---

## Session D — User Role + REVIEW Status + Multi-User (S1 + S2 + S4)
**Goal:** Polish the data model to fully match the proposal spec.

### D1 — `RegisterScreen` — role picker
- `FilterChip` row: Programmer / Artist / Musician
- Store `role` field in Firestore `users/{uid}` document via `AuthRepository`

### D2 — `TeamWorkspaceScreen` — show role badge on avatars
- Fetch `users` documents by ID for each team member
- Display role as a small tag under the initials circle

### D3 — Status pickers — add REVIEW
- Add `TaskStatus.REVIEW` to all dropdown menus in `PersonalDashboardScreen` and `TeamWorkspaceScreen`

### D4 — `ProjectRepository` — multi-user query
- Add `memberIds` array field to Firestore project documents
- Update `getMyJams` to union query `creatorId == me` OR `memberIds contains me`
- "Join Jam" from The Arcade adds current user to `memberIds`

---

## Session E — Task Detail Screen + Navigation (S3)
**Goal:** Add a proper task detail/edit screen and wire navigation.

### E1 — Create `TaskDetailScreen.kt`
- Full task view: title, category badge, assignee, estimated time, status, description
- Inline edit mode (tap to edit any field)
- Delete button with confirm

### E2 — Wire navigation
- Add `TaskDetail` destination to nav graph
- Tap `TaskCard` in Workspace → navigate to `TaskDetailScreen`
- Tap `PriorityTaskCard` in Dashboard → navigate to `TaskDetailScreen`

---

## Completion Checklist (vs Proposal)

```
[x] Criar Projetos (Jams)
[x] Geração de Roadmap via IA
[x] Gestão de Tarefas — partial (no edit/delete yet)
[x] Temporizador e Progresso
[ ] Botão de Pânico                        ← Session B
[x] Colaboração em Equipa — partial (assignee, no shared jams yet)
[x] Dashboard Pessoal
[ ] Pesquisa de Jams (The Arcade)          ← Session C
[x] Modo Offline
[x] Autenticação e Dados
```
