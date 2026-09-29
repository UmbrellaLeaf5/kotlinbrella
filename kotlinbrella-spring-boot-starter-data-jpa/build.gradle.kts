description = "Spring Data JPA repository helpers and persistence errors"

dependencies {
  api(project(":kotlinbrella-spring-boot-autoconfigure"))
  api(platform("org.springframework.boot:spring-boot-dependencies:${rootProject.extra["bootVersion"]}"))
  api("org.springframework.boot:spring-boot-starter-data-jpa")
  api("org.springframework.boot:spring-boot-starter-liquibase")
  runtimeOnly("org.postgresql:postgresql")
}
