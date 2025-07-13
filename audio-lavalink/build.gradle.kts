dependencies {
  api(project(":audio-common"))
  
  implementation("dev.arbjerg.lavalink:protocol:4.0.7")
  implementation("com.squareup.okhttp3:okhttp:4.12.0")
  implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")
  implementation("com.google.dagger:dagger:2.48.1")
  annotationProcessor("com.google.dagger:dagger-compiler:2.48.1")
}