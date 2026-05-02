# LudumForge — Feature Roadmap
> Priority order for remaining work to meet DAM 2026 proposal requirements.

---

## 🔴 Must-Have (Proposal Requirements Not Yet Met)

### M1 — Panic Button
**What:** An emergency AI action that drops non-essential tasks (polish, extras) and regenerates a minimal MVP survival list.
- New button on `TeamWorkspaceScreen` (FAB or top action)
- Calls Gemini with current task list + remaining time as context
- Prompts AI to return only MVP-critical tasks
- Replaces non-done tasks (or lets user review before applying)
- Logs a `SYSTEM` event to the Offline Terminal

**Files to touch:** `RoadmapGeneratorViewModel`, `TaskRepository`, `TeamWorkspaceScreen`, `TeamWorkspaceViewModel`

---

### M2 — Task Edit & Delete
**What:** Users can edit an existing task's title, category, estimated minutes, and assignee; or delete it.
- Bottom sheet edit form (reuse Add Task sheet pattern)
- Long-press or swipe-to-reveal on `TaskCard`
- `TaskRepository.updateTask()` and `TaskRepository.deleteTask()`
- Confirm dialog on delete

**Files to touch:** `TeamWorkspaceScreen`, `TeamWorkspaceViewModel`, `TaskRepository`

---

### M3 — Public Jams Feed (The Arcade)
**What:** Replace dummy data in `PublicJamsScreen` with real itch.io jam data.
- `ItchRepository.getLiveJams()` currently returns hardcoded stubs
- Scrape or use itch.io RSS/API to get active jams
- Wire search query and filter chips to filter the real list
- "View Details" → open itch.io URL in browser
- "Join Jam" → import jam as a new Project into the user's dashboard

**Files to touch:** `ItchRepository`, `PublicJamsViewModel`, `PublicJamsScreen`

---

### M4 — Task Category & EstimatedMinutes in Create Form
**What:** The `Task` model has `category` (Code/Art/Audio) and `estimatedMinutes` but neither is set at creation.
- Add category selector (FilterChip row: Code / Art / Audio) to the Add Task bottom sheet
- Add estimated minutes number input
- Pass both fields when creating task in `TeamWorkspaceViewModel.addTask()`
- Show category badge on `TaskCard`

**Files to touch:** `TeamWorkspaceScreen`, `TeamWorkspaceViewModel`, `TaskRepository`

---

## 🟡 Should-Have (Quality & Completeness)

### S1 — REVIEW Status in UI
**What:** `TaskStatus.REVIEW` exists in the model but is never shown in any status picker. Add it to all dropdowns.

**Files to touch:** `PersonalDashboardScreen`, `TeamWorkspaceScreen`

---

### S2 — User Role on Registration
**What:** The proposal defines `User.role` (Programmer / Artist / Musician). Currently not stored or shown.
- Add role picker to `RegisterScreen`
- Store in Firestore `users` collection
- Display role badge on team member avatars in Workspace

**Files to touch:** `RegisterScreen`, `AuthRepository`, `TeamWorkspaceScreen`

---

### S3 — Task Detail Screen
**What:** No screen exists to view or edit a single task in full. Navigation is fully absent.
- New `TaskDetailScreen.kt` with full fields
- Tap on a task in Workspace or Dashboard navigates to it
- Edit mode inline

**Files to touch:** New `TaskDetailScreen`, `NavGraph`/`MainScreen` navigation

---

### S4 — Multi-User / Shared Jams
**What:** `getMyJams` only queries `creatorId == currentUser`. Jams shared with other team members are invisible.
- Add `memberIds: List<String>` field to the Firestore `projects` document
- Query with `whereArrayContains("memberIds", currentUserId)` OR `whereEqualTo("creatorId", ...)`
- "Join Jam" from The Arcade sets this up

**Files to touch:** `ProjectRepository`, `PersonalDashboardViewModel`

---

## 🟢 Nice-to-Have

| ID | Feature |
|---|---|
| N1 | Subscription model (Free: 2 jams/month limit; Premium: unlimited) |
| N2 | Post-jam productivity stats screen |
| N3 | Notification / alarm when deadline is < 1 hour away |
| N4 | Offline task cache in Room (currently only action logs are cached) |
| N5 | Dark mode support (theme already has tokens) |
