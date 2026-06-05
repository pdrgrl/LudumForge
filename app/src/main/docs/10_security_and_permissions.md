# 10. Security and Permissions: LudumForge

## 🔐 Authentication & Authorization

### 1. User Authentication
- Managed via **Firebase Auth**.
- Supports: Email/Password, Google Sign-in.
- User data is tied to a unique `uid`.

### 2. Firestore Security Rules
Rules are configured to ensure team privacy:
- Users can only **read** projects they are members of.
- Users can only **write/edit** tasks within their own projects.
- Public jams in "The Arcade" are **read-only** for all authenticated users.

## 📱 Android Permissions

### 1. Mandatory Permissions
- `INTERNET`: For Firebase, AI API, and Jam feeds.
- `ACCESS_NETWORK_STATE`: To detect offline status and toggle "Modo Terminal" features.

### 2. Optional Permissions
- `POST_NOTIFICATIONS`: To alert the team when a deadline is approaching or when the "Panic Button" is pressed by a team member.

## 🛡️ Data Privacy
- Personal notes in "Modo Terminal" are stored locally and only synced to the user's private Firebase node.
- No sensitive PII (Personally Identifiable Information) beyond email and username is stored.
- AI prompts are sanitized to remove any personal information before being sent to the LLM API.
