description = "Conditional Spring Boot configurations for Kotlinbrella"

dependencies {
  api(project(":kotlinbrella-core"))
  api(platform("org.springframework.boot:spring-boot-dependencies:${rootProject.extra["bootVersion"]}"))
  compileOnly("org.springframework.boot:spring-boot-autoconfigure")
  compileOnly("org.springframework.boot:spring-boot-starter-webmvc")
  compileOnly("org.springframework.boot:spring-boot-starter-validation")
  testImplementation("org.springframework.boot:spring-boot-starter-webmvc")
  testImplementation("org.springframework.boot:spring-boot-starter-validation")
  testImplementation("org.springframework:spring-test")
  testImplementation("org.springframework.boot:spring-boot-test")
  testImplementation("org.assertj:assertj-core")
  testImplementation("ch.qos.logback:logback-classic")
}
