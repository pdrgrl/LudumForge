# 07. Data Model: LudumForge

Initial data structure for LudumForge, designed for both Room (Local) and Firestore (Remote).

## 📊 Core Entities

### 1. Project (Jam)
| Field | Type | Description |
| :--- | :--- | :--- |
| id | String | Unique Identifier (UUID / Firestore ID) |
| name | String | Project Title |
| theme | String | Jam Theme |
| startDate | Long | Timestamp (ms) |
| endDate | Long | Timestamp (ms) |
| teamSize | Int | Number of members |
| status | Enum | PLANNING, ACTIVE, COMPLETED |

### 2. Task
| Field | Type | Description |
| :--- | :--- | :--- |
| id | String | Unique Identifier |
| projectId | String | Foreign Key to Project |
| title | String | Task Name |
| category | Enum | CODE, ART, AUDIO, DESIGN |
| assignedTo | String | User ID (nullable) |
| estimatedMin | Int | Time estimate in minutes |
| priority | Int | 1 (Low) to 3 (Essential) |
| status | Enum | PENDING, IN_PROGRESS, DONE |

### 3. User
| Field | Type | Description |
| :--- | :--- | :--- |
| id | String | Unique Identifier (Firebase UID) |
| username | String | Display Name |
| email | String | User Email |
| role | Enum | PROGRAMMER, ARTIST, MUSICIAN, GENERALIST |
| subscription | Enum | FREE, PREMIUM |

### 4. Team
| Field | Type | Description |
| :--- | :--- | :--- |
| id | String | Unique Identifier |
| projectId | String | Linked Project |
| membersList | List<String>| List of User IDs |

---

## 🔗 Relationships
- **Project 1 : N Task:** A project contains multiple tasks.
- **Project 1 : 1 Team:** Each project is assigned to one team (which can be a solo team).
- **User N : M Team:** A user can be part of multiple teams/projects over time.
- **User 1 : N Task:** A user can be assigned multiple tasks within a project.
