# Architecture & Logic Prompts

### 🏛️ Add a New Use Case
```text
I need to implement a new Use Case for {FeatureName}.
Following our Clean Architecture:
1. Create a Domain model if necessary.
2. Implement the Use Case in the domain layer.
3. Inject the necessary Repository.
4. Expose the logic to the ViewModel using a StateFlow.
Current context: {describe the logic}
```

### 🗄️ Database Changes (Room)
```text
I need to add a new Entity to our Room database called {EntityName}.
1. Define the @Entity class with its primary key.
2. Create a @Dao with CRUD operations (including Flow/Suspend functions).
3. Update the LudumForgeDatabase class.
4. Provide a mapper to convert this Entity to the Domain model.
```

### 💉 Dependency Injection (Hilt)
```text
I am adding {ClassName}. Please provide the Hilt @Module configuration to provide this dependency.
It requires {ConstructorParams}. Should it be a @Singleton or @ViewModelScoped?
```
