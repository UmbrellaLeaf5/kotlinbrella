description = "Spring AOP access and ownership checks"

dependencies {
  api(project(":kotlinbrella-spring-boot-autoconfigure"))
  api(platform("org.springframework.boot:spring-boot-dependencies:${rootProject.extra["bootVersion"]}"))
  api("org.springframework.boot:spring-boot-starter-aspectj")
  api("org.jetbrains.kotlin:kotlin-reflect")
}
