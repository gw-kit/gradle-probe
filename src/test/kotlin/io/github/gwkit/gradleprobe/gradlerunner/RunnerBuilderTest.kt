package io.github.gwkit.gradleprobe.gradlerunner

import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.file.shouldBeADirectory
import io.kotest.matchers.file.shouldExist
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldEndWith
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class RunnerBuilderTest {

    @TempDir
    lateinit var tempDir: File

    @Test
    fun `GRADLE_HOME should point to user home gradle directory`() {
        // GIVEN
        val userHome = System.getProperty("user.home")

        // WHEN
        val gradleHome = GRADLE_HOME

        // THEN
        gradleHome shouldContain userHome
        gradleHome shouldEndWith ".gradle"
    }

    @Test
    fun `buildGradleRunner should create runner with basic setup`() {
        // GIVEN
        val projectRoot = tempDir.resolve("project").apply { mkdirs() }

        // WHEN
        val runner = buildGradleRunner(projectRoot)

        // THEN
        runner.projectDir shouldBe projectRoot
        runner.pluginClasspath.shouldNotBeEmpty()
    }

    @Test
    fun `buildGradleRunner should configure test kit directory`() {
        // GIVEN
        val projectRoot = tempDir.resolve("project").apply { mkdirs() }

        // WHEN
        buildGradleRunner(projectRoot)

        // THEN
        projectRoot.resolve(GRADLE_HOME).shouldBeADirectory()
    }

    @Test
    fun `buildGradleRunner should append testkit properties when system property is set`() {
        // GIVEN
        val projectRoot = tempDir.resolve("project").apply { mkdirs() }
        val expectedCopiedContent = "test.property=test-value"
        val testKitPropertiesFile = tempDir.resolve("testkit.properties").apply {
            writeText(expectedCopiedContent)
        }
        val propertyName = "test.kit.property.${System.currentTimeMillis()}"

        System.setProperty(propertyName, testKitPropertiesFile.absolutePath)

        // WHEN
        buildGradleRunner(projectRoot, propertyName)

        // THEN
        val gradleProperties = projectRoot.resolve("gradle.properties")
        gradleProperties.shouldExist()
        gradleProperties.readText() shouldContain expectedCopiedContent
    }

    @Test
    fun `buildGradleRunner should not modify gradle properties when testkit property is null`() {
        // GIVEN
        val projectRoot = tempDir.resolve("project").apply { mkdirs() }

        // WHEN
        buildGradleRunner(projectRoot, null)

        // THEN
        val gradleProperties = projectRoot.resolve("gradle.properties")
        gradleProperties.exists() shouldBe false
    }

    @Test
    fun `buildGradleRunner should not modify gradle properties when system property does not exist`() {
        // GIVEN
        val projectRoot = tempDir.resolve("project").apply { mkdirs() }
        val nonExistentPropertyName = "non.existent.property.${System.currentTimeMillis()}"

        // WHEN
        buildGradleRunner(projectRoot, nonExistentPropertyName)

        // THEN
        val gradleProperties = projectRoot.resolve("gradle.properties")
        gradleProperties.exists() shouldBe false
    }
}
