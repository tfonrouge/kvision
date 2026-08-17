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
                api(project(":kvision-modules:kvision-state-flow"))
                api(libs.ballast.core)
                api(libs.ballast.saved.state)
                api(libs.ballast.repository)
                api(libs.ballast.sync)
                api(libs.ballast.undo)
            }
        }
        getByName("jsTest") {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

setupDokka(tasks.dokkaGeneratePublicationHtml)
setupPublication()
