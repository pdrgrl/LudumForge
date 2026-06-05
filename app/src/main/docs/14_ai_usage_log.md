# 14. AI Usage Log: LudumForge

Use this file to document significant help from AI agents during the development of LudumForge.

| Date | Agent | Task | Description of Help | Impact |
| :--- | :--- | :--- | :--- | :--- |
| 2026-04-20 | Gemini | Real-time Sync | Refactored `TeamWorkspaceViewModel` to use `flatMapLatest` on `activeJamId` to update tasks automatically. | high |
| 2026-04-23 | Gemini | Batch Operations | Created the logic for `deleteTasksBatch` and optimized Firestore writes. | medium |
| 2026-05-02 | Gemini | Subscription Logic | Designed the `gate on active jam count` logic to differentiate between Free and Premium users. | high |
| 2026-05-25 | Gemini | AI Brainstorming | Helper for Gemini prompt engineering to ensure JSON-only output from the LLM. | high |
| 2026-06-02 | Gemini | UI Refactor | Extracted nested Composables from `MainActivity` into separate files and added `Previews`. | medium |

## How to use this log
1. **Date:** Today's date (or the date of the task).
2. **Agent:** Gemini, Copilot, Cursor, etc.
3. **Task:** Component name or feature (e.g., `Panic Button Logic`).
4. **Description:** What did the AI do? (e.g., "Refactored the filtering algorithm to use Kotlin Flows").
5. **Impact:** Low (naming suggestions), Medium (bug fixes), High (architectural design/whole components).
