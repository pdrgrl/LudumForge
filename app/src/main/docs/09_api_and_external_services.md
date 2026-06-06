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
- **Improved Scraper:** Now parses exact timestamps and status tags from itch.io, supporting up to 30 concurrent jams with parallel metadata fetching.
- **Firestore Cache:** Results are synced to a shared `public_jams` Firestore collection. The app prioritizes this cache unless it is stale (>30 mins) or a manual refresh is requested.
- **Features:** Fetches Jam title, theme, cover image, status, and precise start/end dates.

## 🛠️ Internal Services

### 1. Room Persistence (SQLite)
- **Database:** `LudumForgeDatabase`.
- **Current State:** Stores `ActionLog` (System events: ⚡ for AI, 🏁 for Done, 🗑️ for Deleted).
- **Architecture:** Sync loop with manual retry logic for offline events.

### 2. Session & State
- **SessionManager:** Global StateFlow for `activeJamId` and subscription status.
- **Deep Linking:** Support for collaboration invites.
