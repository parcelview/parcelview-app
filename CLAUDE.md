# ParcelView — Architecture Guide for Claude

This file is the primary reference for all architecture decisions in this project. Read it before writing any code.

---

## Project Overview

ParcelView is a Kotlin Multiplatform Mobile (KMM) app targeting **Android** and **iOS** (primary), with **Web as best-effort**. The UI is built entirely with Compose Multiplatform.

---

## Tech Stack

| Concern | Library |
|---|---|
| UI | Compose Multiplatform |
| Navigation | `androidx.navigation3` (already integrated) |
| DI | Koin (`koin-core`, `koin-compose`, `koin-compose-viewmodel`) |
| State management | MVI — ViewModel + StateFlow |
| Async | Kotlin Coroutines |

---

## Gradle Module Structure

```
parcelview-mobile/
├── composeApp/                     # App shell: entry points, root DI init, nav host
│   └── src/
│       ├── commonMain/
│       ├── androidMain/            # MainActivity
│       └── iosMain/                # MainViewController
├── core/
│   ├── core-ui/                    # Shared theme, design system components
│   └── core-navigation/            # NavKey base, shared nav utilities
├── feature/
│   ├── parcels/
│   │   ├── public/                 # :feature:parcels:public
│   │   │   └── commonMain/ → interfaces, NavKey (ParcelsList, ParcelDetail), data classes
│   │   └── impl/                   # :feature:parcels:impl
│   │       └── commonMain/ → ViewModel, screens, repository impl, Koin module
│   ├── scanner/
│   │   ├── public/                 # :feature:scanner:public
│   │   └── impl/                   # :feature:scanner:impl
│   └── settings/
│       ├── public/                 # :feature:settings:public
│       └── impl/                   # :feature:settings:impl
└── gradle/
    └── libs.versions.toml
```

### Public vs Impl split

Every feature has two Gradle modules:

- **`public`** — only what other modules need: `NavKey` types, data classes, repository interfaces. Keep this minimal.
- **`impl`** — everything else: ViewModel, screen Composables, repository implementation, Koin module.

### Dependency direction

```
composeApp → feature:*:impl → feature:*:public → core:*
                                                ↑
                            feature:*:impl can depend on other feature:*:public (never impl)
```

No module may create a circular dependency. `core` modules have no knowledge of `feature` modules.

---

## MVI Pattern (ViewModel)

Every feature screen has exactly one ViewModel with **three sealed classes**:

```kotlin
class ParcelsViewModel(private val repo: ParcelsRepository) : ViewModel() {

    // 1. UiState — what the UI renders
    sealed class UiState {
        object Loading : UiState()
        data class Success(val parcels: List<Parcel>) : UiState()
        data class Error(val message: String) : UiState()
    }

    // 2. UiEvent — inputs from the UI (clicks, text changes, etc.)
    sealed class UiEvent {
        data class ParcelClicked(val trackingId: String) : UiEvent()
        object RefreshRequested : UiEvent()
    }

    // 3. UiAction — one-time side effects (navigate, toast, dialog)
    //    Always has a NoAction default; UI resets to NoAction after consuming
    sealed class UiAction {
        object NoAction : UiAction()
        data class NavigateToDetail(val trackingId: String) : UiAction()
        data class ShowToast(val message: String) : UiAction()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _uiAction = MutableStateFlow<UiAction>(UiAction.NoAction)
    val uiAction: StateFlow<UiAction> = _uiAction.asStateFlow()

    fun onEvent(event: UiEvent) {
        when (event) {
            is UiEvent.ParcelClicked -> _uiAction.value = UiAction.NavigateToDetail(event.trackingId)
            is UiEvent.RefreshRequested -> loadParcels()
        }
    }

    fun onActionConsumed() { _uiAction.value = UiAction.NoAction }
}
```

### UiAction consumption in Composables

```kotlin
val action by viewModel.uiAction.collectAsState()
LaunchedEffect(action) {
    when (val a = action) {
        is UiAction.NavigateToDetail -> {
            navController.push(ParcelDetail(a.trackingId))
            viewModel.onActionConsumed()
        }
        is UiAction.ShowToast -> {
            /* show toast */
            viewModel.onActionConsumed()
        }
        is UiAction.NoAction -> Unit
    }
}
```

**Rules:**
- `UiState` drives all rendering — use `when()` exhaustively in the Composable.
- `UiEvent` is the only way the UI communicates with the ViewModel.
- `UiAction` is for **one-time** side effects only. Always call `onActionConsumed()` immediately after handling.
- Never expose mutable state from a ViewModel (`_uiState` stays private).

---

## Koin DI

### Dependencies (in `libs.versions.toml`)

```toml
[versions]
koin = "4.x.x"   # check latest stable

[libraries]
koin-core = { module = "io.insert-koin:koin-core", version.ref = "koin" }
koin-compose = { module = "io.insert-koin:koin-compose", version.ref = "koin" }
koin-compose-viewmodel = { module = "io.insert-koin:koin-compose-viewmodel", version.ref = "koin" }
```

### Each `impl` module exports a Koin module

```kotlin
// feature/parcels/impl/src/commonMain/kotlin/.../ParcelsModule.kt
val parcelsModule = module {
    factory { ParcelsRepository(get()) }
    viewModel { ParcelsViewModel(get()) }
}
```

### `composeApp` collects and starts all modules

```kotlin
// composeApp/src/commonMain/kotlin/.../AppModule.kt
val appModules = listOf(parcelsModule, scannerModule, settingsModule)
```

```kotlin
// androidMain — Application.onCreate() or MainActivity
startKoin { modules(appModules) }

// iosMain — called from MainViewController before presenting root view
startKoin { modules(appModules) }
```

### Injecting into Composables

```kotlin
@Composable
fun ParcelsScreen() {
    val viewModel: ParcelsViewModel = koinViewModel()
    // ...
}
```

---

## Navigation

Navigation uses `androidx.navigation3`. Each feature's **public** module defines its `NavKey` types:

```kotlin
// feature/parcels/public/src/commonMain/kotlin/.../ParcelsNavKeys.kt
@Serializable object ParcelsList : NavKey
@Serializable data class ParcelDetail(val trackingId: String) : NavKey
```

The nav graph is assembled in `composeApp` and delegates rendering to the feature's screen Composables (from `impl`).

---

## Naming Conventions

| Thing | Convention |
|---|---|
| ViewModel file | `<Feature>ViewModel.kt` |
| Screen Composable | `<Feature>Screen.kt` |
| Repository interface | `<Feature>Repository.kt` (in `public`) |
| Repository impl | `<Feature>RepositoryImpl.kt` (in `impl`) |
| Koin module | `val <feature>Module = module { }` in `<Feature>Module.kt` |
| NavKey file | `<Feature>NavKeys.kt` (in `public`) |
| UiState / UiEvent / UiAction | Nested inside the ViewModel class |

---

## What Lives Where

| Code | Module |
|---|---|
| Theme, typography, colors | `core:core-ui` |
| NavKey base type, nav utilities | `core:core-navigation` |
| Repository interface, NavKeys, shared data classes | `feature:<name>:public` |
| ViewModel, screens, repo impl, Koin module | `feature:<name>:impl` |
| Entry points, root DI init, nav host | `composeApp` |

---

## Current State (as of initial scaffold)

The project currently has a **single `:composeApp` module** with no DI, no ViewModels, and state managed locally in Composables. The feature module split and Koin integration are planned but not yet implemented. Migration happens incrementally — one feature at a time — following the patterns above.

When adding a new feature, always create the `public` + `impl` modules from the start, even if the feature is simple.
