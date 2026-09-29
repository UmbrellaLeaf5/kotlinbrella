description = "Complete Kotlinbrella starter for Spring MVC applications"

dependencies {
  api(project(":kotlinbrella-spring-boot-starter-webmvc"))
  api(project(":kotlinbrella-spring-boot-starter-validation"))
  api(project(":kotlinbrella-spring-boot-starter-data-jpa"))
  api(project(":kotlinbrella-spring-boot-starter-openapi"))
  api(project(":kotlinbrella-spring-boot-starter-access"))
  api(platform("org.springframework.boot:spring-boot-dependencies:${rootProject.extra["bootVersion"]}"))
  api("org.springframework.boot:spring-boot-starter-actuator")
  api("org.springframework.boot:spring-boot-starter-restclient")
  api("org.springframework.boot:spring-boot-starter-liquibase")
  runtimeOnly("org.postgresql:postgresql")
}
