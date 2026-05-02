# LudumForge — Feature Roadmap
> Priority order for remaining work to meet DAM 2026 proposal requirements.
> Last updated: May 2, 2026

---

## 🔴 Must-Have (Proposal Requirements Not Yet Met)

### M1 — Subscription System (Free vs Premium)
**What:** Every new user is `FREE` by default. A single-tap upgrade flow grants `PREMIUM`. A real paywall UI exists (fake price, plan comparison table) but no real payment is processed — this is a university project demo.

**Free tier limits:**
- Max **2 team jams created per calendar month**
- Must supply their own **Gemini API key** in Settings for AI generation
- Panic Button is **locked** (teaser shown, upgrade CTA)
- Standard task creation and management

**Premium tier unlocks:**
- **Unlimited** team jam creation
- **App-managed** Gemini API key (no key required from user)
- **Panic Button** enabled
- Premium badge shown on profile / avatar in the Workspace

**Architecture approach:**
1. Add `plan: UserPlan` (`FREE` / `PREMIUM`) to `User` model + Firestore `users/{uid}` doc
2. Add `monthlyJamsCreated: Int` and `lastResetMonth: String` (ISO month `"2026-05"`) to Firestore user doc for limit tracking
3. Enforce in `ProjectRepository.createJam` — read user plan before allowing creation
4. Expose `isPremium: StateFlow<Boolean>` from a new `SubscriptionViewModel` (or via `AuthRepository`)
5. Gate UI: dim the Create Jam button with an upgrade tooltip when limit reached; gate Panic Button access
6. New **`SubscriptionScreen.kt`** accessible from Settings:
   - Plan comparison table (Free vs Premium columns)
   - Fake price card: **€3.99 / month** (styled like a real paywall)
   - Single **"Upgrade to Premium"** button → writes `plan = PREMIUM` to Firestore → instantly reflects
   - "Already Premium" state shows badge + cancel mock

**Files to create/touch:**
`User.kt`, `AuthRepository.kt`, `ProjectRepository.kt`,
new `SubscriptionViewModel.kt`, new `SubscriptionScreen.kt`,
`PersonalDashboardViewModel.kt`, `RoadmapGeneratorViewModel.kt`,
`TeamWorkspaceScreen.kt` (gate Panic Button),
`LudumForgeTopAppBar.kt` / Settings dialog (link to SubscriptionScreen)

---

### M2 — Panic Button
**What:** An emergency AI action that drops non-essential tasks (polish, extras) and presents a minimal MVP survival list. **Locked behind Premium.**

- Red "Panic" FAB or top-right icon button in `TeamWorkspaceScreen`
- Gated: if `!isPremium` → show upgrade dialog instead of triggering
- On confirm: serialise current non-done tasks as JSON + send remaining time to Gemini
- Gemini prompt: *"Given these tasks and X minutes left, return ONLY the MVP-critical ones as a JSON array. Drop everything else."*
- Returns to the same review step as the Roadmap Generator (user can inspect before committing)
- On accept: delete dropped tasks from Firestore, keep MVP ones
- Logs a `SYSTEM` event: `"🚨 PANIC MODE — X tasks dropped, Y kept"`

**Files to touch:**
`RoadmapGeneratorViewModel.kt` (or new `PanicViewModel.kt`),
`TaskRepository.kt`, `TeamWorkspaceScreen.kt`, `TeamWorkspaceViewModel.kt`

---

### M3 — Task Edit & Delete
**What:** Full task management — edit title, category, estimated minutes, assignee; delete a task.

- Bottom sheet edit form (reuse Add Task sheet, pre-populated)
- Long-press `TaskCard` → edit sheet
- Trash icon or swipe → confirm dialog → delete
- `TaskRepository.updateTask(taskId, updates: Map<String, Any>)`
- `TaskRepository.deleteTask(taskId)`
- Action logged to Offline Terminal

**Files to touch:** `TeamWorkspaceScreen.kt`, `TeamWorkspaceViewModel.kt`, `TaskRepository.kt`

---

### M4 — Public Jams Feed (The Arcade)
**What:** Replace dummy data in `PublicJamsScreen` with real itch.io jam data.

- `ItchRepository.getLiveJams()` currently returns hardcoded stubs
- Parse itch.io public RSS feed (`https://itch.io/jams.xml`) using `XmlPullParser`
- Wire search query + filter chips to the real list
- "View Details" → `Intent` to open itch.io URL in browser
- "Join Jam" → creates a new `Project` in user's Firestore with jam data pre-filled + adds user to `memberIds`

**Files to touch:** `ItchRepository.kt`, `PublicJamsViewModel.kt`, `PublicJamsScreen.kt`

---

### M5 — Task Category & EstimatedMinutes in Create Form
**What:** `category` and `estimatedMinutes` exist in the `Task` model and are populated by the AI, but the manual create form doesn't set them.

- Add `FilterChip` row (Code / Art / Audio / Design / QA) to the Add Task bottom sheet
- Add `estimatedMinutes` `TextField` (number input)
- Pass both fields in `TeamWorkspaceViewModel.addTask()`
- Show category badge pill on `TaskCard`

**Files to touch:** `TeamWorkspaceScreen.kt`, `TeamWorkspaceViewModel.kt`, `TaskRepository.kt`

---

## 🟡 Should-Have (Quality & Completeness)

### S1 — User Role on Registration
**What:** The proposal defines `User.role` (Programmer / Artist / Musician). The model and Firestore storage exist, but the role is never chosen by the user and never displayed.

- Add role picker `FilterChip` row to `RegisterScreen`
- Store in Firestore via `AuthRepository.saveUserToFirestore`
- Display role tag under initials avatar in Workspace

**Files to touch:** `RegisterScreen.kt`, `AuthRepository.kt`, `TeamWorkspaceScreen.kt`

---

### S2 — REVIEW Status in UI
**What:** `TaskStatus.REVIEW` exists in the model but is missing from all status pickers.

- Add `REVIEW` to dropdowns in `PersonalDashboardScreen` and `TeamWorkspaceScreen`

**Files to touch:** `PersonalDashboardScreen.kt`, `TeamWorkspaceScreen.kt`

---

### S3 — Task Detail Screen
**What:** No dedicated task view screen or navigation exists.

- New `TaskDetailScreen.kt` (full fields, inline edit, delete)
- Tap `TaskCard` in Workspace or `PriorityTaskCard` in Dashboard → navigate to it
- Requires nav graph wiring in `MainScreen`/`NavHost`

**Files to touch:** New `TaskDetailScreen.kt`, nav graph

---

### S4 — Multi-User / Shared Jams
**What:** `getMyJams` only queries `creatorId == currentUser`. Team members can't see a jam they were added to.

- Add `memberIds: List<String>` to Firestore `projects` documents
- Union query: `creatorId == me` OR `memberIds contains me`
- "Join Jam" from The Arcade adds user to `memberIds`

**Files to touch:** `ProjectRepository.kt`, `PersonalDashboardViewModel.kt`

---

## 🟢 Nice-to-Have

| ID | Feature |
|---|---|
| N1 | Post-jam productivity stats screen |
| N2 | Notification / alarm when deadline is < 1 hour away |
| N3 | Offline task cache in Room (currently only action logs are cached) |
| N4 | Dark mode support (theme token structure already in place) |
