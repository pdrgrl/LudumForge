# 09. API and External Services: LudumForge

## 📡 Remote Services

### 1. Firebase Suite
- **Firebase Authentication:** Google Sign-In and Email/Password.
- **Cloud Firestore:** Real-time database for team collaboration.
- **Firebase Analytics:** Tracking feature usage (especially the Panic Button).

### 2. AI Service (LLM)
- **Engine:** Google Gemini (Flash) or Groq API.
- **Functionality:** Input a string ("A 2D platformer about a cat in space") and output a JSON roadmap.
- **Security:** API keys must be stored in `local.properties` and never committed to version control.

### 3. Game Jam Feeds
- **Source:** itch.io (via unofficial RSS/JSON feeds or Web Scraping if necessary).
- **Functionality:** Populating "The Arcade" with active events, dates, and themes.

## 🛠️ Internal Services

### 1. Room Persistence
- **Library:** `androidx.room`.
- **Functionality:** Local caching of tasks, users, and project metadata for offline access.

### 2. WorkManager
- **Functionality:** Handling background synchronization between Room and Firestore when the device regains internet connection.
- **Constraint:** Requires unmetered network or any available connection.
