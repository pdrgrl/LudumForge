# LudumForge 🔨
**The Ultimate Game Jam Management Hub**

LudumForge is a premium Android application designed for game developers, artists, and musicians taking part in high-pressure game jams. It combines real-time collaboration, AI-powered project roadmapping, and an "offline-first" terminal to keep your project on track until the final second.

## ✨ Key Features
- **The Architect:** Generate a full project roadmap from your theme using Gemini AI.
- **The War Room:** Real-time task board for your team with edit/delete support.
- **The Arcade:** Live-scraped itch.io jam feed to stay ahead of the curve.
- **Offline Terminal:** A low-latency "hacking" interface to log work and dev notes without internet.
- **Panic Button:** AI-driven triage to drop non-essential tasks when the deadline looms (Premium).
- **Deep-Link Invites:** Quickly bring your team into a shared jam board.

## 🚀 Technical Stack
- **Languages:** Kotlin + Jetpack Compose
- **Backend:** Firebase (Firestore, Auth, Analytics)
- **Local Cache:** Room Database (Action Logs and persistence)
- **Networking:** Jsoup (Public feed scraping), Coroutines & Flow
- **AI:** Google Gemini API integration

## 📂 Documentation
Find more detailed project info in the `docs/` folder:
- [Project Status](docs/PROJECT_STATUS.md)
- [Implementation Plan](docs/IMPLEMENTATION_PLAN.md)
- [Feature Roadmap](docs/ROADMAP.md)

---
*Created for DAM 2026 — Pedro Grilo nº 51319*
