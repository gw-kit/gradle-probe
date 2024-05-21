package io.github.gwkit.gradleprobe.gradle.junit


@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class GradlePluginTest(
    // resource project directory
    // kts/groovy (is needed?)
    // gradle version
    val resourceProjectDir: String,
    val gradleVersion: String = DEFAULT_GRADLE_VERSION,
) {
    companion object {
        const val DEFAULT_GRADLE_VERSION = "8.7.0"
    }
}
