# 04. Screens and UI: LudumForge

Status of screen implementation as of June 2026.

## 📱 Screen Implementation State

| Screen (Proposal Name) | Code Name | Implementation State |
| :--- | :--- | :--- |
| **Dashboard Pessoal** | `PersonalDashboardScreen` | ✅ Complete |
| **The War Room** | `TeamWorkspaceScreen` | ✅ Complete (Edit/Delete sync) |
| **The Architect (IA)** | `RoadmapGeneratorScreen` | ✅ Complete (Gemini 2.5) |
| **Modo Terminal** | `OfflineTerminalScreen` | ✅ Complete (Monospaced UI) |
| **The Arcade (Public)** | `PublicJamsScreen` | ✅ Complete (Itch.io Live) |
| **Subscription** | `SubscriptionScreen` | ✅ Complete (Free/Premium logic) |
| **Auth** | `LoginScreen` / `RegisterScreen`| ✅ Complete |

## 🎨 UI Details & Accents
- **Theme:** Material 3 with Dynamic Theming (Dark Mode prioritized).
- **Fonts:** `Outfit` (Main), `Share Tech Mono` (Terminal/Timer).
- **Navigation:** Scaffold with Bottom Navigation Bar.
- **Components:** Custom `PriorityTaskCard`, `LudumForgeTopAppBar`, and `JoinJamBottomSheet`.

## 🚧 Pending UI Polish
- **Role Selection:** Missing role picker (Programmer/Artist/Musician) in `RegisterScreen`.
- **Add Task Form:** Needs Category and Minutes fields (currently partial).
- **Panic Button:** UI teaser exists in Subscription, but needs a FAB in the War Room.
