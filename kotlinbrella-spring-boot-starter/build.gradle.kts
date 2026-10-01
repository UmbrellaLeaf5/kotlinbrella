description = "Complete Kotlinbrella starter for Spring MVC applications"

val bootBom = rootProject.extra["bootBom"] as String

dependencies {
  api(project(":kotlinbrella-spring-boot-starter-webmvc"))
  api(project(":kotlinbrella-spring-boot-starter-validation"))
  api(project(":kotlinbrella-spring-boot-starter-data-jpa"))
  api(project(":kotlinbrella-spring-boot-starter-openapi"))
  api(project(":kotlinbrella-spring-boot-starter-access"))
  api(platform(bootBom))
  api("org.springframework.boot:spring-boot-starter-actuator")
  api("org.springframework.boot:spring-boot-starter-restclient")
  api("org.springframework.boot:spring-boot-starter-liquibase")
  runtimeOnly("org.postgresql:postgresql")
}
