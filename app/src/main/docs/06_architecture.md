# 06. Architecture: LudumForge

The project follows **Clean Architecture** principles and **MVVM** (Model-View-ViewModel).

## 🏛️ Layers

### 1. UI Layer (Compose)
- **Composables:** Stateless UI components extracted into separate files for maintainability.
- **ViewModels:** Managing screen state using `StateFlow`. Uses `flatMapLatest` on `activeJamId` for real-time reactivity.
- **Theme:** Material 3 with dynamic theming and custom fonts (`Outfit`, `Share Tech Mono`).

### 2. Domain Layer
- **Models:** Kotlin data classes used across the app (`Task`, `Project`, `User`).
- **Logic:** Business rules for roadmap parsing and task filtering (to be fully extracted into UseCases).

### 3. Data Layer
- **Repositories:** Abstracting Firebase and Room.
- **Remote Source:** Firestore is the primary source of truth for `Tasks` and `Projects`.
- **Local Source:** Room (`LudumForgeDatabase`) currently handles `ActionLog` for system events. Caching for Tasks and Projects is defined but pending full repository wiring.
- **Session Management:** `SessionManager` tracks the `activeJamId` globally across the app.

## 🔄 Current Implementation Status
- **Reactivity:** Using `callbackFlow` in repositories to stream Firestore snapshots directly to the UI.
- **AI Integration:** `RoadmapGeneratorViewModel` interfaces with `gemini-2.5-flash-lite` using the `GenerativeAI` SDK.
- **Offline First:** Currently "Online First". Room is used for logging, with a roadmap to extend it as a full offline cache for tasks.
