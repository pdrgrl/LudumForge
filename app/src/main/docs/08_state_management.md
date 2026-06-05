# 08. State Management: LudumForge

LudumForge uses **Unidirectional Data Flow (UDF)** to manage UI state consistently.

## 🏗️ State Patterns

### 1. UI State (Sealed Classes)
Every screen has a corresponding `State` sealed class to handle different lifecycle phases:
```kotlin
sealed class ScreenState<out T> {
    object Idle : ScreenState<Nothing>()
    object Loading : ScreenState<Nothing>()
    data class Success<T>(val data: T) : ScreenState<T>()
    data class Error(val message: String) : ScreenState<Nothing>()
}
```

### 2. StateFlow & SharedFlow
- **StateFlow:** Used for persistent UI state (e.g., the current task list). Collected by Composables as `collectAsStateWithLifecycle()`.
- **SharedFlow:** Used for one-time events (e.g., showing a Snackbar when the "Panic Button" is pressed, navigating after AI generation).

## 🔄 Synchronization State
Since the app works **Offline-First**, we track sync status:
- **Local:** Immediate update in Room.
- **Syncing:** Showing a small cloud icon indicating data is being pushed to Firestore.
- **Synced:** Data confirmed in the cloud.

## 🚨 Emergency State (The Panic Mode)
When the "Panic Button" is triggered, a global flag is set in the `ProjectViewModel`. This updates the UI theme to high-alert (Red accents) and triggers the task-trimming logic across all active state collectors.
