plugins {
    id("buildlogic.kotlin-common-conventions")
}

dependencies {
    implementation(libs.junit)
    implementation(libs.kotlinReflect)
    implementation(gradleTestKit())
}

@Suppress("UnstableApiUsage")
testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
        }
    }
}
