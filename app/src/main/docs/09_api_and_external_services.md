# 09. API and External Services: LudumForge

## 📡 Remote Services

### 1. Firebase Suite
- **Auth:** Email + Google Sign-in (`AuthRepository`).
- **Firestore:** Live collections for `tasks`, `projects`, and `users`.
- **Sync:** Real-time listeners via `callbackFlow`.

### 2. AI Service (Gemini)
- **Model:** `gemini-2.5-flash-lite`.
- **Key Logic:** Users can save their own API key in Settings.
- **Premium UX:** Premium users get a streamlined experience (key input hidden).

### 3. Game Jam Feeds (Itch.io)
- **Implementation:** `ItchRepository` using **Jsoup** for live web scraping.
- **Caching:** 15-minute local cache to prevent rate-limiting and improve performance.
- **Features:** Fetches Jam title, theme, cover image, and external URL.

## 🛠️ Internal Services

### 1. Room Persistence (SQLite)
- **Database:** `LudumForgeDatabase`.
- **Current State:** Stores `ActionLog` (System events: ⚡ for AI, 🏁 for Done, 🗑️ for Deleted).
- **Architecture:** Sync loop with manual retry logic for offline events.

### 2. Session & State
- **SessionManager:** Global StateFlow for `activeJamId` and subscription status.
- **Deep Linking:** Support for collaboration invites.
