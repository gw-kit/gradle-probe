# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
./gradlew build          # Build and run tests
./gradlew test           # Run tests only
./gradlew compileKotlin  # Compile only
```

Tests run in parallel by default with JUnit Jupiter.

## Project Overview

**gradle-probe** is a library that simplifies writing functional tests for Gradle plugins. It provides a JUnit 5 extension with annotation-based property injection.

## Architecture

All code resides in `io.github.gwkit.gradleprobe` package:

- **GradlePluginTestExtension** - JUnit 5 `TestInstancePostProcessor` that orchestrates test setup:
  - Copies test projects from resources to temp directories
  - Processes build files (keeps `.gradle.kts` or `.gradle` based on `kts` flag)
  - Injects properties via reflection into `lateinit` fields

- **Annotations** (in `GradlePluginTest.kt`):
  - `@GradlePluginTest` - Class-level, specifies resource project and DSL type
  - `@RootProjectDir` - Injects root project `File`
  - `@GradleRunnerInstance` - Injects configured `GradleRunner`
  - `@ProjectFile` - Injects project file as `File`, `String`, or `RestorableFile`

- **GradleRunnerUtils** - Extension functions for `GradleRunner` (`runTask`, `runTaskAndFail`) and `BuildResult` assertions

- **ResourceUtils** - Functions for copying resources (`copyDirFromResources`, `getResourceFile`)

- **RestorableFile** - Wrapper allowing file modification with rollback capability

## Key Dependencies

- `gradleTestKit()` is `compileOnly` - consumers must provide it
- `kotlin-reflect` for annotation-based property injection
- `kotest-assertions-core` for assertion utilities
- Uses `-Xskip-metadata-version-check` compiler flag for Gradle API compatibility