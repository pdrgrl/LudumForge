# 09. API and External Services: LudumForge

## 📡 Remote Services

### 1. Firebase Suite
- **Firebase Authentication:** Google Sign-In and Email/Password implemented.
- **Cloud Firestore:** Real-time database used for `tasks` and `projects` collections.
- **Firebase Analytics:** Integrated via project setup.

### 2. AI Service (LLM)
- **Engine:** `gemini-2.5-flash-lite`.
- **Implementation:** `GenerativeModel` instance in `RoadmapGeneratorViewModel`.
- **Prompting:** Specialized system prompt to enforce raw JSON array output.
- **Configuration:** Users can provide their own API Key in Settings (Premium users have a streamlined flow).

### 3. Game Jam Feeds
- **Source:** itch.io (via `ItchRepository`).
- **Caching:** Fetches are cached for 15 minutes to reduce API overhead.
- **Features:** Supports opening external jam URLs and displaying cover images.

## 🛠️ Internal Services

### 1. Room Persistence
- **Database:** `LudumForgeDatabase`.
- **Current Entities:** `ActionLog` (Tracking system events like "Task created", "User joined").
- **Pending:** Wiring `Project` and `Task` entities for full offline support.

### 2. Session Management
- **SessionManager:** Singleton object managing the `activeJamId` and `isPremium` state app-wide.
