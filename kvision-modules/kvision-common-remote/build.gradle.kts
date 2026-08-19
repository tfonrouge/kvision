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
                api(libs.kotlinx.coroutines.core)
                api(libs.kotlinx.serialization.json)
                api(project(":kvision-modules:kvision-common-types"))
                api(libs.kilua.rpc.core)
            }
        }
        getByName("jsMain") {
            dependencies {
            }
        }
        getByName("jsTest") {
            dependencies {
                implementation(kotlin("test-js"))
                implementation(project(":kvision-modules:kvision-testutils"))
            }
        }
        getByName("jvmMain") {
            dependencies {
            }
        }
        getByName("jvmTest") {
            dependencies {
            }
        }
    }
}

setupDokka()
setupPublication()
