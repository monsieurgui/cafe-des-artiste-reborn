dependencies {
  implementation(project(":audio-common"))
  implementation(project(":infra-config"))
  implementation("org.xerial:sqlite-jdbc:3.45.1.0")
  implementation("com.google.dagger:dagger:2.48.1")
  implementation("org.slf4j:slf4j-api:2.0.9")
  annotationProcessor("com.google.dagger:dagger-compiler:2.48.1")
} 