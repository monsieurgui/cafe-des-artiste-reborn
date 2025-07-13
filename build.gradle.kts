plugins {
  java
  `java-library`
  id("com.diffplug.spotless") version "6.25.0" apply false
  id("com.google.cloud.tools.jib") version "3.4.0" apply false
}

allprojects {
  group = "dev.cafe"
  version = "1.0.0-SNAPSHOT"

  repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://maven.lavalink.dev/releases")
  }
}

subprojects {
  apply(plugin = "java")
  apply(plugin = "java-library")
  apply(plugin = "jacoco")
  apply(plugin = "com.diffplug.spotless")

  java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
  }

  dependencies {
    implementation("org.slf4j:slf4j-api:2.0.9")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    testImplementation("org.mockito:mockito-core:5.8.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.8.0")
  }

  tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.named("jacocoTestReport"))
  }

  tasks.named<JacocoReport>("jacocoTestReport") {
    dependsOn(tasks.test)
  }

  configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    java {
      googleJavaFormat("1.19.1")
      removeUnusedImports()
      trimTrailingWhitespace()
      endWithNewline()
    }
  }
}