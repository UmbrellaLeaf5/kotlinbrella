description = "Conditional Spring Boot configurations for Kotlinbrella"

dependencies {
  api(project(":kotlinbrella-core"))
  api(platform("org.springframework.boot:spring-boot-dependencies:${rootProject.extra["bootVersion"]}"))
  compileOnly("org.springframework.boot:spring-boot-autoconfigure")
}
