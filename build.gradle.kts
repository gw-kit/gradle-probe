import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    alias(libs.plugins.kotlinJvm)
    `jvm-test-suite`
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.junitApi)
}

val targetJvmVersion = JavaLanguageVersion.of(17)
kotlin {
    jvmToolchain {
        languageVersion.set(targetJvmVersion)
    }
    compilerOptions {
        apiVersion = KotlinVersion.KOTLIN_1_9
        languageVersion = KotlinVersion.KOTLIN_1_9
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
