import groovy.lang.GroovyObject
import org.gradle.internal.impldep.org.apache.maven.Maven
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile
import org.jfrog.gradle.plugin.artifactory.dsl.PublisherConfig

plugins {
    idea
    java
    `java-library`
    `maven-publish`
    kotlin("jvm") version "1.3.10"
    id("com.jfrog.artifactory") version "4.8.1"
    id("com.gradle.build-scan") version "2.1"
}

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
    publishAlways()
}

group = "io.imulab.x"
version = "0.1.2"

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
}

publishing {
    publications {
        create<MavenPublication>("oauth-sdk") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
            from(components["java"])
            pom {
                name.set(artifactId)
                developers {
                    developer {
                        id.set("imulab")
                        name.set("Weinan Qiu")
                        email.set("davidiamyou@gmail.com")
                    }
                }
            }
        }
    }
}

artifactory {
    setContextUrl(System.getenv("ARTIFACTORY_CONTEXT_URL") ?: "http://artifactory.imulab.io/artifactory")
    publish(delegateClosureOf<PublisherConfig> {
        repository(delegateClosureOf<GroovyObject> {
            setProperty("repoKey", System.getenv("ARTIFACTORY_REPO") ?: "gradle-dev-local")
            setProperty("username", System.getenv("ARTIFACTORY_USERNAME") ?: "imulab")
            setProperty("password", System.getenv("ARTIFACTORY_PASSWORD") ?: "changeme")
            setProperty("maven", true)
        })
        defaults(delegateClosureOf<GroovyObject> {
            invokeMethod("publications", "oauth-sdk")
        })
    })
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
