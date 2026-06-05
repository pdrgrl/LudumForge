# 02. Requirements: LudumForge

## ✅ Functional Requirements (FR)

### FR1: Project Management (Jams)
- **FR1.1:** Create/Rename/Delete Jams with theme and duration. [DONE]
- **FR1.2:** Select an "Active Jam" to focus work. [DONE]
- **FR1.3:** Collaborate via deep-link invites. [DONE]

### FR2: AI Roadmap Generation (The Architect)
- **FR2.1:** Input idea, team size, and duration. [DONE]
- **FR2.2:** Generate roadmap using `gemini-2.5-flash-lite`. [DONE]
- **FR2.3:** Select and push AI-generated tasks to the workspace. [DONE]

### FR3: Task Management
- **FR3.1:** Add, edit, and delete tasks. [DONE]
- **FR3.2:** Update status (TODO, IN_PROGRESS, REVIEW, DONE). [DONE]
- **FR3.3:** Assign tasks to team members with initials avatars. [DONE]
- **FR3.4:** Filter and search tasks in the War Room. [DONE]

### FR4: Survival Features
- **FR4.1: Panic Button:** (Planned) Trim non-essential tasks for MVP.
- **FR4.2:** Real-time countdown timer for the active Jam. [DONE]
- **FR4.3:** Action Log: System events tracking team activity. [DONE]

### FR5: Collaboration & Social
- **FR5.1:** Real-time sync via Firestore. [DONE]
- **FR5.2: The Arcade:** Fetch itch.io jams with cover images and 15min cache. [DONE]
- **FR5.3:** Subscription System: Free (2 jams) vs Premium (unlimited). [DONE]

### FR6: Offline Mode
- **FR6.1:** Access local Action Log without internet. [DONE]
- **FR6.2:** (Planned) Full task/project caching via Room.

---

## ⚙️ Non-Functional Requirements (NFR)
- **NFR1:** AI responses must be raw JSON (Prompt Engineering).
- **NFR2:** High-alert UI (Dynamic accents and specific fonts).
- **NFR3:** Seamless UX for Premium (Hidden API key fields).
