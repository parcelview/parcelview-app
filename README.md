# ParcelView

A Kotlin Multiplatform Mobile (KMM) package-tracking app targeting Android, iOS, and Web (best-effort). The UI is built with Compose Multiplatform.

* [/composeApp](./composeApp/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - [commonMain](./composeApp/src/commonMain/kotlin) is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    the [iosMain](./composeApp/src/iosMain/kotlin) folder would be the right place for such calls.
    Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./composeApp/src/jvmMain/kotlin)
    folder is the appropriate location.

* [/iosApp](./iosApp/iosApp) contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

### Build and Run Android Application

To build and run the development version of the Android app, use the run configuration from the run widget
in your IDE’s toolbar or build it directly from the terminal:
- on macOS/Linux
  ```shell
  ./gradlew :composeApp:assembleDebug
  ```
- on Windows
  ```shell
  .\gradlew.bat :composeApp:assembleDebug
  ```

### Build and Run Web Application

To build and run the development version of the web app, use the run configuration from the run widget
in your IDE's toolbar or run it directly from the terminal:
- for the Wasm target (faster, modern browsers):
  - on macOS/Linux
    ```shell
    ./gradlew :composeApp:wasmJsBrowserDevelopmentRun
    ```
  - on Windows
    ```shell
    .\gradlew.bat :composeApp:wasmJsBrowserDevelopmentRun
    ```
- for the JS target (slower, supports older browsers):
  - on macOS/Linux
    ```shell
    ./gradlew :composeApp:jsBrowserDevelopmentRun
    ```
  - on Windows
    ```shell
    .\gradlew.bat :composeApp:jsBrowserDevelopmentRun
    ```

### Build and Run iOS Application

To build and run the development version of the iOS app, use the run configuration from the run widget
in your IDE’s toolbar or open the [/iosApp](./iosApp) directory in Xcode and run it from there.

---

## Architecture

> Full details for contributors and Claude: see [CLAUDE.md](./CLAUDE.md).

### Stack

| Concern | Choice |
|---|---|
| UI | Compose Multiplatform |
| Navigation | `androidx.navigation3` |
| DI | Koin (`koin-core`, `koin-compose`, `koin-compose-viewmodel`) |
| State | MVI — ViewModel + StateFlow |

### Module structure

```
parcelview-mobile/
├── composeApp/          # App shell: entry points, root DI init, nav host
├── core/
│   ├── core-ui/         # Shared theme and design system components
│   └── core-navigation/ # NavKey base, shared nav utilities
└── feature/
    ├── parcels/
    │   ├── public/      # NavKeys, data classes, repository interfaces
    │   └── impl/        # ViewModel, screens, repo impl, Koin module
    ├── scanner/
    │   ├── public/
    │   └── impl/
    └── settings/
        ├── public/
        └── impl/
```

**Dependency direction:** `composeApp → feature:*:impl → feature:*:public → core:*`

Feature `impl` modules may depend on other features' `public` modules, but **never** on another feature's `impl`.

### MVI pattern

Each screen has one ViewModel with three sealed classes: `UiState` (what to render), `UiEvent` (inputs from UI), and `UiAction` (one-time side effects like navigation or toasts). See [CLAUDE.md](./CLAUDE.md) for the full pattern with code examples.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html),
[Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/#compose-multiplatform),
[Kotlin/Wasm](https://kotl.in/wasm/)…

We would appreciate your feedback on Compose/Web and Kotlin/Wasm in the public Slack channel [#compose-web](https://slack-chats.kotlinlang.org/c/compose-web).
If you face any issues, please report them on [YouTrack](https://youtrack.jetbrains.com/newIssue?project=CMP).