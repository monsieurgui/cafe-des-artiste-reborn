dependencies {
  api(project(":audio-common"))
  implementation(project(":infra-config"))
  
  implementation("com.google.dagger:dagger:2.48.1")
  annotationProcessor("com.google.dagger:dagger-compiler:2.48.1")
  implementation("io.github.resilience4j:resilience4j-circuitbreaker:2.2.0")
}