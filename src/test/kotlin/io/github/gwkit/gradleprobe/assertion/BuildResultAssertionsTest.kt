package io.github.gwkit.gradleprobe.assertion

import io.kotest.assertions.MultiAssertionError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.gradle.testkit.runner.BuildResult
import org.junit.jupiter.api.Test

class BuildResultAssertionsTest {

    @Test
    fun `assertOutputContainsStrings should pass when output contains all expected strings`() {
        // GIVEN
        val buildResult = mockk<BuildResult> {
            every { output } returns "BUILD SUCCESSFUL\nTask :test completed"
        }

        // WHEN
        val result = buildResult.assertOutputContainsStrings("BUILD SUCCESSFUL", "Task :test")

        // THEN
        result shouldBe buildResult
    }

    @Test
    fun `assertOutputContainsStrings should pass with single expected string`() {
        // GIVEN
        val buildResult = mockk<BuildResult> {
            every { output } returns "Hello World Output"
        }

        // WHEN
        val result = buildResult.assertOutputContainsStrings("Hello World")

        // THEN
        result shouldBe buildResult
    }

    @Test
    fun `assertOutputContainsStrings should fail when expected string is missing`() {
        // GIVEN
        val buildResult = mockk<BuildResult> {
            every { output } returns "BUILD FAILED"
        }

        // WHEN / THEN
        shouldThrow<AssertionError> {
            buildResult.assertOutputContainsStrings("BUILD SUCCESSFUL")
        }
    }

    @Test
    fun `assertOutputContainsStrings should report all missing strings using soft assertions`() {
        // GIVEN
        val buildResult = mockk<BuildResult> {
            every { output } returns "Some output"
        }

        // WHEN / THEN
        val exception = shouldThrow<MultiAssertionError> {
            buildResult.assertOutputContainsStrings("missing1", "missing2", "missing3")
        }
        exception.errors.size shouldBeGreaterThanOrEqual 3
    }

    @Test
    fun `assertOutputContainsStrings should return BuildResult for chaining`() {
        // GIVEN
        val buildResult = mockk<BuildResult> {
            every { output } returns "first second third"
        }

        // WHEN
        val result = buildResult
            .assertOutputContainsStrings("first")
            .assertOutputContainsStrings("second")
            .assertOutputContainsStrings("third")

        // THEN
        result shouldBe buildResult
    }

    @Test
    fun `assertOutputContainsStrings should handle empty vararg`() {
        // GIVEN
        val buildResult = mockk<BuildResult> {
            every { output } returns "any output"
        }

        // WHEN
        val result = buildResult.assertOutputContainsStrings()

        // THEN
        result shouldBe buildResult
    }
}
