package io.github.gwkit.gradleprobe.gradle.junit
import io.github.gwkit.gradleprobe.gradle.resources.RestorableFile
import io.github.gwkit.gradleprobe.gradle.resources.copyDirFromResources
import io.github.gwkit.gradleprobe.gradle.resources.toUnixAbsolutePath
import io.github.gwkit.gradleprobe.gradle.runner.buildGradleRunner
import io.github.gwkit.gradleprobe.lib.reflect.injectProperty
import org.gradle.testkit.runner.GradleRunner
import org.gradle.util.GradleVersion as GradleToolVersion
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.TestInstancePostProcessor
import java.io.File
import java.nio.file.Files
import java.util.UUID
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

class GradlePluginTestExtension : TestInstancePostProcessor {

    override fun postProcessTestInstance(testInstance: Any, context: ExtensionContext) {
        val testClass: KClass<out Any> = testInstance::class

        if (testClass.hasAnnotation<Nested>()) {
            return
        }

        val gradleRunnerTest: GradleRunnerTest = testClass.findAnnotation()
            ?: error("Test class ${testInstance::class.qualifiedName} must be annotated with @GradleRunnerTest")

        val gradleVersion: GradleVersion? = testClass.findAnnotation()

        val rootProjectDir: File = copyResourceProjectToTempDir(
            testClass,
            gradleRunnerTest
        )

        with(testInstance) {
            injectProperty<RootProjectDir, File>(rootProjectDir)
            injectProperty<GradleRunnerInstance, GradleRunner>(
                buildGradleRunner(
                    rootProjectDir,
                    gradleVersion?.version ?: GradleToolVersion.current().version
                )
            )
            injectProperty<ProjectFile, RestorableFile> {
                val tempTestFile: File = Files.createTempDirectory(TEST_DIR_PREFIX).toFile()
                val fileToBeRestored: File = resolveExistingFile(rootProjectDir, relativePath)
                val originCopy: File = tempTestFile.resolve(UUID.randomUUID().toString())
                fileToBeRestored.copyTo(originCopy)
                RestorableFile(originFileCopy = originCopy, file = fileToBeRestored)
            }
            injectProperty<ProjectFile, File> {
                resolveExistingFile(rootProjectDir, relativePath)
            }
            injectProperty<ProjectFile, String> {
                resolveExistingFile(rootProjectDir, relativePath).toUnixAbsolutePath()
            }
        }
    }

    private fun copyResourceProjectToTempDir(
        testClass: KClass<out Any>,
        gradleRunnerTest: GradleRunnerTest,
    ): File {
        val tempTestFile: File = Files.createTempDirectory(TEST_DIR_PREFIX).toFile()

        return tempTestFile.copyDirFromResources(
            testClass,
            gradleRunnerTest.resourceProjectDir
        )
    }

    private fun resolveExistingFile(rootProjectDir: File, relativePath: String): File {
        return rootProjectDir.resolve(relativePath)
            .takeIf { it.exists() }
            ?: error("File $relativePath not found")
    }

    companion object {
        private const val TEST_DIR_PREFIX = "gradle-runner-test"
    }

}