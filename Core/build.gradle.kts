import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id("com.android.library")
    id("kotlinx-serialization")
    kotlin("multiplatform")
}

android {
    compileSdkVersion(28)
    defaultConfig {
        minSdkVersion(26)
        targetSdkVersion(28)
    }
    sourceSets {
        getByName("main") {
            manifest.srcFile("src/main/AndroidManifest.xml")
            java.srcDirs(file("src/main/kotlin"))
        }
    }
}

kotlin {
    //select iOS target platform depending on the Xcode environment variables
    val iOSTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget =
        if (System.getenv("SDK_NAME")?.startsWith("iphoneos") == true)
            ::iosArm64
        else
            ::iosX64

    iOSTarget("ios") {
        binaries {
            framework {
                baseName = "Core"
            }
        }
    }

    android("android")

    sourceSets["commonMain"].dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-common")
        implementation("io.ktor:ktor-client-core:${rootProject.ext["ktor_version"]}")
        implementation("io.ktor:ktor-client-json-native:${rootProject.ext["ktor_version"]}")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:${rootProject.ext["serializer_version"]}")
        implementation("com.soywiz.korlibs.klock:klock:1.11.3")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:${rootProject.ext["coroutines_version"]}")
    }

    sourceSets["androidMain"].dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib")
        api("com.android.support:appcompat-v7:${rootProject.ext["supportLibVersion"]}")
        api("com.android.support:support-media-compat:${rootProject.ext["supportLibVersion"]}")
        implementation("io.ktor:ktor-client-android:${rootProject.ext["ktor_version"]}")
        implementation("io.ktor:ktor-client-gson:${rootProject.ext["ktor_version"]}")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:${rootProject.ext["serializer_version"]}")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${rootProject.ext["coroutines_version"]}")
    }

    sourceSets["iosMain"].dependencies {
        implementation("io.ktor:ktor-client-ios:${rootProject.ext["ktor_version"]}")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-native:${rootProject.ext["serializer_version"]}")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-native:${rootProject.ext["coroutines_version"]}")
    }
}

val packForXcode by tasks.creating(Sync::class) {
    group = "build"

    //selecting the right configuration for the iOS framework depending on the Xcode environment variables
    val mode = System.getenv("CONFIGURATION") ?: "DEBUG"
    val framework = kotlin.targets.getByName<KotlinNativeTarget>("ios").binaries.getFramework(mode)

    inputs.property("mode", mode)
    dependsOn(framework.linkTask)

    val targetDir = File(buildDir, "xcode-frameworks")
    from({ framework.outputDirectory })
    into(targetDir)

    doLast {
        val gradlew = File(targetDir, "gradlew")
        gradlew.writeText("#!/bin/bash\nexport 'JAVA_HOME=${System.getProperty("java.home")}'\ncd '${rootProject.rootDir}'\n./gradlew \$@\n")
        gradlew.setExecutable(true)
    }
}

tasks.getByName("build").dependsOn(packForXcode)
