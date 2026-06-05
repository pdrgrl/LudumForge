# 11. Testing Strategy: LudumForge

## 🧪 Testing Layers

### 1. Unit Tests (JUnit 5 + MockK)
- **Roadmap Logic:** Verify that the "Panic Button" correctly filters non-essential tasks based on priority.
- **Time Calculations:** Test the countdown timer and progress percentage logic.
- **Mappers:** Ensure Room entities convert correctly to Domain models.

### 2. UI Tests (Compose UI Test)
- **Navigation:** Verify that clicking bottom navigation icons leads to the correct screens.
- **State Rendering:** Test that the "Loading" state shows a spinner and "Error" shows the appropriate message.
- **Task Interaction:** Simulate clicking a task checkbox and verifying the progress bar updates.

### 3. Integration Tests
- **Database (Room):** Test DAOs (Data Access Objects) to ensure CRUD operations work as expected.
- **API (MockWebServer):** Simulate AI API responses to test the parsing and error-handling logic.

## 🚀 CI/CD Integration
- Run Unit tests on every Pull Request.
- Automated linting check using `detekt` or `ktlint`.
- Build a debug APK for manual QA on every merge to `develop`.

## 🧪 Manual QA Checklist
- [ ] Transition from Online to Offline (Airplane mode).
- [ ] Trigger "Panic Button" and verify task list reduction.
- [ ] Real-time sync between two different devices.
- [ ] AI roadmap generation with various prompts.
- [ ] "The Arcade" jam feed loading.
