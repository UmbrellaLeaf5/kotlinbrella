description = "Spring MVC errors and request context"

dependencies {
  api(project(":kotlinbrella-spring-boot-autoconfigure"))
  api(platform("org.springframework.boot:spring-boot-dependencies:${rootProject.extra["bootVersion"]}"))
  api("org.springframework.boot:spring-boot-starter-webmvc")
  api("org.springframework.boot:spring-boot-starter-validation")
  api("com.fasterxml.jackson.module:jackson-module-kotlin")
  api("org.jetbrains.kotlin:kotlin-reflect")
}
