# Contributing to LudumForge

Thank you for your interest in LudumForge! This project aims to be a robust tool for the Game Jam community.

## 🛠️ Development Workflow

1.  **Check the Docs:** Before starting, read the `docs/` to understand the vision and requirements.
2.  **Branching:** Create a feature branch from `main` (e.g., `feature/ai-roadmap-logic`).
3.  **ADRs:** If you propose an architectural change, create a new ADR in `docs/adr/` using the `template.md`.
4.  **Testing:** Ensure your changes are covered by unit tests (Logic) or UI tests (Compose).
5.  **Pull Request:** Use the provided PR template in `.github/pull_request_template.md`.

## 🎨 Coding Standards

- **Kotlin:** Use idiomatic Kotlin (Standard Library, Coroutines, Flow).
- **Compose:** Use small, reusable Composables. Keep state hoisting in mind.
- **Naming:** Follow standard Android/Kotlin naming conventions.
- **Comments:** Comment on *why* something is done, not *what* (the code should be self-explanatory).

## 🤖 AI Usage

If you use AI to generate code, please:
- Review and refactor the output to match our standards.
- Add an entry to `docs/14_ai_usage_log.md`.
