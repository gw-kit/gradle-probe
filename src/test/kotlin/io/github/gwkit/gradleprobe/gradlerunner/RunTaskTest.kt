package io.github.gwkit.gradleprobe.gradlerunner

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class RunTaskTest {

    @TempDir
    lateinit var tempDir: File

    @Test
    fun `runTask should work with multiple tasks`() {
        // GIVEN
        val projectRoot = createMinimalProject()
        val runner = buildGradleRunner(projectRoot)

        // WHEN
        val result = runner.runTask("help", "tasks")

        // THEN
        result.task(":help")?.outcome shouldBe TaskOutcome.SUCCESS
        result.task(":tasks")?.outcome shouldBe TaskOutcome.SUCCESS
        result.output shouldContain "BUILD SUCCESSFUL"
    }

    @Test
    fun `runTaskAndFail should work with multiple tasks`() {
        // GIVEN
        val projectRoot = createFailingProject()
        val runner = buildGradleRunner(projectRoot)

        // WHEN
        val result = runner.runTaskAndFail("help", "failingTask")

        // THEN
        result.task(":help")?.outcome shouldBe TaskOutcome.SUCCESS
        result.task(":failingTask")?.outcome shouldBe TaskOutcome.FAILED
    }

    private fun createMinimalProject(): File {
        val projectRoot = tempDir.resolve("project").apply { mkdirs() }
        projectRoot.resolve("settings.gradle.kts").writeText("""rootProject.name = "test-project"""")
        projectRoot.resolve("build.gradle.kts").writeText("""plugins { id("base") }""")
        return projectRoot
    }

    private fun createFailingProject(): File {
        val projectRoot = tempDir.resolve("failing-project").apply { mkdirs() }
        projectRoot.resolve("settings.gradle.kts").writeText("""rootProject.name = "failing-project"""")
        projectRoot.resolve("build.gradle.kts").writeText(
            """
            plugins { id("base") }

            tasks.register("failingTask") {
                doLast {
                    throw GradleException("Task failed intentionally")
                }
            }
            """.trimIndent()
        )
        return projectRoot
    }
}
