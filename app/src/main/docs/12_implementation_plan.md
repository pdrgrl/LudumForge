# 12. Implementation Plan: LudumForge

Updated roadmap based on current code state and stability improvements.

## ✅ Completed (Fases 1, 2 & 3)
- **Auth & Collaboration:** Firebase Login/Register, Deep-link invites, and `memberIds` sync.
- **The Arcade:** Real-time itch.io scraping using Jsoup with 15-minute cache.
- **Workspace:** Real-time task streaming, edit/delete sheets, and status updates.
- **Subscription:** Tiered system based on `activeJam` count.
- **AI Core:** Roadmap generation and Panic Mode (M1).
- **User Roles (M2) & Task UI (M3):** Fully integrated into registration and workspace.
- **Stability Polish:** Fixed memory leaks (Firestore listeners) and Logout crashes. [DONE]
- **Timer Polish:** Dynamic formatting (Days/Hours/Minutes) for better readability. [DONE]

## 🧠 Phase 2: Final Polish & Survival (Current Focus)

### 2.4 Offline & Persistence 🟡
- Complete Room wiring for `Tasks` and `Projects` (currently only `ActionLog` is stored).
- Improve the sync loop for manual retries when offline.

## 🚀 Future Polishing (Should-Have)
- **Task Detail Screen:** Move beyond bottom sheets for a dedicated task view.
- **Analytics:** Post-jam productivity stats for Premium users.
- **Unit Testing:** Comprehensive coverage for the AI-driven Panic logic.
