The below file describes the codebase, including the high-level file structure, 
app architecture, and best-practices when adding code. Use it to understand how to 
add new code to the codebase.

# Project Overview

This is a Kotlin Multiplatform chat application that uses Jetpack Compose for the UI. The project follows a multi-module architecture with shared code in the commonMain module and platform-specific implementations for Android, iOS, and web, and desktop.

## Architecture

The app uses Circuit (a framework from Slack) as its main architecture. It follows a UDF (unidirectional data flow) architecture. Each screen in the app is represented by a Circuit screen. Each screen has a `Presenter` which does very lightweight transformations from the data layer to the UI layer.

The UI layer is written entirely in a declarative fashion (Jetpack compose). UI should generally be stateless.

The data layer is the source-of-truth for data in the app. It makes calls to low-level data sources like `DataStore` calls and network calls and transforms the data in preparation for the UI layer, including manipulating data sources appropriately based on UI events.

The app uses dependency injection via `Metro`.

## Structure

- `composeApp/src/commonMain`: common code targeting all platforms. This is the majority of code in the repo.
  - `data`: the data layer of the app
- `composeApp/src/androidMain`: android-specific code
- `composeApp/src/iosMain`: iOS-specific code
- `composeApp/src/desktopMain`: desktop-specific code
- `composeApp/src/wasmJsMain`: web-specific code

# General Philosophy, Principles, and Best Practices
- Maximize code reuse across platforms through Kotlin Multiplatform. Prefer generic code over platform-specific code.
- UI is built only using Jetpack Compose. **Do not** use other UI frameworks
- Use Kotlin DSL in Gradle and version catalogs in a .toml file for build configuration
- UI should have `@Preview` functions covering all major use-cases. Circuit presenters, repositories, and other business logic should have unit tests.
- annotate objects with `@Inject` to dependency inject them into the hierarchy. 
- anytime you change the `libs.toml` or `build.gradle.kts` file, run `./gradlew sync` after you're done.