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
    sourceSets {
        getByName("jsMain") {
            dependencies {
                api(project(":kvision"))
                api(libs.ballast.core)
                api(libs.ballast.navigation)
            }
        }
        getByName("jsTest") {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

setupDokka()
setupPublication()
