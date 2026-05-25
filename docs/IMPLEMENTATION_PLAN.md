# LudumForge — Implementation Plan
> Ordered session-by-session plan. Each session ~2-3h.
> Last updated: May 25, 2026

---

## ✅ Session A — Task Edit & Delete + Category/Minutes (DONE)
- **Completed:** TaskRepository updated, ViewModel exposes update/delete, and Workspace Screen uses a pre-filled edit sheet.
- **Note:** Category chips and minutes are implemented in the Edit sheet but still need to be ported to the Add Task sheet.

## ✅ Session B — Subscription System (DONE)
- **Completed:** Free/Premium model fully end-to-end. One-tap upgrade logic integrated into `PersonalDashboardViewModel`.
- **Note:** The planned `SubscriptionViewModel` was merged into the hoisted `PersonalDashboardViewModel` for better state sharing.

## ✅ Session D — Public Jams Feed (DONE)
- **Completed:** Real itch.io scraping using Jsoup. "Join Jam" adds user to `memberIds`.

## ✅ Session E — Multi-User & Shared Jams (DONE)
- **Completed:** `memberIds` array added to Firestore. Union queries implemented for "My Jams". Deep link invites functional.

---

## 🏃 Session C — Panic Button (IN PROGRESS)
**Goal:** Implement the emergency MVP generator.
- [ ] C1 — `triggerPanicMode(context)` in `TeamWorkspaceViewModel`.
- [ ] C2 — Review Step using Gemini to drop extra tasks.
- [ ] C3 — Panic FAB in UI.

## 🏃 Session F — User Roles & Final UI Polish (IN PROGRESS)
**Goal:** Complete the proposal data model in the UI.
- [ ] F1 — Role picker in `RegisterScreen.kt`.
- [ ] F2 — "Add Task" sheet category/minutes fields.
- [ ] F3 — Task Detail Screen (Optional/S-Tier).
