# LudumForge — Project Status
> Last updated: May 2, 2026 | DAM 2026 — Pedro Grilo nº 51319

---

## ✅ Screens — Implementation State

| Screen (Proposal Name) | Code Name | State |
|---|---|---|
| Dashboard Pessoal | `PersonalDashboardScreen` | ✅ Complete |
| The War Room | `TeamWorkspaceScreen` | ✅ Complete |
| The Architect (IA) | `RoadmapGeneratorScreen` | ✅ Complete |
| Modo Terminal (Offline) | `OfflineTerminalScreen` | ✅ Complete |
| The Arcade (Public) | `PublicJamsScreen` | 🟡 UI shell only — dummy data |
| Login / Register | `LoginScreen`, `RegisterScreen` | ✅ Complete |

---

## ✅ Core Features — Implementation State

| Feature | State | Notes |
|---|---|---|
| Firebase Auth (login/register) | ✅ Done | Email + password via `AuthRepository` |
| Create Jams (with duration picker) | ✅ Done | Firestore, duration sets real `endDate` |
| Real deadline timer | ✅ Done | Driven by `activeJam.endDate`, switches on jam select |
| Rename / Delete Jam | ✅ Done | Cascade-deletes all tasks on delete |
| Real-time task stream (Firestore) | ✅ Done | `flatMapLatest` on `activeJamId` |
| Create Task (with assignee) | ✅ Done | Includes assignee picker and initials avatars |
| Task status change (Workspace) | ✅ Done | Dropdown per card |
| Task status change (Dashboard) | ✅ Done | Tap card → dropdown |
| Task filter / search | ✅ Done | Live filter in Workspace |
| Assignee filter chips | ✅ Done | Filter by team member in Workspace |
| AI Roadmap Generation (Gemini) | ✅ Done | Selectable review step before push |
| Offline Room DB (action logs) | ✅ Done | Scoped to active jam |
| Manual dev notes (Terminal) | ✅ Done | Submitted to Room, synced to Firestore |
| Background sync loop | ✅ Done | Every 10s in `OfflineTerminalViewModel` |
| Manual retry sync button | ✅ Done | Shows spinner while syncing |
| Active jam name in TopBar | ✅ Done | Via `SessionManager.activeJamName` |
| Jam completion progress bar | ✅ Done | Live task ratio per jam card |
| No-jam empty state (Workspace) | ✅ Done | Full empty state screen with guidance |
| Public Jams feed (itch.io) | 🔴 Stub | `ItchRepository` exists, returns dummy data |
| Panic Button | 🔴 Not started | Required by proposal |
| Task edit (title/category/assignee) | 🔴 Not started | No edit sheet exists |
| Task delete | 🔴 Not started | No delete action on tasks |
| `REVIEW` status visible in UI | 🟡 Partial | Exists in model, not shown in status pickers |
| Multi-user / shared jams | 🔴 Not started | Only `creatorId` query, no team membership |
| Subscription (Free/Premium) | 🔴 Not started | Mentioned in proposal, not scoped |

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
| Project.status | ✅ | ✅ |
| Project.creatorId | — | ✅ Added |
| Task.category (Code/Art/Audio) | ✅ | 🟡 Field exists in model, not used in UI filters |
| Task.estimatedMinutes | ✅ | 🟡 Field exists in model, not set in create form |
| User.role (Programmer/Artist/Musician) | ✅ | 🔴 Not stored |
| Team entity (membersList) | ✅ | 🔴 Not implemented |

---

## 📁 Architecture Overview

```
ui/screens/          → Composable screens (1 per feature)
ui/components/       → Shared UI (TopAppBar)
ui/theme/            → Color tokens
viewmodels/          → 1 ViewModel per screen, MVVM
data/repositories/   → Firestore + Room abstraction layer
data/daos/           → Room DAOs
models/              → Data classes (Project, Task, User, ActionLog)
data/SessionManager  → Global active jam state (StateFlow)
```
