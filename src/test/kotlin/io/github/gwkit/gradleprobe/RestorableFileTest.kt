package io.github.gwkit.gradleprobe

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class RestorableFileTest {

    @TempDir
    lateinit var tempDir: File

    @Test
    fun `restoreOriginContent should restore file to original content`() {
        // GIVEN
        val originalContent = "original content"
        val modifiedContent = "modified content"

        val originFileCopy = tempDir.resolve("origin-copy.txt").apply {
            writeText(originalContent)
        }
        val fileToModify = tempDir.resolve("file-to-modify.txt").apply {
            writeText(originalContent)
        }
        val restorableFile = RestorableFile(originFileCopy = originFileCopy, file = fileToModify)

        fileToModify.writeText(modifiedContent)
        fileToModify.readText() shouldBe modifiedContent

        // WHEN
        restorableFile.restoreOriginContent()

        // THEN
        fileToModify.readText() shouldBe originalContent
    }

    @Test
    fun `file property should return the wrapped file`() {
        // GIVEN
        val originFileCopy = tempDir.resolve("origin.txt").apply { writeText("origin") }
        val wrappedFile = tempDir.resolve("wrapped.txt").apply { writeText("content") }

        // WHEN
        val restorableFile = RestorableFile(originFileCopy = originFileCopy, file = wrappedFile)

        // THEN
        restorableFile.file shouldBe wrappedFile
    }

    @Test
    fun `restoreOriginContent should overwrite existing content`() {
        // GIVEN
        val originalContent = "line1\nline2\nline3"
        val modifiedContent = "completely different content with more lines\nand more\nand even more"

        val originFileCopy = tempDir.resolve("backup.txt").apply {
            writeText(originalContent)
        }
        val fileToModify = tempDir.resolve("target.txt").apply {
            writeText(originalContent)
        }
        val restorableFile = RestorableFile(originFileCopy = originFileCopy, file = fileToModify)

        fileToModify.writeText(modifiedContent)

        // WHEN
        restorableFile.restoreOriginContent()

        // THEN
        fileToModify.readText() shouldBe originalContent
    }
}
