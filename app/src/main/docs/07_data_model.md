# 07. Data Model: LudumForge

Current data structure as implemented in the Kotlin models, supporting both Room (Local) and Firestore (Remote).

## 📊 Core Entities

### 1. Project (Jam)
| Field | Type | Description |
| :--- | :--- | :--- |
| id | String | Unique Identifier (Firestore ID) |
| name | String | Project Title |
| theme | String | Jam Theme |
| startDate | Date | Java Date object |
| endDate | Date | Java Date object |
| teamSize | Int | Number of members |
| status | Enum | PLANNING, ACTIVE, SUBMITTED, COMPLETED, CANCELLED |
| creatorId | String | UID of the creator |
| memberIds | List<String> | Collaborators who joined via invite |
| coverImageUrl| String? | URL from itch.io feed (optional) |
| jamUrl | String? | External link to itch.io page |

### 2. Task
| Field | Type | Description |
| :--- | :--- | :--- |
| id | String | Unique Identifier |
| projectId | String | Foreign Key to Project |
| title | String | Task Name |
| category | Enum | ART, CODE, AUDIO, DESIGN, QA, OTHER |
| assignedTo | String | User ID (nullable) |
| estimatedMin | Int | Time estimate in minutes |
| status | Enum | TODO, IN_PROGRESS, REVIEW, DONE |

### 3. User
| Field | Type | Description |
| :--- | :--- | :--- |
| id | String | Firebase UID |
| username | String | Display Name |
| email | String | User Email |
| role | Enum | PROGRAMMER, ARTIST, MUSICIAN, GENERALIST |
| subscription | Enum | FREE, PREMIUM |

---

## 🔗 Implementation Notes
- **Naming Convention:** The code uses `Project` as the model name but refers to them as `Jams` in the UI and SessionManager.
- **Room Integration:** While `Project` and `Task` are annotated as `@Entity`, they are currently primarily managed via `TaskRepository` (Firestore). ActionLog is the only entity fully wired into `LudumForgeDatabase`.
