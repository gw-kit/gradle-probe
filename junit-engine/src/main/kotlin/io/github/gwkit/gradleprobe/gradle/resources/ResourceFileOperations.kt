package io.github.gwkit.gradleprobe.gradle.resources

import java.io.File
import java.net.URL
import kotlin.reflect.KClass

internal fun KClass<out Any>.getResourceFile(filePath: String): File {
    val url: URL = java.classLoader
        .getResource(filePath)
        ?: error("Resource file $filePath not found")
    return File(url.file)
}

internal fun File.copyDirFromResources(
    sourceClass: KClass<out Any>,
    dirToCopy: String,
    destDir: String = dirToCopy,
): File {
    val target: File = resolve(destDir)
    sourceClass.getResourceFile(dirToCopy)
        .copyRecursively(target, true)
    return target
}

internal fun File.toUnixAbsolutePath(): String = absolutePath.replace("\\", "/")
