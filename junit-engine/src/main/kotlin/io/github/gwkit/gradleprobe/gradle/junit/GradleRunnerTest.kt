package io.github.gwkit.gradleprobe.gradle.junit

/**
 * Prepares a test class for running a Gradle plugin test.
 * The extension finds the test project in resources and copies it to a temporary directory.
 *
 * @param resourceProjectDir The relative path to the test project located in test resources.
 *
 * @see GradlePluginTestExtension
 */
@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class GradleRunnerTest(
    val resourceProjectDir: String,
)

/**
 * Specifies the Gradle version to use for the test.
 * The extension will use the specified Gradle version to run the test.
 *
 * @param version The Gradle version to use.
 *
 * @see GradlePluginTestExtension
 */
@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class GradleVersion(
    val version: String
)

/**
 * Injects a GradleRunner instance into test class property.
 * The property must be of type GradleRunner and must be lateinit.
 * ```
 * @GradlePluginTest("testProject")
 * class MyTest {
 *
 *   @GradleRunnerInstance
 *   lateinit var gradleRunner: GradleRunner
 *
 * }
 * ```
 */
@MustBeDocumented
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class GradleRunnerInstance

/**
 * Injects the root project directory into test class property.
 * The property must be of type File and must be lateinit.
 * ```
 * @GradlePluginTest("testProject")
 * class MyTest {
 *
 *   @RootProjectDir
 *   lateinit var rootProjectDir: File
 *
 * }
 * ```
 */
@MustBeDocumented
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class RootProjectDir

/**
 * Resolves and injects a project file into test class property. The file must exist.
 * The property must be of type [java.io.File] or [String] and must be lateinit.
 * ```
 * @GradlePluginTest("testProject")
 * class MyTest {
 *
 *  @ProjectFile("build.gradle.kts")
 *  lateinit var buildFile: File
 *
 *  @ProjectFile("build.gradle.kts")
 *  lateinit var buildFilePath: String
 *
 *  }
 *  ```
 * @param relativePath The relative path to the project file.
 * Useful when the test class has `TestInstance.Lifecycle.PER_CLASS` lifecycle.
 */
@MustBeDocumented
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class ProjectFile(
    val relativePath: String,
)