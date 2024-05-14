plugins {
    kotlin("jvm") version "1.9.0"
    id("io.qameta.allure") version "2.11.2"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("io.rest-assured:rest-assured:3.0.0")
    implementation("io.rest-assured:json-schema-validator:2.13.3")
    implementation("org.assertj:assertj-core:3.24.2")
    implementation("io.qameta.allure:allure-rest-assured:2.17.2")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    testImplementation("io.qameta.allure:allure-rest-assured:2.17.2")
    testImplementation("io.qameta.allure:allure-junit5:2.17.2")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.1")

}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

allure {
    version.set("2.17.2")
    adapter {
        autoconfigure.set(true)
        aspectjWeaver.set(true)
    }
}
