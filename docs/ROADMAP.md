# LudumForge — Feature Roadmap
> Priority order for final polish to meet DAM 2026 proposal requirements.
> Last updated: May 25, 2026

---

## 🔴 Must-Have (Final Requirements)

### M1 — Panic Button Implementation
**What:** An emergency AI action that drops non-essential tasks (polish, extras) and presents a minimal MVP survival list.
- **State:** UI teaser exists in SubscriptionScreen. Logic is missing.
- **Todo:** 
    - Add "Panic" FAB to `TeamWorkspaceScreen`.
    - Implement `triggerPanicMode` in `TeamWorkspaceViewModel`.
    - Gemini prompt logic to filter tasks by MVP importance.
    - Review step to confirm task deletion.

### M2 — User Role on Registration
**What:** Users should select their role (Programmer / Artist / Musician) during sign-up.
- **State:** `UserRole` enum exists. `AuthRepository` can store it. UI is missing.
- **Todo:**
    - Add role picker `FilterChip` row to `RegisterScreen.kt`.
    - Pass selection to `authViewModel.signUp`.
    - Update Workspace to show role badges on user avatars.

### M3 — Task Category & Minutes in "Add Task" Form
**What:** These fields are in the code/edit form but missing from the manual "Add" flow.
- **Todo:** 
    - Add Category `FilterChip` row and `estimatedMinutes` text field to the "Add Task" bottom sheet in `TeamWorkspaceScreen.kt`.

---

## 🟡 Should-Have (Quality & Completeness)

### S1 — Dedicated Task Detail Screen
**What:** Currently tasks are managed via bottom sheets. A dedicated screen would allow for longer notes/logs.
- **Todo:**
    - Create `TaskDetailScreen.kt`.
    - Wire navigation from `TaskCard` and `PriorityTaskCard`.

---

## ✅ Completed (Moved from Roadmap)
- **Subscription System (Free/Premium):** Fully functional with 2-jam limit for free users.
- **Task Edit & Delete:** fully implemented in Workspace.
- **Public Jams Feed:** Real-time itch.io scraping implemented.
- **Multi-user / Shared Jams:** Firestore `memberIds` and deep-link invites implemented.
- **REVIEW Status:** Integrated into workspace and dashboard.

