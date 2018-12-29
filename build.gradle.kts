import org.jetbrains.kotlin.gradle.dsl.KotlinCompile

plugins {
    idea
    java
    kotlin("jvm") version "1.3.10"
    id("com.github.johnrengelman.shadow") version "4.0.3"
    id("com.gradle.build-scan") version "2.1"
}

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
    publishAlways()
}

group = "io.imulab.x"
version = "0.1.0"

repositories {
    maven(url = "https://artifactory.imulab.io/artifactory/gradle-dev-local/")
    jcenter()
    mavenCentral()
}

tasks {
    compileKotlin {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
        kotlinOptions {
            jvmTarget = "1.8"
            suppressWarnings = true
            freeCompilerArgs = listOf()
        }
    }
    compileTestKotlin {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
        kotlinOptions {
            jvmTarget = "1.8"
            suppressWarnings = true
            freeCompilerArgs = listOf()
        }
    }
    test {
        useJUnitPlatform {
            includeEngines("junit-jupiter", "spek2")
        }
    }
    shadowJar {
        baseName = "oauth-sdk"
        classifier = "all"
        version = project.version.toString()
    }
}

dependencies {
    implementation(platform("io.imulab.x:astrea-dependencies:2"))

    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")

    api("org.mindrot:jbcrypt")
    api("org.bitbucket.b_c:jose4j")

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    testImplementation("org.spekframework.spek2:spek-dsl-jvm") {
        exclude(group = "org.jetbrains.kotlin")
    }
    testImplementation("org.spekframework.spek2:spek-runner-junit5") {
        exclude(group = "org.jetbrains.kotlin")
        exclude(group = "org.junit.platform")
    }
    testRuntimeOnly(kotlin("reflect"))
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin")
    testImplementation("org.assertj:assertj-core")
}
