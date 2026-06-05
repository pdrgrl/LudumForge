# AI Agents Policy & Usage 🤖

This project is designed to be highly compatible with AI development agents (like Gemini, Cursor, or GitHub Copilot).

## 🧭 Guidelines for Agents

1.  **Architecture First:** Always respect the defined Architecture Decision Records (ADRs) found in `docs/adr/`.
2.  **State Management:** Follow the Unidirectional Data Flow (UDF) pattern with ViewModels and StateFlow.
3.  **UI Consistency:** Use the Material 3 components defined in `ui/components/`.
4.  **Documentation:** Every new feature or significant architectural change must be accompanied by an update to the relevant `.md` file in `docs/`.

## 🛠️ Prompting the Agent

For specific tasks, please refer to the pre-defined prompts in the `prompts/` directory:
- `prompts/architecture_prompts.md`: For structural changes.
- `prompts/compose_prompts.md`: For UI development.
- `prompts/debugging_prompts.md`: For fixing issues.

## 📝 AI Usage Log

All significant AI-generated contributions (code refactoring, architecture design, logic generation) should be logged in `docs/14_ai_usage_log.md` to maintain transparency and track the evolution of the project.
