# 05. Navigation: LudumForge

## 🗺️ Navigation Structure

### 1. Root Level (Main Activity)
- **Auth Flow:** Login -> Register (or Social Login).
- **Main App:** Scaffold with Bottom Navigation and Top App Bar.

### 2. Bottom Navigation Hubs
- **Home (Dashboard):** Personal progress and timer.
- **Arcade:** Public jam feed.
- **Architect:** AI generation entry point.
- **War Room:** Shared team board.
- **Terminal:** Offline/Minimalist view.

### 3. Hierarchical Navigation (Destinations)
- **Project Setup:** From Arcade/Architect -> Setup Screen -> Success Feedback.
- **Task Detail:** From Dashboard/War Room -> Task Edit (Bottom Sheet).
- **Subscription:** From Profile Icon -> Subscription Screen.

## 🛠️ Implementation Strategy
- **Library:** `androidx.navigation:navigation-compose`.
- **Type-Safety:** Use a `Screen` sealed class to define routes and arguments.
- **Transitions:** Standard fade and slide animations for hub switching.

## 🏗️ State and Context
- The `currentProject` is stored in a `ProjectViewModel` shared across the Architect and War Room.
- Navigation should handle cases where no project is active (redirecting to Architect or Arcade).
