description = "Spring MVC errors and request context"

val bootBom = rootProject.extra["bootBom"] as String

dependencies {
  api(project(":kotlinbrella-spring-boot-autoconfigure"))
  api(platform(bootBom))
  api("org.springframework.boot:spring-boot-starter-webmvc")
  api("org.springframework.boot:spring-boot-starter-validation")
  api("com.fasterxml.jackson.module:jackson-module-kotlin")
  api("org.jetbrains.kotlin:kotlin-reflect")
}
