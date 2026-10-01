description = "Neutral Jakarta Bean Validation constraints for patch requests"

val bootBom = rootProject.extra["bootBom"] as String

dependencies {
  api(project(":kotlinbrella-core"))
  api(platform(bootBom))
  api("org.springframework.boot:spring-boot-starter-validation")
}
