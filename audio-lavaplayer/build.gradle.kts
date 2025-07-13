dependencies {
  api(project(":audio-common"))
  implementation(project(":bot-core"))  // For AudioController
  
  implementation("dev.arbjerg:lavaplayer:2.2.3")
  implementation("net.dv8tion:JDA:5.5.1")  // For JDA and AudioSendHandler
  implementation("dev.lavalink.youtube:v2:1.8.0")  // YouTube plugin for enhanced search
  implementation("com.google.dagger:dagger:2.48.1")
  annotationProcessor("com.google.dagger:dagger-compiler:2.48.1")
}