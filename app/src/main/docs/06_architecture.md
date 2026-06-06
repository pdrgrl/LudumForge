# 06. Architecture: LudumForge

The project follows **Clean Architecture** principles and **MVVM** (Model-View-ViewModel).

## 🏛️ Layers

### 1. UI Layer (Compose)
- **Composables:** Stateless components with hoisted state.
- **ViewModels:** Managing reactive state. Uses `flatMapLatest` and `combine` for leak-free Firestore streams.
- **Unified Auth:** App-wide shared `AuthViewModel` ensures consistent session state across all screens.

### 2. Domain Layer
- **Models:** Kotlin data classes (`Task`, `Project`, `User`).
- **Reactive Logic:** Business rules for calculating jam progress and filtering tasks are handled within the ViewModels using reactive operators.

### 3. Data Layer
- **Repositories:** Abstracting Firestore (Remote) and Room (Local).
- **Real-time Sync:** `callbackFlow` provides immediate UI updates for task deletions, status changes, and project renames.
- **Shared Caching:** Public Jams from itch.io are now cached in a shared Firestore collection (`public_jams`). This reduces the need for expensive web scraping and ensures all users see a consistent, performant feed.
- **Self-Healing Listeners:** Internal tracking maps are cleared on each snapshot to ensure local state perfectly matches remote truth (fixing the persistence bug on deletions).

## 🔄 Stability & Lifecycle
- **Safe Logout:** Navigation triggers before session revocation to prevent permission crashes.
- **State Clearing:** `clearData()` wipes in-memory caches on logout to ensure a clean slate for subsequent users.
- **Auto-Completion:** The ViewModel automatically updates a project's status to `COMPLETED` via the repository when the countdown timer hits zero in the Personal Dashboard.
