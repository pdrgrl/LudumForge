# 06. Architecture: LudumForge

The project follows **Clean Architecture** principles and **MVVM** (Model-View-ViewModel).

## 🏛️ Layers

### 1. UI Layer (Compose)
- **Composables:** Stateless UI components extracted into separate files.
- **ViewModels:** Managing screen state using `StateFlow`. Uses `flatMapLatest` and `combine` operators to manage reactive data streams efficiently.
- **Navigation:** Single NavHost with stable roots to prevent Activity reconstruction during session changes.

### 2. Domain Layer
- **Models:** Kotlin data classes (`Task`, `Project`, `User`).
- **Logic:** Business rules for roadmap parsing and task filtering.

### 3. Data Layer
- **Repositories:** Abstracting Firestore and Room.
- **Reactive Streams:** Uses `callbackFlow` for real-time Firestore listeners. 
- **Memory Safety:** Listeners are automatically disposed of using `flatMapLatest` when keys (like `activeJamId` or `userId`) change, preventing memory leaks and resource exhaustion.
- **Error Handling:** Graceful handling of "Permission Denied" errors during logout to prevent app crashes.

## 🔄 Current Implementation Status
- **Reactivity:** Fully reactive UI that responds to Firestore changes in real-time.
- **Offline First:** Room handles `ActionLog`. Projects and Tasks are streamed from Firestore with local state clearing on logout.
