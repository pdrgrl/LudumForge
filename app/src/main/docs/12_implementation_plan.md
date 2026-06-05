# 12. Implementation Plan: LudumForge

Updated roadmap based on current code state and proposal requirements.

## ✅ Completed (Fases 1, 2 & 3)
- **Auth & Collaboration:** Firebase Login/Register, Deep-link invites, and `memberIds` sync.
- **The Arcade:** Real-time itch.io scraping using Jsoup with 15-minute cache.
- **Workspace:** Real-time task streaming, edit/delete sheets, and status updates (including `REVIEW`).
- **Subscription:** Tiered system (2 Jams for Free, Unlimited for Premium) based on `activeJam` count.
- **AI Core:** Roadmap generation with selectable review step.
- **Panic Mode (M1):** AI-powered MVP trimming implemented via Terminal command sequence. [DONE]

## 🧠 Phase 2: Final Polish & "Must-Haves" (Current Focus)

### 2.2 User Roles & Badges 🔴
- Add `FilterChip` role picker to `RegisterScreen`.
- Update user avatars in the War Room to show role badges.

### 2.3 Task Creation UI 🟡
- Add Category selection and Minutes input to the "Add Task" sheet.

### 2.4 Offline & Persistence 🟡
- Complete Room wiring for `Tasks` and `Projects` (currently only `ActionLog` is stored).
- Improve the sync loop for manual retries when offline.

## 🚀 Future Polishing (Should-Have)
- **Task Detail Screen:** Move beyond bottom sheets for a dedicated task view.
- **Analytics:** Post-jam productivity stats for Premium users.
