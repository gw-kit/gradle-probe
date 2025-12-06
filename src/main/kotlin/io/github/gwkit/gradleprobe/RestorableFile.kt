package io.github.gwkit.gradleprobe

import java.io.File
import java.io.IOException

/**
 * Wraps a file that can be restored to its original content.
 * Useful when the test modifies a file and needs to restore it to its original state.
 *
 * @property file The file that can be modified during tests.
 */
class RestorableFile(
    private val originFileCopy: File,
    val file: File,
) {

    /**
     * Restores the file to its original content.
     */
    fun restoreOriginContent() {
        originFileCopy.copyTo(file, overwrite = true)
    }
}
