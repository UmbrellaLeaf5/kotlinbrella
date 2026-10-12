description = "Test contract helpers for Kotlinbrella consumers"

val bootBom = rootProject.extra["bootBom"] as String

dependencies {
  api(platform(bootBom))
  api(project(":kotlinbrella-core"))
  api("org.junit.jupiter:junit-jupiter-api")
  compileOnly("org.springframework:spring-test")
  compileOnly("com.fasterxml.jackson.core:jackson-databind")
  compileOnly("jakarta.servlet:jakarta.servlet-api")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.springframework.boot:spring-boot-starter-webmvc")
  testImplementation("com.fasterxml.jackson.core:jackson-databind")
}
