import io.github.surpsg.deltacoverage.gradle.CoverageEngine
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.deltaCoverage)
    alias(libs.plugins.detekt)
    `jvm-test-suite`
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

                    systemProperty("junit.jupiter.execution.parallel.enabled", true)
                    systemProperty("junit.jupiter.execution.parallel.config.strategy", "dynamic")
                    systemProperty("junit.jupiter.execution.parallel.config.dynamic.factor", "0.9")
                    systemProperty("junit.jupiter.execution.parallel.mode.default", "concurrent")
                    systemProperty("junit.jupiter.execution.parallel.mode.classes.default", "concurrent")
                    systemProperty("junit.jupiter.execution.timeout.default", "10 s")
                    systemProperties(

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
            violationRules.failIfCoverageLessThan(0.9)
        }
    }
}

detekt {
    buildUponDefaultConfig = true
    parallel = true
}
