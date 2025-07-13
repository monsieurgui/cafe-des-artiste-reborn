plugins {
  application
  id("com.google.cloud.tools.jib")
}

dependencies {
  implementation(project(":bot-core"))
  implementation(project(":audio-lavaplayer"))
  implementation(project(":audio-lavalink"))
  implementation(project(":infra-config"))
  implementation(project(":infra-metrics"))
  
  implementation("net.dv8tion:JDA:5.5.1")
  implementation("com.google.dagger:dagger:2.48.1")
  annotationProcessor("com.google.dagger:dagger-compiler:2.48.1")
  implementation("ch.qos.logback:logback-classic:1.4.14")
}

application {
  mainClass.set("dev.cafe.bot.Main")
}

jib {
  from {
    image = "gcr.io/distroless/java21-debian12"
  }
  to {
    image = "cafe-des-artistes"
    tags = setOf("latest", project.version.toString())
  }
  container {
    ports = listOf("8080")
    environment = mapOf(
      "JAVA_TOOL_OPTIONS" to "-XX:+UseZGC -XX:+UnlockExperimentalVMOptions"
    )
  }
}