package io.github.gwkit.gradleprobe.gradlerunner

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
