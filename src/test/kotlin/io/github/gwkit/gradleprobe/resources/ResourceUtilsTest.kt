package io.github.gwkit.gradleprobe.resources

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.file.shouldExist
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class ResourceUtilsTest {

    @TempDir
    lateinit var tempDir: File

    @Test
    fun `getResourceFile should return file from classpath resources`() {
        // GIVEN
        val resourcePath = "test-project/build.gradle.kts"

        // WHEN
        val file = getResourceFile<ResourceUtilsTest>(resourcePath)

        // THEN
        file.shouldExist()
        file.name shouldBe "build.gradle.kts"
    }

    @Test
    fun `getResourceFile should throw error when resource not found`() {
        // GIVEN
        val nonExistentPath = "non-existent-resource.txt"

        // WHEN / THEN
        val exception = shouldThrow<IllegalStateException> {
            getResourceFile<ResourceUtilsTest>(nonExistentPath)
        }
        exception.message shouldContain "Resource not found"
        exception.message shouldContain nonExistentPath
    }

    @Test
    fun `copyDirFromResources should copy directory to target location`() {
        // GIVEN
        val sourceDir = "test-project"

        // WHEN
        val copiedDir = tempDir.copyDirFromResources<ResourceUtilsTest>(sourceDir)

        // THEN
        copiedDir.shouldExist()
        copiedDir.resolve("build.gradle.kts").shouldExist()
        copiedDir.resolve("build.gradle").shouldExist()
        copiedDir.resolve("settings.gradle.kts").shouldExist()
        copiedDir.resolve("src/main/java/Sample.java").shouldExist()
    }

    @Test
    fun `copyDirFromResources should use custom destination name`() {
        // GIVEN
        val sourceDir = "test-project"
        val customDestDir = "custom-project-name"

        // WHEN
        val copiedDir = tempDir.copyDirFromResources<ResourceUtilsTest>(sourceDir, customDestDir)

        // THEN
        copiedDir.shouldExist()
        copiedDir.name shouldBe customDestDir
        copiedDir.resolve("build.gradle.kts").shouldExist()
    }

    @Test
    fun `copyDirFromResources should preserve file content`() {
        // GIVEN
        val sourceDir = "test-project"

        // WHEN
        val copiedDir = tempDir.copyDirFromResources<ResourceUtilsTest>(sourceDir)

        // THEN
        val buildFile = copiedDir.resolve("build.gradle.kts")
        buildFile.readText() shouldContain "plugins"
        buildFile.readText() shouldContain "java"
    }

    @Test
    fun `toUnixAbsolutePath should convert backslashes to forward slashes`() {
        // GIVEN
        val file = File("/some/path/to/file.txt")

        // WHEN
        val unixPath = file.toUnixAbsolutePath()

        // THEN
        unixPath shouldNotContain "\\"
        unixPath shouldContain "/"
    }

    @Test
    fun `toUnixAbsolutePath should return absolute path`() {
        // GIVEN
        val file = tempDir.resolve("test-file.txt")

        // WHEN
        val unixPath = file.toUnixAbsolutePath()

        // THEN
        unixPath shouldBe file.absolutePath.replace("\\", "/")
    }
}