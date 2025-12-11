# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.0.2] - 2025-12-11

### Added

- TestKit properties support via `testKitPropertyName` parameter in `buildGradleRunner` function
- Integration with [CoverJet](https://github.com/gw-kit/cover-jet-plugin) plugin through `io.github.gwkit.coverjet.test-kit` system property
- Automatic injection of TestKit properties into test project's `gradle.properties`

## [0.0.1] - 2025-12-06

### Added

- Initial release of gradle-probe library for functional testing of Gradle plugins

#### JUnit 5 Extension
- `GradlePluginTestExtension` - Test instance post-processor that automates test setup

#### Annotations
- `@GradlePluginTest` - Class-level annotation to configure test project resource path and DSL type (Groovy/Kotlin)
- `@RootProjectDir` - Injects root project directory as `File`
- `@GradleRunnerInstance` - Injects pre-configured `GradleRunner` instance
- `@ProjectFile` - Injects project files as `File`, `String`, or `RestorableFile`

#### Test Utilities
- `RestorableFile` - Wrapper allowing file modification with rollback capability
- `GradleRunner.runTask()` - Extension function to run Gradle tasks
- `GradleRunner.runTaskAndFail()` - Extension function for expected failures
- `BuildResult.assertOutputContainsStrings()` - Assertion utility for build output validation

#### Resource Management
- Automatic test project copying from classpath resources to temp directories
- Build file filtering based on DSL preference (`.gradle` vs `.gradle.kts`)
- Cross-platform path handling