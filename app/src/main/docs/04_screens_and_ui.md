# 04. Screens and UI: LudumForge

Status of screen implementation as of June 2026.

## 📱 Screen Implementation State

| Screen (Proposal Name) | Code Name | Implementation State |
| :--- | :--- | :--- |
| **Dashboard Pessoal** | `PersonalDashboardScreen` | ✅ Complete |
| **The War Room** | `TeamWorkspaceScreen` | ✅ Complete (Edit/Delete sync) |
| **The Architect (IA)** | `RoadmapGeneratorScreen` | ✅ Complete (Gemini 2.5) |
| **Modo Terminal** | `OfflineTerminalScreen` | ✅ Complete (Panic Mode integrated) |
| **The Arcade (Public)** | `PublicJamsScreen` | ✅ Complete (Itch.io Live) |
| **Subscription** | `SubscriptionScreen` | ✅ Complete (Free/Premium logic) |
| **Auth** | `LoginScreen` / `RegisterScreen`| ✅ Complete |

## 🎨 UI Details & Accents
- **Theme:** Material 3 with Dynamic Theming (Dark Mode prioritized).
- **Fonts:** `Outfit` (Main), `Share Tech Mono` (Terminal/Timer).
- **Navigation:** Scaffold with Bottom Navigation Bar.
- **Components:** Custom `PriorityTaskCard`, `LudumForgeTopAppBar`, and `JoinJamBottomSheet`.

## 🛠️ Panic Mode (The Command Flow)
Implemented as a specialized terminal experience in `OfflineTerminalScreen`:
1.  **Trigger:** Typing `panic` in the command line.
2.  **Analysis:** System logs update in real-time as Gemini identifies non-essential tasks.
3.  **Confirmation:** Typing `confirm` triggers a batch deletion of suggested tasks to save the MVP.
4.  **Abort:** Typing `abort` cancels the crisis analysis.

## 🚧 Pending UI Polish
- **Role Selection:** Missing role picker (Programmer/Artist/Musician) in `RegisterScreen`.
- **Add Task Form:** Needs Category and Minutes fields (currently partial).
