package io.github.gwkit.gradleprobe.assertion

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.string.shouldContain
import org.gradle.testkit.runner.BuildResult

/**
 * Asserts that the build output contains all the specified strings.
 *
 * @param expectedString The strings that should be present in the output.
 * @return This build result for chaining.
 */
fun BuildResult.assertOutputContainsStrings(vararg expectedString: String): BuildResult {
    assertSoftly(output) {
        expectedString.forEach {
            shouldContain(it)
        }
    }
    return this
}