package io.github.gwkit.gradleprobe.junit.extension

import io.github.gwkit.gradleprobe.RestorableFile
import io.github.gwkit.gradleprobe.junit.GradlePluginTest
import io.github.gwkit.gradleprobe.junit.GradleRunnerInstance
import io.github.gwkit.gradleprobe.junit.ProjectFile
import io.github.gwkit.gradleprobe.junit.RootProjectDir
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.file.shouldExist
import io.kotest.matchers.file.shouldNotExist
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldEndWith
import io.mockk.mockk
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtensionContext
import java.io.File

class GradlePluginTestExtensionTest {

    private val extension = GradlePluginTestExtension()

    private fun mockExtensionContext(): ExtensionContext = mockk(relaxed = true)

    @Test
    fun `postProcessTestInstance should inject RootProjectDir property`() {
        // GIVEN
        val testInstance = TestClassWithRootProjectDir()

        // WHEN
        extension.postProcessTestInstance(testInstance, mockExtensionContext())

        // THEN
        testInstance.rootProjectDir.shouldExist()
        testInstance.rootProjectDir.resolve("settings.gradle.kts").shouldExist()
    }

    @Test
    fun `postProcessTestInstance should inject GradleRunner property`() {
        // GIVEN
        val testInstance = TestClassWithGradleRunner()

        // WHEN
        extension.postProcessTestInstance(testInstance, mockExtensionContext())

        // THEN
        testInstance.gradleRunner shouldNotBe null
    }

    @Test
    fun `postProcessTestInstance should inject ProjectFile as File`() {
        // GIVEN
        val testInstance = TestClassWithProjectFileAsFile()

        // WHEN
        extension.postProcessTestInstance(testInstance, mockExtensionContext())

        // THEN
        testInstance.buildFile.shouldExist()
        testInstance.buildFile.name shouldBe "build.gradle.kts"
    }

    @Test
    fun `postProcessTestInstance should inject ProjectFile as String`() {
        // GIVEN
        val testInstance = TestClassWithProjectFileAsString()

        // WHEN
        extension.postProcessTestInstance(testInstance, mockExtensionContext())

        // THEN
        testInstance.buildFilePath shouldEndWith "build.gradle.kts"
        testInstance.buildFilePath shouldContain "/"
    }

    @Test
    fun `postProcessTestInstance should inject ProjectFile as RestorableFile`() {
        // GIVEN
        val testInstance = TestClassWithRestorableFile()

        // WHEN
        extension.postProcessTestInstance(testInstance, mockExtensionContext())

        // THEN
        testInstance.restorableBuildFile.file.shouldExist()
        testInstance.restorableBuildFile.file.name shouldBe "build.gradle.kts"
    }

    @Test
    fun `postProcessTestInstance should delete groovy build files when kts is true`() {
        // GIVEN
        val testInstance = TestClassWithKtsTrue()

        // WHEN
        extension.postProcessTestInstance(testInstance, mockExtensionContext())

        // THEN
        testInstance.rootProjectDir.resolve("build.gradle.kts").shouldExist()
        testInstance.rootProjectDir.resolve("build.gradle").shouldNotExist()
    }

    @Test
    fun `postProcessTestInstance should delete kotlin build files when kts is false`() {
        // GIVEN
        val testInstance = TestClassWithKtsFalse()

        // WHEN
        extension.postProcessTestInstance(testInstance, mockExtensionContext())

        // THEN
        testInstance.rootProjectDir.resolve("build.gradle").shouldExist()
        testInstance.rootProjectDir.resolve("build.gradle.kts").shouldNotExist()
    }

    @Test
    fun `postProcessTestInstance should skip nested classes`() {
        // GIVEN
        val nestedInstance = NestedTestClassForSkipTest()

        // WHEN
        extension.postProcessTestInstance(nestedInstance, mockExtensionContext())

        // THEN - should not throw, property should remain uninitialized
        shouldThrow<UninitializedPropertyAccessException> {
            nestedInstance.rootProjectDir
        }
    }

    @Test
    fun `postProcessTestInstance should throw error when annotation is missing`() {
        // GIVEN
        val testInstance = TestClassWithoutAnnotation()

        // WHEN / THEN
        val exception = shouldThrow<IllegalStateException> {
            extension.postProcessTestInstance(testInstance, mockExtensionContext())
        }
        exception.message shouldContain "must be annotated with @GradlePluginTest"
    }

    @Test
    fun `postProcessTestInstance should throw error when project file does not exist`() {
        // GIVEN
        val testInstance = TestClassWithNonExistentFile()

        // WHEN / THEN
        val exception = shouldThrow<IllegalStateException> {
            extension.postProcessTestInstance(testInstance, mockExtensionContext())
        }
        exception.message shouldContain "not found"
        exception.message shouldContain "non-existent-file.txt"
    }

    @Test
    fun `RestorableFile should be able to restore original content`() {
        // GIVEN
        val testInstance = TestClassWithRestorableFile()
        extension.postProcessTestInstance(testInstance, mockExtensionContext())

        val originalContent = testInstance.restorableBuildFile.file.readText()
        val modifiedContent = "// modified content"
        testInstance.restorableBuildFile.file.writeText(modifiedContent)

        // WHEN
        testInstance.restorableBuildFile.restoreOriginContent()

        // THEN
        testInstance.restorableBuildFile.file.readText() shouldBe originalContent
    }

    @Test
    fun `postProcessTestInstance should inject multiple properties`() {
        // GIVEN
        val testInstance = TestClassWithMultipleProperties()

        // WHEN
        extension.postProcessTestInstance(testInstance, mockExtensionContext())

        // THEN
        testInstance.rootProjectDir.shouldExist()
        testInstance.gradleRunner shouldNotBe null
        testInstance.buildFile.shouldExist()
        testInstance.buildFilePath shouldEndWith "build.gradle.kts"
    }
}

// Test fixture classes

@GradlePluginTest("test-project")
private class TestClassWithRootProjectDir {
    @RootProjectDir
    lateinit var rootProjectDir: File
}

@GradlePluginTest("test-project")
private class TestClassWithGradleRunner {
    @GradleRunnerInstance
    lateinit var gradleRunner: GradleRunner
}

@GradlePluginTest("test-project")
private class TestClassWithProjectFileAsFile {
    @ProjectFile("build.gradle.kts")
    lateinit var buildFile: File
}

@GradlePluginTest("test-project")
private class TestClassWithProjectFileAsString {
    @ProjectFile("build.gradle.kts")
    lateinit var buildFilePath: String
}

@GradlePluginTest("test-project")
private class TestClassWithRestorableFile {
    @ProjectFile("build.gradle.kts")
    lateinit var restorableBuildFile: RestorableFile
}

@GradlePluginTest("test-project", kts = true)
private class TestClassWithKtsTrue {
    @RootProjectDir
    lateinit var rootProjectDir: File
}

@GradlePluginTest("test-project", kts = false)
private class TestClassWithKtsFalse {
    @RootProjectDir
    lateinit var rootProjectDir: File
}

@GradlePluginTest("test-project")
private class TestClassWithNonExistentFile {
    @ProjectFile("non-existent-file.txt")
    lateinit var missingFile: File
}

@GradlePluginTest("test-project")
private class TestClassWithMultipleProperties {
    @RootProjectDir
    lateinit var rootProjectDir: File

    @GradleRunnerInstance
    lateinit var gradleRunner: GradleRunner

    @ProjectFile("build.gradle.kts")
    lateinit var buildFile: File

    @ProjectFile("build.gradle.kts")
    lateinit var buildFilePath: String
}

private class TestClassWithoutAnnotation {
    @RootProjectDir
    lateinit var rootProjectDir: File
}

@Nested
private class NestedTestClassForSkipTest {
    @RootProjectDir
    lateinit var rootProjectDir: File
}
