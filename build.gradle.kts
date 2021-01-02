import com.jfrog.bintray.gradle.BintrayExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    //java
    //kotlin("") version Config.Versions.kotlin
    kotlin("multiplatform") version Config.Versions.kotlin
    id ("com.github.hierynomus.license") version "0.15.0"
    `maven-publish`
    maven
    id ("com.jfrog.bintray") version "1.8.0"
    id("org.jetbrains.dokka") version "0.9.17"
}

group = Config.ProjectData.group
version = Config.ProjectData.Dynamics.version//+"-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}


kotlin {
    /* Targets configuration omitted.
    *  To find out how to configure the targets, please follow the link:
    *  https://kotlinlang.org/docs/reference/building-mpp-with-gradle.html#setting-up-targets */
    js{
        browser()
        //nodejs()
    }
    jvm().compilations["main"].defaultSourceSet {
        dependencies {
            implementation(kotlin("stdlib-jdk8"))
            implementation(kotlin("reflect"))
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Config.Versions.coroutines}")
        }
    }
    // JVM-specific tests and their dependencies:
    jvm().compilations["test"].defaultSourceSet {
        dependencies {
            implementation(kotlin("test-junit"))
        }
    }

    js().compilations["main"].defaultSourceSet  {
        dependencies {
            //implementation(kotlin("js"))
            implementation(kotlin("reflect"))
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:${Config.Versions.coroutines}")
        }
        /* ... */
    }
    js().compilations["test"].defaultSourceSet {
        dependencies {
            implementation(kotlin("test-js"))
            implementation(kotlin("test-js-runner"))
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation(kotlin("reflect"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Config.Versions.coroutines}")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation(project(":mpp-test"))
            }
        }
    }
}

apply(from = "../publish.mpp.gradle.kts")