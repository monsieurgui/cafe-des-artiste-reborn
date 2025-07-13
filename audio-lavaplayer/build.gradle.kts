dependencies {
  api(project(":audio-common"))
  
  implementation("dev.arbjerg:lavaplayer:2.2.4")
  implementation("dev.arbjerg.lavalink:youtube-plugin:1.7.1")
  implementation("com.google.dagger:dagger:2.48.1")
  annotationProcessor("com.google.dagger:dagger-compiler:2.48.1")
}