description = "Neutral Jakarta Bean Validation constraints for patch requests"

dependencies {
  api(project(":kotlinbrella-core"))
  api(platform("org.springframework.boot:spring-boot-dependencies:${rootProject.extra["bootVersion"]}"))
  api("org.springframework.boot:spring-boot-starter-validation")
}
