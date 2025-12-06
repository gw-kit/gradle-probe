import io.github.surpsg.deltacoverage.gradle.CoverageEngine
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.deltaCoverage)
    alias(libs.plugins.detekt)
    `jvm-test-suite`
    `java-gradle-plugin`
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(gradleTestKit())
    implementation(libs.junitApi)
    implementation(libs.kotlinReflect)
    implementation(libs.kotestAssertions)

    testImplementation(libs.junitApi)
    testImplementation(gradleTestKit())
}

val targetJvmVersion = JavaLanguageVersion.of(17)
kotlin {
    jvmToolchain {
        languageVersion.set(targetJvmVersion)
    }
    compilerOptions {
        apiVersion = KotlinVersion.KOTLIN_1_9
        languageVersion = KotlinVersion.KOTLIN_1_9
        freeCompilerArgs.add("-Xskip-metadata-version-check")
    }
}

java {
    toolchain {
        languageVersion.set(targetJvmVersion)
    }
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter(libs.versions.junitVer)
            dependencies {
                implementation(libs.mockk)
                implementation(libs.kotestAssertions)
            }
            targets.all {
                testTask.configure {
                    outputs.apply {
                        upToDateWhen { false }
                        cacheIf { false }
                    }

                    systemProperties(
                        "junit.jupiter.execution.parallel.enabled" to true,
                        "junit.jupiter.execution.parallel.config.strategy" to "dynamic",
                        "junit.jupiter.execution.parallel.config.dynamic.factor" to "0.9",
                        "junit.jupiter.execution.parallel.mode.default" to "concurrent",
                        "junit.jupiter.execution.parallel.mode.classes.default" to "concurrent",
                        "junit.jupiter.execution.timeout.default" to "10 s",
                        "mockk.junit.extension.requireParallelTesting" to "true",
                    )

                    testLogging {
                        events(TestLogEvent.SKIPPED, TestLogEvent.FAILED, TestLogEvent.PASSED)
                        showStandardStreams = true
                    }
                }
            }
        }
    }
}

configure<io.github.surpsg.deltacoverage.gradle.DeltaCoverageConfiguration> {
    coverage.engine = CoverageEngine.INTELLIJ

    diffSource.git.compareWith("refs/remotes/origin/main")

    reports {
        html.set(true)
    }

    reportViews {
        val test by getting {
            violationRules.failIfCoverageLessThan(0.8)
        }
    }
}

detekt {
    buildUponDefaultConfig = true
    parallel = true
}
