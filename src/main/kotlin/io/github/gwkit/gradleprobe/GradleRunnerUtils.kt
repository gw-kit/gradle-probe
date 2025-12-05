package io.github.gwkit.gradleprobe

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString

/**
 * The Gradle home directory (typically ~/.gradle).
 */
val GRADLE_HOME: String
    get() {
        val userHome: String = System.getProperty("user.home") ?: error("Cannot obtain 'user.home'.")
        return Path(userHome, ".gradle").absolutePathString()
    }

/**
 * Builds a [GradleRunner] configured for the given project root.
 *
 * @param projectRoot The root directory of the Gradle project.
 * @param testKitPropertyName The system property name containing the path to the test-kit properties file.
 * @return A configured [GradleRunner].
 */
fun buildGradleRunner(
    projectRoot: File,
    testKitPropertyName: String? = null,
): GradleRunner {
    return GradleRunner.create()
        .withPluginClasspath()
        .withProjectDir(projectRoot)
        .withTestKitDir(
            projectRoot.resolve(GRADLE_HOME).apply { mkdirs() }
        )
        .apply {
            if (testKitPropertyName != null) {
                val testKitPath: String? = System.getProperty(testKitPropertyName)
                if (testKitPath != null) {
                    File(projectDir, "gradle.properties").appendText(
                        File(testKitPath).readText()
                    )
                }
            }
        }
}

/**
 * Runs the specified Gradle tasks.
 *
 * @param task The tasks to run.
 * @return The build result.
 */
fun GradleRunner.runTask(vararg task: String): BuildResult {
    return tasksWithDebugOption(*task).build()
}

/**
 * Runs the specified Gradle tasks and expects the build to fail.
 *
 * @param task The tasks to run.
 * @return The build result.
 */
fun GradleRunner.runTaskAndFail(vararg task: String): BuildResult {
    return tasksWithDebugOption(*task).buildAndFail()
}

private fun GradleRunner.tasksWithDebugOption(vararg task: String): GradleRunner {
    val arguments: List<String> = mutableListOf(*task) + "-si"
    return withArguments(*arguments.toTypedArray())
}