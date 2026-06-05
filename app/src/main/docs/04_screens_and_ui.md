# 04. Screens and UI: LudumForge

The application follows a "Workspace" metaphor, dividing the experience into 5 main hubs.

## 1. The Arcade (Public)
- **Purpose:** Discovery and community engagement.
- **Features:** Feed of active Game Jams (Theme, Duration, Link).
- **UI Elements:** Search bar, cards with countdowns, "Find Team" buttons.

## 2. The Architect (AI Generator)
- **Purpose:** Initial project setup and planning.
- **Features:** Text input for game idea, sliders for team size, duration picker.
- **UI Elements:** Large text area, "Forge Roadmap" button with loading animation (AI processing).

## 3. Dashboard Pessoal (Private)
- **Purpose:** Primary focus screen during the event.
- **Features:** Global countdown timer, individual progress bars, personal task list.
- **UI Elements:** Circular timer, "Quick Note" fab, personal task checkboxes.

## 4. The War Room (Group/Shared)
- **Purpose:** Team synchronization.
- **Features:** Kanban board (To-Do, In-Progress, Done) synced across users.
- **UI Elements:** Swipeable columns, member avatars on task cards, "Panic Button" (bottom of the screen).

## 5. Modo Terminal (Offline)
- **Purpose:** Minimalist survival interface.
- **Features:** Monospaced font, cached timer, simple design notepad.
- **UI Elements:** Terminal-style text output, low-power mode visuals.

---

## 🎨 Design Principles
- **Theme:** Dark Mode by default (to reduce eye strain).
- **Colors:** Deep blues and mechanical grays with neon accents (Cyan for progress, Amber for warnings, Red for Panic).
- **Typography:** Share Tech Mono for the Terminal and Outfit for the main UI.
- **Interaction:** Material 3 Bottom Navigation for switching hubs.
