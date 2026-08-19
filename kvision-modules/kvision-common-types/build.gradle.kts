plugins {
    kotlin("multiplatform")
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.nmcp)
    id("maven-publish")
    id("signing")
    id("org.jetbrains.dokka")
}

kotlin {
    compilerOptions()
    kotlinJsTargets()
    kotlinJvmTargets()
    sourceSets {
        getByName("commonMain") {
            dependencies {
                api(libs.kotlinx.serialization.json)
            }
        }
        getByName("jsMain") {
            dependencies {
            }
        }
        getByName("jvmMain") {
            dependencies {
            }
        }
    }
}

setupDokka()
setupPublication()
