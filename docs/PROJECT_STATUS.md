# LudumForge — Project Status
> Last updated: May 25, 2026 | DAM 2026 — Pedro Grilo nº 51319

---

## ✅ Screens — Implementation State

| Screen (Proposal Name) | Code Name | State |
|---|---|---|
| Dashboard Pessoal | `PersonalDashboardScreen` | ✅ Complete |
| The War Room | `TeamWorkspaceScreen` | ✅ Complete |
| The Architect (IA) | `RoadmapGeneratorScreen` | ✅ Complete |
| Modo Terminal (Offline) | `OfflineTerminalScreen` | ✅ Complete |
| The Arcade (Public) | `PublicJamsScreen` | ✅ Complete |
| Subscription / Premium | `SubscriptionScreen` | ✅ Complete |
| Login / Register | `LoginScreen`, `RegisterScreen` | ✅ Complete |

---

## ✅ Core Features — Implementation State

| Feature | State | Notes |
|---|---|---|
| Firebase Auth (login/register) | ✅ Done | Email + password via `AuthRepository` |
| Create Jams (with duration picker) | ✅ Done | Firestore, duration sets real `endDate` |
| Real deadline timer | ✅ Done | Driven by `activeJam.endDate` |
| Rename / Delete Jam | ✅ Done | Includes task cleanup |
| Real-time task stream (Firestore) | ✅ Done | Live sync in Workspace |
| Create Task (with assignee) | ✅ Done | UI for title/assignee done |
| Task status change (Workspace/Dashboard) | ✅ Done | Includes `REVIEW` status |
| Task Edit (title/category/assignee) | ✅ Done | Edit sheet exists in Workspace |
| Task Delete | ✅ Done | Confirm dialog + repository call |
| AI Roadmap Generation (Gemini) | ✅ Done | Selectable review step |
| Offline Room DB (action logs) | ✅ Done | Sync loop + manual retry |
| Public Jams feed (itch.io) | ✅ Done | Live scraping using Jsoup |
| Shared Jams / Invites | ✅ Done | `memberIds` array + Deep Link invite support |
| Subscription (Free/Premium) | ✅ Done | 2-jam limit for FREE, unlimited for PREMIUM |
| Category/Minutes in Edit form | ✅ Done | Surfaced in the edit bottom sheet |
| Category/Minutes in Add form | 🟡 Partial | Fields missing from the "Add Task" sheet UI |
| Panic Button | 🔴 Not started | UI teaser exists, no logic implemented |
| User role (Programmer/Artist/Musician) | 🔴 Not started | Logic exists in models, UI picker missing from Register |

---

## 🏗️ Data Model — Current vs Proposal

| Field | Proposal | Current |
|---|---|---|
| Project.id | ✅ | ✅ |
| Project.name | ✅ | ✅ |
| Project.theme | ✅ | ✅ |
| Project.startDate | ✅ | ✅ |
| Project.endDate | ✅ | ✅ |
| Project.teamSize | ✅ | ✅ |
| Project.creatorId | — | ✅ Added |
| Project.memberIds | ✅ | ✅ Added |
| Task.category (Code/Art/Audio) | ✅ | ✅ Implemented |
| Task.estimatedMinutes | ✅ | ✅ Implemented |
| User.role (Programmer/Artist/Musician) | ✅ | 🔴 Not stored during registration |
| User.plan (Free/Premium) | ✅ | ✅ Implemented |

---

## 📁 Architecture Overview

```
ui/screens/          → Composable screens (1 per feature)
ui/navigation/       → NavHost with Route definitions
ui/components/       → Shared UI (TopAppBar)
ui/theme/            → Color tokens and Material3 theme
viewmodels/          → MVVM ViewModels (some shared via hoisting)
data/repositories/   → Firestore + Room + Web scraping logic
data/daos/           → Room DAOs (SQLite caching)
models/              → Data classes (Project, Task, User, ActionLog)
data/SessionManager  → Global active jam state (StateFlow)
```

