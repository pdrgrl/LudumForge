# Jetpack Compose & UI Prompts

### 🎨 Create a New Composable
```text
I need to build the {Screen/Component Name}.
- Use Material 3 components.
- Follow our neon-mechanical theme (Dark mode).
- Keep the Composable stateless.
- Use a separate Preview for the Success, Loading, and Error states.
Design details: {add details from docs/04_screens_and_ui.md}
```

### 🔄 Implement State Hoisting
```text
Refactor the following Composable to use state hoisting.
Extract the state to a {ScreenName}UiState and move the events (onEventName) to the caller.
Ensure it works well with a ViewModel.
```

### ✨ Add Animation
```text
I want to add a transition animation to {UIElement}.
When the state changes from {StateA} to {StateB}, it should {fade/slide/scale}.
Use `AnimatedVisibility` or `animate*AsState` APIs.
```
