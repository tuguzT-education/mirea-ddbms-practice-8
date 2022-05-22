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
    implementation(compose.desktop.currentOs)
    implementation("org.slf4j:slf4j-simple:1.7.30")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
    implementation("io.github.microutils:kotlin-logging-jvm:2.1.20")
    implementation("org.litote.kmongo:kmongo-coroutine-serialization:4.6.0")
    implementation("org.testcontainers:testcontainers:1.16.3")
    implementation("com.github.tkuenneth:nativeparameterstoreaccess:0.1.2")

    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-debug:1.4.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
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
