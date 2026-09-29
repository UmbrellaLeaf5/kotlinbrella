description = "Minimal Servlet MVC sample without JPA, AOP or Springdoc"

plugins {
  id("org.springframework.boot")
  kotlin("plugin.spring")
}

dependencies {
  implementation(project(":kotlinbrella-spring-boot-starter-webmvc"))
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
}
