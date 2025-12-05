package io.github.gwkit.gradleprobe.gradlerunner

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner

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

@Suppress("SpreadOperator")
private fun GradleRunner.tasksWithDebugOption(vararg task: String): GradleRunner {
    val arguments: List<String> = task.toList() + "-si"
    return withArguments(arguments)
}
