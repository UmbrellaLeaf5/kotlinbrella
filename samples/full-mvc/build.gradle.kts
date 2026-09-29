description = "Runnable Kotlin MVC sample of the aggregate starter"

plugins {
  id("org.springframework.boot")
  kotlin("plugin.jpa")
  kotlin("plugin.spring")
}

dependencies {
  implementation(project(":kotlinbrella-spring-boot-starter"))
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
  testImplementation("org.testcontainers:postgresql:${rootProject.extra["testcontainersVersion"]}")
  testImplementation("org.testcontainers:junit-jupiter:${rootProject.extra["testcontainersVersion"]}")
}
