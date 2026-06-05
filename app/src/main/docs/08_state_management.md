# 08. State Management: LudumForge

LudumForge uses **Unidirectional Data Flow (UDF)** to manage UI state consistently.

## 🏗️ State Patterns

### 1. UI State (Sealed Classes)
Every screen uses specific state holders to manage Loading, Success, and Error phases.

### 2. StateFlow & SharedFlow
- **StateFlow:** Used for persistent UI state (Jams, Tasks, Ratios).
- **SharedFlow:** Used for one-time events (Jam limit warnings, navigation commands).

## 🔄 Synchronization & Cleanup

### 🧪 "Clean Slate" Logout Flow
To prevent crashes and state leakage between users, the logout process follows a strict sequence:
1.  **Navigate:** Move the UI to the Login screen first.
2.  **Clear Local State:** Call `viewModel.clearData()` to wipe in-memory lists (tasks, jams).
3.  **Revoke Access:** Call `FirebaseAuth.signOut()`.

### 🚨 Memory Management
The app prevents listener accumulation by using the `flatMapLatest` operator. This ensures that only one set of Firestore listeners is active at any time for the current user/jam.
