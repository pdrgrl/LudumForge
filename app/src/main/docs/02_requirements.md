# 02. Requirements: LudumForge

## ✅ Functional Requirements (FR)

### FR1: Project Management (Jams)
- **FR1.1:** Create a new project with title, theme, team size, and deadline.
- **FR1.2:** View a list of active and completed projects.
- **FR1.3:** Delete or archive projects.

### FR2: AI Roadmap Generation (The Architect)
- **FR2.1:** Input game idea text and parameters.
- **FR2.2:** Generate a structured task list (JSON-based) via LLM.
- **FR2.3:** Categorize tasks into Code, Art, and Audio.
- **FR2.4:** Assign time estimates to generated tasks.

### FR3: Task Management
- **FR3.1:** Add, edit, and remove individual tasks.
- **FR3.2:** Mark tasks as Pending, In-Progress, or Done.
- **FR3.3:** Assign tasks to specific team members.

### FR4: Survival Features
- **FR4.1: Panic Button:** Automatically trim non-essential tasks to secure an MVP.
- **FR4.2:** Real-time countdown timer for the project deadline.
- **FR4.3:** Progress bars for total project and individual categories.

### FR5: Collaboration & Social
- **FR5.1:** Real-time synchronization of task status via Firebase.
- **FR5.2: The Arcade:** Search for active Game Jams using itch.io API or similar feed.
- **FR5.3:** Team workspace (War Room) with Kanban-style visualization.

### FR6: Offline Mode
- **FR6.1:** Access local task list and timer without internet.
- **FR6.2:** Simple notepad for design notes in cache.
- **FR6.3:** Background synchronization when connection is restored.

---

## ⚙️ Non-Functional Requirements (NFR)

### NFR1: Performance
- AI generation should complete within 10-15 seconds.
- Local data access (Room) should be near-instant.

### NFR2: Availability & Reliability
- Offline mode must ensure the timer never stops.
- Data consistency between local cache and Firebase.

### NFR3: Security
- User authentication via Firebase Auth.
- Role-based permissions within teams.

### NFR4: Usability
- High-contrast UI for readability during long coding sessions.
- Intuitive navigation between public feed and private workspace.
