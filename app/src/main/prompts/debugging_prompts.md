# Debugging & Error Handling Prompts

### 🐞 Fix a Logic Bug
```text
The {FeatureName} is not working as expected. 
Expected behavior: {behavior}
Actual behavior: {actual}
Here is the code for the ViewModel and UseCase. Please identify the logic error and provide a fix.
```

### 💥 Fix a Crash
```text
The app is crashing with {StackTrace}.
It seems to happen in {ClassName}. 
Please analyze the cause and suggest a safe way to handle this (e.g., Null safety, Coroutine exception handling).
```

### 🎨 Fix a UI Glitch
```text
The {UIComponent} is not rendering correctly on {small screens / when keyboard is open}.
Please adjust the Modifier or the Layout structure to make it responsive.
```
