# 06. Architecture: LudumForge

The project follows **Clean Architecture** principles and **MVVM** (Model-View-ViewModel).

## 🏛️ Layers

### 1. UI Layer (Compose)
- **Composables:** Stateless UI components.
- **ViewModels:** Managing screen state using `StateFlow` and handling user intent.
- **Theme:** Material 3 implementation.

### 2. Domain Layer
- **Use Cases:** Encapsulating specific business logic (e.g., `GenerateRoadmapUseCase`, `ApplyPanicButtonUseCase`).
- **Models:** Pure Kotlin data classes (Domain Entities).

### 3. Data Layer
- **Repositories:** Abstracting data sources (Local vs Remote).
- **Local Source:** Room Database (Offline cache, Task storage).
- **Remote Source:** Firebase Firestore (Real-time sync) and AI API Client.
- **Mappers:** Converting DTOs to Domain Models.

## 🔄 Data Flow
- **Unidirectional Data Flow (UDF):** The UI sends events to the ViewModel, which updates the StateFlow, which is then collected by the UI.
- **Offline First:** Data is always saved to Room first, then synced to Firebase in the background.

## 🤖 AI Component
- **Engine:** REST API calls to a lightweight LLM (e.g., Gemini Flash or Groq).
- **Prompting:** Hidden system prompts enforce a JSON output format containing:
    - Task Title.
    - Category.
    - Estimated Minutes.
    - Priority Level.
