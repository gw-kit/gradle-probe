package io.github.gwkit.gradleprobe.resources

import java.io.File

/**
 * Gets a file from the classpath resources.
 *
 * @param filePath The relative path to the resource file.
 * @return The file from resources.
 */
internal inline fun <reified T> getResourceFile(filePath: String): File {
    return T::class.java.classLoader
        .getResource(filePath)?.file
        ?.let(::File)
        ?: error("Resource not found: $filePath")
}

/**
 * Copies a directory from resources to the target directory.
 *
 * @param dirToCopy The relative path to the directory in resources.
 * @param destDir The destination directory name. Defaults to [dirToCopy].
 * @return The target directory.
 */
internal inline fun <reified T> File.copyDirFromResources(
    dirToCopy: String,
    destDir: String = dirToCopy,
): File {
    val target = resolve(destDir)
    getResourceFile<T>(dirToCopy).copyRecursively(target, true)
    return target
}

/**
 * Converts the file path to a Unix-style absolute path.
 */
internal fun File.toUnixAbsolutePath(): String = absolutePath.replace("\\", "/")
