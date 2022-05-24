import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.compose") version "1.1.0"
    kotlin("plugin.serialization") version "1.6.10"
}

group = "io.github.tuguzt"
version = "0.0.1"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    // Compose for Desktop, UI
    implementation(compose.desktop.currentOs)
    implementation("com.github.tkuenneth:nativeparameterstoreaccess:0.1.2")

    // KotlinX
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")

    // Logging
    implementation("org.slf4j:slf4j-simple:1.7.36")
    implementation("io.github.microutils:kotlin-logging-jvm:2.1.21")

    // Docker Compose, KMongo
    implementation("org.testcontainers:testcontainers:1.17.1")
    implementation("org.litote.kmongo:kmongo-coroutine-serialization:4.6.0")

    // Koin
    implementation("io.insert-koin:koin-core:3.2.0")
    implementation("io.insert-koin:koin-logger-slf4j:3.2.0")

    // Testing
    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-debug:1.6.1")
}

tasks {
    test {
        useJUnitPlatform()
    }

    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
            freeCompilerArgs = listOf("-opt-in=kotlin.RequiresOptIn")
        }
    }
}

compose.desktop {
    application {
        mainClass = "io.github.tuguzt.ddbms.practice8.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "DDBMS-Practice-8"
            packageVersion = "1.0.0"
        }
    }
}
