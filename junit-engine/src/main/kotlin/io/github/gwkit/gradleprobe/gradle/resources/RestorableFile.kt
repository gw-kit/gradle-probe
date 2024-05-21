package io.github.gwkit.gradleprobe.gradle.resources

import java.io.File
import java.io.IOException

/**
 * Represents a file that can be restored to its original content.
 * The file is created by copying the content of the origin file.
 *
 * @param originFileCopy The origin file copy.
 * @param file The file to restore.
 *
 */
class RestorableFile(private val originFileCopy: File, val file: File) {

    @Throws(IOException::class)
    fun restoreOriginContent() {
        originFileCopy.copyTo(file, overwrite = true)
    }
}
