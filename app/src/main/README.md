# LudumForge 🛠️🎮

**Empowering Game Jams with AI-Driven Planning.**

LudumForge is an Android application designed to help game developers and creative teams manage and organize game development marathons (Game Jams). It uses Artificial Intelligence to automatically generate realistic task plans based on game ideas, team size, and time limits, tracking progress in real-time.

## 🚀 Key Features

*   **IA Roadmap Generation:** Input your game idea and get a minute-by-minute task plan categorized by Code, Art, and Audio.
*   **Panic Button:** Emergency feature that trims non-essential tasks to ensure a viable MVP (Minimum Viable Product) before the deadline.
*   **Real-time Collaboration:** Sync task status across the team via Firebase.
*   **Offline Mode:** Keep the timer running and access your logic notes even without an internet connection.
*   **The Arcade:** A public feed to search for active Game Jams (via itch.io).
*   **The War Room:** A team Kanban board for total synchronization.

## 🏗️ Architecture

*   **Language:** Kotlin
*   **UI:** Jetpack Compose
*   **Backend:** Firebase (Auth, Firestore)
*   **Local Database:** Room (SQLite)
*   **AI Integration:** LLM API with hidden Prompt Engineering for JSON structured outputs.

## 📂 Project Structure

For detailed documentation, please refer to the `docs/` folder:
- [Project Vision](docs/01_project_vision.md)
- [Requirements](docs/02_requirements.md)
- [Architecture](docs/06_architecture.md)
- [Data Model](docs/07_data_model.md)

## 🛠️ Getting Started

1. Clone the repository.
2. Open in Android Studio.
3. Configure your Firebase and AI API keys in `local.properties`.
4. Run on an Android device or emulator.

---
*Developed for DAM 2026 - Pedro Grilo (51319)*
